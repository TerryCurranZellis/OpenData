/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.plugin;

import com.towermarsh.opendata.plugin.PluginLogContext;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
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
 * @since 2.1.0
 * @version 4.1.0
 */
public final class PluginExecutionCoordinator {
    private static final Logger LOGGER = Logger.getLogger(PluginExecutionCoordinator.class.getName());

    private PluginFactory pluginFactory;
    private PluginRunAudit audit;
    private DatabaseResourceManager database;
    private Clock clock;
    private Duration shutdownTimeout;
    private final PluginExceptionHandler exceptionHandler;

    /**
     * Creates a coordinator with its execution services.
     *
     * @param pluginFactory creates configured plugin instances
     * @param audit records plugin execution results
     * @param database database resources passed to plugins
     * @param clock clock used for execution timestamps
     * @param shutdownTimeout maximum wait for executor shutdown
     */
    public PluginExecutionCoordinator(
            PluginFactory pluginFactory,
            PluginRunAudit audit,
            DatabaseResourceManager database,
            Clock clock,
            Duration shutdownTimeout) {
        this.pluginFactory = Objects.requireNonNull(pluginFactory, "pluginFactory");
        this.audit = Objects.requireNonNull(audit, "audit");
        this.database = Objects.requireNonNull(database, "database");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.shutdownTimeout = Objects.requireNonNull(shutdownTimeout, "shutdownTimeout");
        if (shutdownTimeout.isZero() || shutdownTimeout.isNegative()
                || shutdownTimeout.toMillis() == 0) {
            throw new IllegalArgumentException("shutdownTimeout must be at least one millisecond.");
        }
        this.exceptionHandler = new PluginExceptionHandler();
    }

    /**
     * Runs configured plugins concurrently up to the requested limit.
     *
     * @param plugins selected, configured plugins
     * @param requestedParallelism requested maximum number of workers
     * @param dryRun whether writes and run audit are disabled
     * @return results in the same order as the selected plugins
     * @throws InterruptedException if the calling thread is interrupted
     */
    public PluginExecutionSummary execute(
            List<ResolvedPlugin> plugins,
            int requestedParallelism,
            boolean dryRun) throws InterruptedException {
        var immutablePlugins = List.copyOf(Objects.requireNonNull(plugins, "plugins"));
        if (immutablePlugins.isEmpty()) {
            return new PluginExecutionSummary(List.of());
        }
        var parallelism = Math.max(1, Math.min(requestedParallelism, immutablePlugins.size()));
        var executor = Executors.newFixedThreadPool(parallelism, new PluginThreadFactory());
        try {
            var tasks = immutablePlugins.stream()
                    .map(plugin -> (Callable<PluginRunResult>) () -> executeOne(plugin, dryRun))
                    .toList();
            var futures = executor.invokeAll(tasks);
            var results = new ArrayList<PluginRunResult>(futures.size());
            for (int index = 0; index < futures.size(); index++) {
                try {
                    results.add(futures.get(index).get());
                } catch (ExecutionException exception) {
                    var plugin = immutablePlugins.get(index);
                    var now = clock.instant();
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
        } catch (InterruptedException exception) {
            // Cancel any still-running plugins before propagating interruption.
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw exception;
        } finally {
            executor.shutdown();
            if (!Thread.currentThread().isInterrupted()) {
                try {
                    if (!executor.awaitTermination(shutdownTimeout.toMillis(), TimeUnit.MILLISECONDS)) {
                        var cancelled = executor.shutdownNow().size();
                        LOGGER.log(Level.WARNING,
                                "Forced plugin executor shutdown; cancelled queued tasks: {0}", cancelled);
                    }
                } catch (InterruptedException exception) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                    throw exception;
                }
            }
        }
    }

    /**
     * Executes one plugin, reporting errors and recording audit details.
     *
     * @param resolved resolved plugin descriptor and configuration
     * @param dryRun whether this execution must avoid data writes
     * @return the plugin's execution result
     */
    @SuppressWarnings("try")
    private PluginRunResult executeOne(ResolvedPlugin resolved, boolean dryRun) {
        var pluginId = resolved.descriptor().id();
        var runId = UUID.randomUUID();
        var startedAt = clock.instant();
        PluginMetrics metrics = PluginMetrics.ZERO;
        @SuppressWarnings("UnusedAssignment")
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
            var interrupted = Thread.currentThread().isInterrupted();
            status = interrupted ? PluginRunStatus.CANCELLED : PluginRunStatus.FAILED;
            var failureMessage = PluginFailureReport.describe(exception);
            error = Optional.of(failureMessage);
            LOGGER.log(
                    interrupted ? Level.WARNING : Level.SEVERE,
                    (interrupted ? "Plugin execution was cancelled: plugin=" : "Plugin execution failed: plugin=")
                    + pluginId + ", runId=" + runId + "\n" + failureMessage,
                    exception);
        }

        var result = new PluginRunResult(
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
                                    + PluginFailureReport.describe(auditException)));
                }
            }
        }
        return result;
    }
}
