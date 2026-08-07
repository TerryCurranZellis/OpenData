/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import com.towermarsh.opendata.util.ExceptionMessages;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.logging.PluginLogContext;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runs each selected plugin as an isolated task on a bounded executor.
 *
 * @author Terry Curran
 * @version 2.1
 */
public final class PluginExecutionCoordinator {
    private static final Logger LOGGER = Logger.getLogger(PluginExecutionCoordinator.class.getName());

    private final PluginFactory pluginFactory;
    private final PluginRunAudit audit;
    private final DatabaseResourceManager database;
    private final Clock clock;
    private final Duration shutdownTimeout;
    private final PluginExceptionHandler exceptionHandler;

    /**
     *
     * @param pluginFactory
     * @param audit
     * @param database
     * @param clock
     * @param shutdownTimeout
     */
    public PluginExecutionCoordinator(
            final PluginFactory pluginFactory,
            final PluginRunAudit audit,
            final DatabaseResourceManager database,
            final Clock clock,
            final Duration shutdownTimeout) {
        this.pluginFactory = Objects.requireNonNull(pluginFactory, "pluginFactory");
        this.audit = Objects.requireNonNull(audit, "audit");
        this.database = Objects.requireNonNull(database, "database");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.shutdownTimeout = Objects.requireNonNull(shutdownTimeout, "shutdownTimeout");
        this.exceptionHandler = new PluginExceptionHandler();
    }

    /**
     *
     * @param plugins
     * @param requestedParallelism
     * @param dryRun
     * @return
     * @throws InterruptedException
     */
    public PluginExecutionSummary execute(
            final List<ResolvedPlugin> plugins,
            final int requestedParallelism,
            final boolean dryRun) throws InterruptedException {
        final List<ResolvedPlugin> immutablePlugins = List.copyOf(Objects.requireNonNull(plugins, "plugins"));
        if (immutablePlugins.isEmpty()) {
            return new PluginExecutionSummary(List.of());
        }
        final int parallelism = Math.max(1, Math.min(requestedParallelism, immutablePlugins.size()));
        final ExecutorService executor = Executors.newFixedThreadPool(parallelism, new PluginThreadFactory());
        try {
            final List<Callable<PluginRunResult>> tasks = immutablePlugins.stream()
                    .map(plugin -> (Callable<PluginRunResult>) () -> executeOne(plugin, dryRun))
                    .toList();
            final var futures = executor.invokeAll(tasks);
            final List<PluginRunResult> results = new ArrayList<>(futures.size());
            for (int index = 0; index < futures.size(); index++) {
                try {
                    results.add(futures.get(index).get());
                } catch (ExecutionException exception) {
                    final ResolvedPlugin plugin = immutablePlugins.get(index);
                    final Instant now = clock.instant();
                    results.add(new PluginRunResult(
                            plugin.descriptor().id(),
                            UUID.randomUUID(),
                            PluginRunStatus.FAILED,
                            now,
                            now,
                            PluginMetrics.ZERO,
                            Optional.ofNullable(exception.getCause()).map(Throwable::toString)));
                }
            }
            return new PluginExecutionSummary(results);
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(shutdownTimeout.toMillis(), TimeUnit.MILLISECONDS)) {
                final int cancelled = executor.shutdownNow().size();
                LOGGER.log(Level.WARNING, "Forced plugin executor shutdown; cancelled queued tasks: {0}", cancelled);
            }
        }
    }

    @SuppressWarnings("try")
    private PluginRunResult executeOne(final ResolvedPlugin resolved, final boolean dryRun) {
        final String pluginId = resolved.descriptor().id();
        final UUID runId = UUID.randomUUID();
        final Instant startedAt = clock.instant();
        PluginMetrics metrics = PluginMetrics.ZERO;
        PluginRunStatus status = dryRun ? PluginRunStatus.DRY_RUN : PluginRunStatus.SUCCESS;
        Optional<String> error = Optional.empty();
        boolean auditStarted = false;

        try (var ignored = PluginLogContext.open(pluginId, runId)) {
            if (!dryRun) {
                audit.started(runId, pluginId, Thread.currentThread().getName(), startedAt);
                auditStarted = true;
            }
            LOGGER.log(Level.INFO, "Starting plugin {0}.", pluginId);
            final OpenDataPlugin plugin = pluginFactory.create(resolved);
            metrics = exceptionHandler.execute(pluginId, plugin, new PluginExecutionContext(
                    runId,
                    resolved.descriptor(),
                    resolved.definition(),
                    database,
                    clock,
                    dryRun));
            status = dryRun ? PluginRunStatus.DRY_RUN : PluginRunStatus.SUCCESS;
            LOGGER.log(Level.INFO,
                    "Plugin {0} completed; read={1}, inserted={2}, updated={3}, skipped={4}",
                    new Object[]{pluginId, metrics.read(), metrics.inserted(), metrics.updated(), metrics.skipped()});
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            status = PluginRunStatus.CANCELLED;
            error = Optional.of("Plugin execution was interrupted.");
            LOGGER.log(Level.WARNING, "Plugin execution was interrupted: " + pluginId, exception);
        } catch (Exception exception) {
            final boolean interrupted = Thread.currentThread().isInterrupted();
            status = interrupted ? PluginRunStatus.CANCELLED : PluginRunStatus.FAILED;
            final String failureMessage = ExceptionMessages.rootCauseMessage(exception);
            error = Optional.of(failureMessage);
            LOGGER.log(
                    interrupted ? Level.WARNING : Level.SEVERE,
                    "{0}: plugin={1}, error={2}",
                    new Object[]{
                        interrupted ? "Plugin execution was cancelled" : "Plugin execution failed",
                        pluginId,
                        failureMessage
                    });
            LOGGER.log(Level.FINE, "Plugin execution failure details: " + pluginId, exception);
        }

        PluginRunResult result = new PluginRunResult(
                pluginId, runId, status, startedAt, clock.instant(), metrics, error);
        if (!dryRun && auditStarted) {
            try {
                audit.completed(result);
            } catch (RuntimeException auditException) {
                LOGGER.log(Level.SEVERE, "Unable to complete run audit for " + pluginId, auditException);
                if (result.successful()) {
                    result = new PluginRunResult(
                            pluginId,
                            runId,
                            PluginRunStatus.FAILED,
                            startedAt,
                            clock.instant(),
                            metrics,
                            Optional.of("Plugin succeeded but run-audit completion failed: "
                                    + auditException.getMessage()));
                }
            }
        }
        return result;
    }
}
