/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.config.ApplicationBootstrapPropertiesLoader;
import com.towermarsh.opendata.config.ApplicationRuntimeConfiguration;
import com.towermarsh.opendata.config.ClasspathConfigurationPropertiesSource;
import com.towermarsh.opendata.config.JdbcConfigurationPropertiesSource;
import com.towermarsh.opendata.config.OpenDataConfigurationException;
import com.towermarsh.opendata.config.PropertiesPluginDefinitionLoader;
import com.towermarsh.opendata.config.RsaConfigurationPasswordCipher;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.database.SQLServerResource;
import com.towermarsh.opendata.database.UnavailableDatabaseResourceManager;
import com.towermarsh.opendata.logging.LoggingManager;
import com.towermarsh.opendata.plugin.JdbcPluginRegistry;
import com.towermarsh.opendata.plugin.JdbcPluginRunAudit;
import com.towermarsh.opendata.plugin.NoOpPluginRunAudit;
import com.towermarsh.opendata.plugin.PluginExecutionCoordinator;
import com.towermarsh.opendata.plugin.PluginExecutionSummary;
import com.towermarsh.opendata.plugin.PluginRunAudit;
import com.towermarsh.opendata.plugin.PluginSelectionResolver;
import com.towermarsh.opendata.plugin.ReflectionPluginFactory;
import com.towermarsh.opendata.plugin.ResolvedPlugin;
import com.towermarsh.opendata.util.DurationFormatter;
import com.towermarsh.opendata.util.ExceptionMessages;
import java.io.IOException;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resource-owning adapter for JavaFX plugin execution and dry-run operations.
 *
 * <p>The gateway deliberately follows the same two-phase resource lifecycle as
 * the command-line runner. It first opens the bootstrap database to resolve the
 * persistent registry, runtime configuration and plugin definitions. That pool
 * is then closed before the runtime execution pool is opened. A dry run uses an
 * unavailable database resource and a no-op audit implementation so provider
 * writes and generic run-audit writes cannot occur.</p>
 *
 * <p>No JavaFX type crosses this boundary. The caller is responsible for
 * running {@link #execute(java.util.List, boolean)} away from the JavaFX
 * application thread.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class PluginExecutionGateway {

    private static final Logger LOGGER = Logger.getLogger(PluginExecutionGateway.class.getName());

    /**
     * Executes the explicitly selected registered plugins.
     *
     * @param pluginIds immutable snapshot of checked plugin identifiers
     * @param dryRun {@code true} to execute without provider database writes or
     * generic run-audit rows
     * @return aggregate execution result
     * @throws IOException when runtime logging cannot be configured
     * @throws InterruptedException when plugin execution is interrupted
     */
    public PluginExecutionSummary execute(
            final List<String> pluginIds,
            final boolean dryRun) throws IOException, InterruptedException {
        final var requested = List.copyOf(Objects.requireNonNull(pluginIds, "pluginIds"));
        if (requested.isEmpty()) {
            throw new IllegalArgumentException("At least one plugin must be selected.");
        }

        final var passwordCipher = new RsaConfigurationPasswordCipher();
        final var bootstrap = new ApplicationBootstrapPropertiesLoader(passwordCipher)
                .load(Map.of());

        final List<ResolvedPlugin> plugins;
        final ApplicationRuntimeConfiguration runtime;
        DatabaseResourceManager configurationDatabase = null;
        try {
            configurationDatabase = SQLServerResource.initialise(
                    bootstrap.toDatabasePoolConfiguration());
            final var registry = new JdbcPluginRegistry(configurationDatabase);
            final var propertiesSource = bootstrap.useDatabaseProperties()
                    ? new JdbcConfigurationPropertiesSource(configurationDatabase)
                    : new ClasspathConfigurationPropertiesSource();
            runtime = ApplicationRuntimeConfiguration.load(propertiesSource, Map.of());
            LoggingManager.configure(runtime.logging(), false);
            if (!dryRun && runtime.database().password().isBlank()) {
                throw new OpenDataConfigurationException(
                        "application.database.password must be supplied for a database-writing run.");
            }

            final var selected = new PluginSelectionResolver().resolve(requested, registry);
            final var definitionLoader = new PropertiesPluginDefinitionLoader(propertiesSource);
            plugins = selected.stream()
                    .map(descriptor -> new ResolvedPlugin(
                    descriptor,
                    definitionLoader.load(descriptor.id(), Map.of())))
                    .toList();
        } finally {
            closeDatabase(configurationDatabase);
        }

        final int parallelism = runtime.execution().maxParallelPlugins();
        LOGGER.log(Level.INFO,
                "GUI selected {0} registered plugin(s); parallelism={1}; dryRun={2}",
                new Object[]{plugins.size(), Math.min(parallelism, plugins.size()), dryRun});

        DatabaseResourceManager executionDatabase = null;
        try {
            final PluginRunAudit audit;
            if (dryRun) {
                executionDatabase = new UnavailableDatabaseResourceManager();
                audit = new NoOpPluginRunAudit();
            } else {
                executionDatabase = SQLServerResource.initialise(runtime.database());
                audit = new JdbcPluginRunAudit(executionDatabase);
            }

            final var coordinator = new PluginExecutionCoordinator(
                    new ReflectionPluginFactory(),
                    audit,
                    executionDatabase,
                    Clock.systemUTC(),
                    runtime.execution().shutdownTimeout());
            final var summary = coordinator.execute(plugins, parallelism, dryRun);
            logSummary(summary);
            return summary;
        } finally {
            closeDatabase(executionDatabase);
        }
    }

    private static void logSummary(final PluginExecutionSummary summary) {
        summary.results().forEach(result -> LOGGER.log(
                result.successful() ? Level.INFO : Level.SEVERE,
                "Plugin summary: id={0}, status={1}, duration={2}, read={3}, inserted={4}, "
                + "updated={5}, skipped={6}, error={7}",
                new Object[]{
                    result.pluginId(),
                    result.status().name(),
                    DurationFormatter.formatElapsed(result.duration()),
                    result.metrics().read(),
                    result.metrics().inserted(),
                    result.metrics().updated(),
                    result.metrics().skipped(),
                    result.errorMessage().orElse("")
                }));
        LOGGER.log(Level.INFO,
                "Plugin execution complete; selected={0}, succeeded={1}, failed={2}",
                new Object[]{summary.results().size(), summary.succeeded(), summary.failed()});
    }

    private static void closeDatabase(final DatabaseResourceManager database) {
        if (database == null) {
            return;
        }
        try {
            database.close();
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE,
                    "Database shutdown failed: {0}",
                    ExceptionMessages.rootCauseMessage(exception));
            LOGGER.log(Level.FINE, "Database shutdown failure details.", exception);
        }
    }
}
