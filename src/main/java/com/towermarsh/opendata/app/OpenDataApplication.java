/*
 * Filename: OpenDataApplication.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.app;

import com.towermarsh.opendata.cli.CommandLineArguments;
import com.towermarsh.opendata.cli.CommandLineArgumentsProcessor;
import com.towermarsh.opendata.config.ApplicationRuntimeConfiguration;
import com.towermarsh.opendata.config.OpenDataConfigurationException;
import com.towermarsh.opendata.config.OverrideConfiguration;
import com.towermarsh.opendata.config.PropertiesPluginDefinitionLoader;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.database.SQLServerResource;
import com.towermarsh.opendata.database.UnavailableDatabaseResourceManager;
import com.towermarsh.opendata.logging.LoggingManager;
import com.towermarsh.opendata.plugin.ClasspathPluginRegistry;
import com.towermarsh.opendata.plugin.JdbcPluginRunAudit;
import com.towermarsh.opendata.plugin.NoOpPluginRunAudit;
import com.towermarsh.opendata.plugin.PluginExecutionCoordinator;
import com.towermarsh.opendata.plugin.PluginExecutionSummary;
import com.towermarsh.opendata.plugin.PluginRunAudit;
import com.towermarsh.opendata.plugin.PluginSelectionResolver;
import com.towermarsh.opendata.plugin.ReflectionPluginFactory;
import com.towermarsh.opendata.plugin.ResolvedPlugin;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Clock;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Coordinates registry selection, configuration, pooled database access, and
 * plugin execution.
 */
public final class OpenDataApplication {

    private static final Logger LOGGER = Logger.getLogger(OpenDataApplication.class.getName());

    public ExecutionStatus start(
            final CommandLineArguments arguments,
            final CommandLineArgumentsProcessor processor) throws IOException, InterruptedException {
        final var registry = new ClasspathPluginRegistry();
        if (arguments.helpRequested()) {
            processor.printHelp(new PrintWriter(System.out, true));
            return ExecutionStatus.SUCCESS;
        }
        if (arguments.versionRequested()) {
            final String version = OpenDataApplication.class.getPackage().getImplementationVersion();
            System.out.println("OpenData " + (version == null ? "development" : version));
            return ExecutionStatus.SUCCESS;
        }
        if (arguments.listPluginsRequested()) {
            registry.list().forEach(plugin -> System.out.printf(
                    "%s\t%s\t%s%n",
                    plugin.id(),
                    plugin.enabled() ? "enabled" : "disabled",
                    plugin.displayName()));
            return ExecutionStatus.SUCCESS;
        }

        final var overrideConfiguration = OverrideConfiguration.load(arguments.overrideFile());
        final var runtime = ApplicationRuntimeConfiguration.load(overrideConfiguration.applicationValues());
        if (!arguments.dryRun() && runtime.database().password().isBlank()) {
            throw new OpenDataConfigurationException(
                    "application.database.password must be supplied for a database-writing run.");
        }
        LoggingManager.configure(runtime.logging(), arguments.verbose());

        final var selected = new PluginSelectionResolver().resolve(arguments, registry);
        final boolean multiPluginRun = selected.size() > 1;
        final var definitionLoader = new PropertiesPluginDefinitionLoader();
        final List<ResolvedPlugin> plugins = selected.stream()
                .map(descriptor -> new ResolvedPlugin(
                descriptor,
                definitionLoader.load(
                        descriptor.id(),
                        overrideConfiguration.pluginValues(descriptor.id(), multiPluginRun))))
                .toList();

        final int parallelism = arguments.parallelism().orElse(runtime.execution().maxParallelPlugins());
        LOGGER.log(Level.INFO,
                "Selected {0} plugin(s); parallelism={1}; dryRun={2}",
                new Object[]{plugins.size(), Math.min(parallelism, plugins.size()), arguments.dryRun()});

        DatabaseResourceManager database = null;
        try {
            final PluginRunAudit audit;
            if (arguments.dryRun()) {
                database = new UnavailableDatabaseResourceManager();
                audit = new NoOpPluginRunAudit();
            } else {
                database = SQLServerResource.initialise(runtime.database());
                audit = new JdbcPluginRunAudit(database);
            }
            final var coordinator = new PluginExecutionCoordinator(
                    new ReflectionPluginFactory(),
                    audit,
                    database,
                    Clock.systemUTC(),
                    runtime.execution().shutdownTimeout());
            final PluginExecutionSummary summary = coordinator.execute(
                    plugins, parallelism, arguments.dryRun());
            logSummary(summary);
            return summary.allSuccessful() ? ExecutionStatus.SUCCESS : ExecutionStatus.PLUGIN_FAILURE;
        } finally {
            closeDatabase(database);
        }
    }

    /**
     * Closes the database resource without allowing a shutdown failure to
     * replace the application's calculated execution status.
     *
     * @param database database resource, possibly null
     */
    private static void closeDatabase(
            final DatabaseResourceManager database) {
        if (database == null) {
            return;
        }
        try {
            database.close();
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Database shutdown failed: {0}", messageFor(exception));
            LOGGER.log(Level.FINE, "Database shutdown failure details.", exception);
        }
    }

    private static String messageFor(final Throwable exception) {
        var current = exception;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        final var message = current.getMessage();
        return message == null || message.isBlank()
                ? current.getClass().getSimpleName()
                : message;
    }

    private static void logSummary(final PluginExecutionSummary summary) {
        summary.results().forEach((var result) -> {
            LOGGER.log(
                    result.successful() ? Level.INFO : Level.SEVERE,
                    "Plugin summary: id={0}, status={1}, durationMs={2}, read={3}, inserted={4}, updated={5}, skipped={6}, error={7}",
                    new Object[]{
                        result.pluginId(),
                        result.status().name(),
                        result.duration().toMillis(),
                        result.metrics().read(),
                        result.metrics().inserted(),
                        result.metrics().updated(),
                        result.metrics().skipped(),
                        result.errorMessage().orElse("")
                    });
        });
        LOGGER.log(Level.INFO,
                "Plugin execution complete; selected={0}, succeeded={1}, failed={2}",
                new Object[]{summary.results().size(), summary.succeeded(), summary.failed()});
    }
}
