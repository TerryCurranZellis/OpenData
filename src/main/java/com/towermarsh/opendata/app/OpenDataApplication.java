\
/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.app;

import com.towermarsh.opendata.cli.CommandLineArguments;
import com.towermarsh.opendata.cli.CommandLineArgumentsProcessor;
import com.towermarsh.opendata.config.ApplicationBootstrapProperties;
import com.towermarsh.opendata.config.ApplicationBootstrapPropertiesLoader;
import com.towermarsh.opendata.config.ApplicationRuntimeConfiguration;
import com.towermarsh.opendata.config.ClasspathConfigurationPropertiesSource;
import com.towermarsh.opendata.config.ConfigurationPasswordCipher;
import com.towermarsh.opendata.config.ConfigurationRegistrationService;
import com.towermarsh.opendata.config.JdbcConfigurationPropertiesSource;
import com.towermarsh.opendata.config.OpenDataConfigurationException;
import com.towermarsh.opendata.config.PluginRegistration;
import com.towermarsh.opendata.config.PropertiesFileConfigurationPropertiesSource;
import com.towermarsh.opendata.config.PropertiesPluginDefinitionLoader;
import com.towermarsh.opendata.config.RsaConfigurationPasswordCipher;
import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.database.SQLServerResource;
import com.towermarsh.opendata.database.UnavailableDatabaseResourceManager;
import com.towermarsh.opendata.logging.LoggingManager;
import com.towermarsh.opendata.plugin.ClasspathPluginRegistry;
import com.towermarsh.opendata.plugin.JdbcPluginRegistry;
import com.towermarsh.opendata.plugin.JdbcPluginRunAudit;
import com.towermarsh.opendata.plugin.NoOpPluginRunAudit;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginDescriptor;
import com.towermarsh.opendata.plugin.PluginExecutionCoordinator;
import com.towermarsh.opendata.plugin.PluginExecutionSummary;
import com.towermarsh.opendata.plugin.PluginRegistryException;
import com.towermarsh.opendata.plugin.PluginRunAudit;
import com.towermarsh.opendata.plugin.PluginSelectionResolver;
import com.towermarsh.opendata.plugin.ReflectionPluginFactory;
import com.towermarsh.opendata.plugin.ResolvedPlugin;
import com.towermarsh.opendata.util.DurationFormatter;
import com.towermarsh.opendata.util.ExceptionMessages;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Coordinates plugin administration, registry selection, configuration, pooled
 * database access, and plugin execution.
 *
 * @author Terry Curran
 * @version 2.1
 */
public final class OpenDataApplication {

    /**
     * default logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(OpenDataApplication.class.getName());

    /**
     * Starts the OpenData application for one parsed command line.
     *
     * @param arguments command-line arguments
     * @param processor command-line processor
     * @return execution status
     * @throws IOException on I/O failure
     * @throws InterruptedException when concurrent execution is interrupted
     */
    public ExecutionStatus start(
            final CommandLineArguments arguments,
            final CommandLineArgumentsProcessor processor)
            throws IOException, InterruptedException {
        if (arguments.helpRequested()) {
            processor.printHelp(new PrintWriter(System.out, true, StandardCharsets.UTF_8));
            return ExecutionStatus.SUCCESS;
        }

        // read the password for the database
        final ConfigurationPasswordCipher passwordCipher = new RsaConfigurationPasswordCipher();
        final var bootstrapLoader = new ApplicationBootstrapPropertiesLoader(passwordCipher);
        final var bootstrap = bootstrapLoader.load(Map.of());
        requireDatabasePassword(bootstrap, arguments);

        DatabaseResourceManager configurationDatabase = null;
        try {
            configurationDatabase = SQLServerResource.initialise(
                    bootstrap.toDatabasePoolConfiguration());
            final var registeredPlugins = new JdbcPluginRegistry(configurationDatabase);

            // decide what to do
            if (arguments.listPluginsRequested()) {
                printRegisteredPlugins(registeredPlugins);
                return ExecutionStatus.SUCCESS;
            }
            if (arguments.detailRequested()) {
                printPluginConfiguration(arguments, configurationDatabase, registeredPlugins);
                return ExecutionStatus.SUCCESS;
            }
            if (arguments.registerRequested()) {
                registerPlugins(
                        arguments,
                        bootstrap,
                        bootstrapLoader,
                        passwordCipher,
                        configurationDatabase,
                        registeredPlugins);
                return ExecutionStatus.SUCCESS;
            }
            if (arguments.unregisterRequested()) {
                administerSelected(arguments, registeredPlugins, AdministrationAction.UNREGISTER);
                return ExecutionStatus.SUCCESS;
            }
            if (arguments.enableRequested()) {
                administerSelected(arguments, registeredPlugins, AdministrationAction.ENABLE);
                return ExecutionStatus.SUCCESS;
            }
            if (arguments.disableRequested()) {
                administerSelected(arguments, registeredPlugins, AdministrationAction.DISABLE);
                return ExecutionStatus.SUCCESS;
            }

            return runPlugins(arguments, bootstrap, configurationDatabase, registeredPlugins);
        } finally {
            closeDatabase(configurationDatabase);
        }
    }

    /**
     * Display the stored configuration for one selected registered plugin.
     *
     * @param arguments command-line arguments
     * @param database configuration database
     * @param registry registered plugin registry
     */
    private static void printPluginConfiguration(
            final CommandLineArguments arguments,
            final DatabaseResourceManager database,
            final JdbcPluginRegistry registry) {
        final var pluginId = arguments.pluginIds().get(0);
        final var plugin = registry.find(pluginId)
                .orElseThrow(() -> new PluginRegistryException(
                "Registered plugin was not found: " + pluginId));

        final var properties = new JdbcConfigurationPropertiesSource(database)
                .loadPluginProperties(pluginId);

        System.out.println();
        System.out.println("Plugin Configuration");
        System.out.println("--------------------");
        System.out.println("Plugin : " + plugin.id());
        System.out.println("Name   : " + plugin.displayName());
        System.out.println("Status : " + (plugin.enabled() ? "enabled" : "disabled"));
        System.out.println();

        properties.forEach((key, value) ->
                System.out.printf("%-35s = %s%n", key, value));

        System.out.println();
        noteIgnoredParallelism(arguments);
    }

    /**
     * Decide which plugin to run
     *
     * @param arguments command line arguments
     * @param bootstrap bootstrap loader
     * @param configurationDatabase configuration is in the database
     * @param registry source for the configuration if its in the database
     * @return plugin execution status
     * @throws IOException on I/O failure
     * @throws InterruptedException when concurrent execution is interrupted
     */
    private static ExecutionStatus runPlugins(
            final CommandLineArguments arguments,
            final ApplicationBootstrapProperties bootstrap,
            final DatabaseResourceManager configurationDatabase,
            final JdbcPluginRegistry registry) throws IOException, InterruptedException {
        final var propertiesSource = bootstrap.useDatabaseProperties()
                ? new JdbcConfigurationPropertiesSource(configurationDatabase)
                : new ClasspathConfigurationPropertiesSource();
        final var runtime = ApplicationRuntimeConfiguration.load(propertiesSource, Map.of());
        if (!arguments.dryRun() && runtime.database().password().isBlank()) {
            throw new OpenDataConfigurationException(
                    "application.database.password must be supplied for a database-writing run.");
        }
        LoggingManager.configure(runtime.logging(), arguments.verbose());

        final var selected = new PluginSelectionResolver().resolve(arguments, registry);
        final var definitionLoader = new PropertiesPluginDefinitionLoader(propertiesSource);
        final var plugins = selected.stream()
                .map(descriptor -> new ResolvedPlugin(
                descriptor,
                definitionLoader.load(descriptor.id(), Map.of())))
                .toList();

        final var parallelism = arguments.parallelism().orElse(
                runtime.execution().maxParallelPlugins());
        LOGGER.log(Level.INFO,
                "Selected {0} registered plugin(s); parallelism={1}; dryRun={2}",
                new Object[]{plugins.size(), Math.min(parallelism, plugins.size()), arguments.dryRun()});

        DatabaseResourceManager executionDatabase = null;
        closeDatabase(configurationDatabase);
        try {
            final PluginRunAudit audit;
            if (arguments.dryRun()) {
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
            final var summary = coordinator.execute(plugins, parallelism, arguments.dryRun());
            logSummary(summary);
            return summary.allSuccessful()
                    ? ExecutionStatus.SUCCESS
                    : ExecutionStatus.PLUGIN_FAILURE;
        } finally {
            closeDatabase(executionDatabase);
        }
    }

    /**
     * register one of more plugins
     */
    private static void registerPlugins(
            final CommandLineArguments arguments,
            final ApplicationBootstrapProperties bootstrap,
            final ApplicationBootstrapPropertiesLoader bootstrapLoader,
            final ConfigurationPasswordCipher passwordCipher,
            final DatabaseResourceManager database,
            final JdbcPluginRegistry registeredPlugins) {
        final var classpathSource = new ClasspathConfigurationPropertiesSource();
        final List<PluginRegistration> registrations;
        if (arguments.pluginFile().isPresent()) {
            registrations = List.of(registrationFromFile(
                    arguments.pluginIds().get(0), arguments.pluginFile().orElseThrow()));
        } else {
            registrations = registrationsFromClasspath(arguments, classpathSource);
        }

        new ConfigurationRegistrationService(
                classpathSource,
                new JdbcConfigurationPropertiesSource(database),
                registeredPlugins,
                bootstrapLoader,
                passwordCipher)
                .register(bootstrap, registrations);

        registrations.forEach((var registration) -> {
            final var actual = registeredPlugins.find(registration.descriptor().id())
                    .orElseThrow(() -> new PluginRegistryException(
                    "Registered plugin could not be read back: "
                    + registration.descriptor().id()));
            LOGGER.info(String.format(
                    "Registered plugin: %s (%s)%n",
                    actual.id(),
                    actual.enabled() ? "enabled" : "disabled"));
        });
        noteIgnoredParallelism(arguments);
    }

    /**
     * Read plugin details from a file
     */
    private static PluginRegistration registrationFromFile(
            final String requestedPluginId,
            final Path file) {
        final var source = new PropertiesFileConfigurationPropertiesSource(file);
        final var properties = source.loadPluginProperties(requestedPluginId);
        final var definition = new PropertiesPluginDefinitionLoader(source)
                .load(requestedPluginId, Map.of());
        validateImplementation(definition.implementationClass());
        return new PluginRegistration(toDescriptor(definition), properties);
    }

    /**
     * get list of registered plugins
     */
    private static List<PluginRegistration> registrationsFromClasspath(
            final CommandLineArguments arguments,
            final ClasspathConfigurationPropertiesSource source) {
        final var catalog = new ClasspathPluginRegistry();
        final List<PluginDescriptor> requested;
        if (arguments.allPluginsRequested()) {
            requested = catalog.list();
        } else {
            requested = arguments.pluginIds().stream()
                    .map(id -> catalog.find(id).orElseThrow(() -> new PluginRegistryException(
                    "Packaged plugin definition was not found: " + id)))
                    .toList();
        }
        final var loader = new PropertiesPluginDefinitionLoader(source);
        final List<PluginRegistration> registrations = new ArrayList<>();
        for (var descriptor : requested) {
            final var properties = source.loadPluginProperties(descriptor.id());
            final var definition = loader.load(descriptor.id(), Map.of());
            validateImplementation(definition.implementationClass());
            registrations.add(new PluginRegistration(toDescriptor(definition), properties));
        }
        return List.copyOf(registrations);
    }

    /**
     * return plugin details
     */
    private static PluginDescriptor toDescriptor(final PluginDefinition definition) {
        return new PluginDescriptor(
                definition.id(),
                definition.displayName(),
                definition.description(),
                definition.implementationClass(),
                definition.enabled(),
                definition.configurationVersion());
    }

    /**
     * validate the plugin
     */
    private static void validateImplementation(final String className) {
        try {
            final var implementation = Class.forName(
                    className, false, Thread.currentThread().getContextClassLoader());
            if (!OpenDataPlugin.class.isAssignableFrom(implementation)) {
                throw new PluginRegistryException(
                        "Plugin class does not implement OpenDataPlugin: " + className);
            }
        } catch (ClassNotFoundException exception) {
            throw new PluginRegistryException(
                    "Plugin implementation class was not found: " + className,
                    exception);
        }
    }

    /**
     * we are deciding what to do with a plugin
     */
    private static void administerSelected(
            final CommandLineArguments arguments,
            final JdbcPluginRegistry registry,
            final AdministrationAction action) {
        final List<String> pluginIds = arguments.allPluginsRequested()
                ? registry.list().stream().map(PluginDescriptor::id).toList()
                : arguments.pluginIds();
        if (pluginIds.isEmpty()) {
            LOGGER.warning("No registered plugins matched the request.");
            return;
        }
        for (var pluginId : pluginIds) {
            switch (action) {
                case UNREGISTER ->
                    registry.unregister(pluginId);
                case ENABLE ->
                    registry.setEnabled(pluginId, true);
                case DISABLE ->
                    registry.setEnabled(pluginId, false);
                default ->
                    throw new IllegalStateException("Unsupported administration action: " + action);
            }
            LOGGER.log(Level.INFO, "{0} plugin: {1}", new Object[]{action.displayText, pluginId});
        }
        noteIgnoredParallelism(arguments);
    }

    /**
     * show list of plugins
     */
    private static void printRegisteredPlugins(final JdbcPluginRegistry registry) {
        final var plugins = registry.list();
        if (plugins.isEmpty()) {
            LOGGER.info("No plugins are registered.");
            return;
        }
        LOGGER.info(String.format("%-20s %-10s %-32s %s%n", "PLUGIN", "STATUS", "NAME", "IMPLEMENTATION"));
        plugins.forEach((var plugin) -> {
            LOGGER.info(String.format(
                    "%-20s %-10s %-32s %s%n",
                    plugin.id(),
                    plugin.enabled() ? "enabled" : "disabled",
                    plugin.displayName(),
                    plugin.implementationClass()));
        });
    }

    /**
     * get the database password so we can register plugins
     */
    private static void requireDatabasePassword(
            final ApplicationBootstrapProperties bootstrap,
            final CommandLineArguments arguments) {
        if (bootstrap.databasePassword().isBlank()) {
            final var purpose = arguments.registerRequested()
                    ? "--register"
                    : "plugin registry access";
            throw new OpenDataConfigurationException(
                    "application.database.password must be supplied for " + purpose + ".");
        }
    }

    /**
     * An information message as we don't care about running in parallel
     */
    private static void noteIgnoredParallelism(final CommandLineArguments arguments) {
        if (arguments.parallelism().isPresent()) {
            LOGGER.log(Level.INFO,
                    "--parallelism is ignored for plugin administration operations.");
        }
    }

    /**
     * disconnect from the database and close down
     */
    private static void closeDatabase(final DatabaseResourceManager database) {
        if (database == null) {
            return;
        }
        try {
            database.close();
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Database shutdown failed: {0}", ExceptionMessages.rootCauseMessage(exception));
            LOGGER.log(Level.FINE, "Database shutdown failure details.", exception);
        }
    }

    /**
     * show the execution summary
     */
    private static void logSummary(final PluginExecutionSummary summary) {
        summary.results().forEach((var result) -> {
            LOGGER.log(
                    result.successful() ? Level.INFO : Level.SEVERE,
                    "Plugin summary: id={0}, status={1}, duration={2}, read={3}, inserted={4}, updated={5}, skipped={6}, error={7}",
                    new Object[]{
                        result.pluginId(), result.status().name(), DurationFormatter.formatElapsed(result.duration()),
                        result.metrics().read(), result.metrics().inserted(), result.metrics().updated(),
                        result.metrics().skipped(), result.errorMessage().orElse("")
                    });
        });
        LOGGER.log(Level.INFO,
                "Plugin execution complete; selected={0}, succeeded={1}, failed={2}",
                new Object[]{summary.results().size(), summary.succeeded(), summary.failed()});
    }

    /**
     * plugin status
     */
    private enum AdministrationAction {
        REGISTERED("Registered"),
        UNREGISTER("Unregistered"),
        ENABLE("Enabled"),
        DISABLE("Disabled");

        private final String displayText;

        AdministrationAction(final String displayText) {
            this.displayText = displayText;
        }
    }
}
