/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.config.ApplicationBootstrapProperties;
import com.towermarsh.opendata.config.ApplicationBootstrapPropertiesLoader;
import com.towermarsh.opendata.config.ClasspathConfigurationPropertiesSource;
import com.towermarsh.opendata.config.ConfigurationPasswordCipher;
import com.towermarsh.opendata.config.ConfigurationRegistrationService;
import com.towermarsh.opendata.config.JdbcConfigurationPropertiesSource;
import com.towermarsh.opendata.config.PluginConfigurationDirectoryScanner;
import com.towermarsh.opendata.config.PluginRegistration;
import com.towermarsh.opendata.config.PluginRegistrationResolver;
import com.towermarsh.opendata.config.RsaConfigurationPasswordCipher;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.database.SQLServerResource;
import com.towermarsh.opendata.plugin.JdbcPluginRegistry;
import com.towermarsh.opendata.plugin.PluginDescriptor;
import com.towermarsh.opendata.plugin.PluginRegistryException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Resource-owning adapter for JavaFX plugin administration operations.
 *
 * <p>Each operation resolves the existing encrypted bootstrap configuration,
 * opens a short-lived configuration database resource, delegates to the
 * existing registry/configuration services and closes the resource before
 * returning to the JavaFX task. No JavaFX types cross this boundary.</p>
 *
 * <p>The GUI {@code Register} action discovers complete plugin properties files
 * from the standard configuration folders. {@code Register from File} accepts
 * one explicitly selected properties file. Command-line packaged registration
 * remains unchanged.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
public final class PluginAdministrationGateway {

    private final PluginRegistrationResolver registrationResolver;
    private final PluginConfigurationDirectoryScanner configurationScanner;

    /**
     * Creates the production administration adapter.
     */
    public PluginAdministrationGateway() {
        this(new PluginRegistrationResolver(), new PluginConfigurationDirectoryScanner());
    }

    /**
     * Creates an adapter with explicit registration dependencies.
     *
     * @param registrationResolver shared plugin-registration resolver
     * @param configurationScanner plugin configuration folder scanner
     */
    PluginAdministrationGateway(
            final PluginRegistrationResolver registrationResolver,
            final PluginConfigurationDirectoryScanner configurationScanner) {
        this.registrationResolver = Objects.requireNonNull(
                registrationResolver, "registrationResolver");
        this.configurationScanner = Objects.requireNonNull(
                configurationScanner, "configurationScanner");
    }

    /**
     * Returns the configuration directories searched by the Register action.
     *
     * @return immutable search path list
     */
    public List<Path> registrationDirectories() {
        return configurationScanner.directories();
    }

    /**
     * Scans configuration folders and returns valid definitions whose plugin id
     * is not already present in the persistent registry.
     *
     * @return immutable list of new plugin definitions
     */
    public List<PluginRegistrationCandidate> discoverNewPlugins() {
        final var files = configurationScanner.scan();
        if (files.isEmpty()) {
            return List.of();
        }
        final var registeredIds = withContext(context -> new HashSet<>(
                context.registry().list().stream().map(PluginDescriptor::id).toList()));
        final List<PluginRegistrationCandidate> result = new ArrayList<>();
        final Map<String, Path> newIds = new HashMap<>();

        for (var file : files) {
            final var registration = registrationResolver.resolveFile(file);
            final var descriptor = registration.descriptor();
            if (registeredIds.contains(descriptor.id())) {
                continue;
            }
            final var previous = newIds.putIfAbsent(descriptor.id(), file);
            if (previous != null && !previous.equals(file)) {
                throw new PluginRegistryException(
                        "More than one configuration file declares new plugin.id '%s': %s and %s"
                                .formatted(descriptor.id(), previous, file));
            }
            result.add(new PluginRegistrationCandidate(
                    descriptor.id(), descriptor.displayName(), file));
        }
        return List.copyOf(result);
    }

    /**
     * Registers the new configuration-folder definitions previously discovered
     * by {@link #discoverNewPlugins()}.
     *
     * <p>Definitions are read and validated again immediately before the write
     * so a file changed after discovery cannot silently register as a different
     * plugin.</p>
     *
     * @param candidates discovered candidates confirmed by the user
     * @return registered plugin identifiers
     */
    public List<String> registerDiscovered(
            final List<PluginRegistrationCandidate> candidates) {
        final var requested = List.copyOf(Objects.requireNonNull(candidates, "candidates"));
        if (requested.isEmpty()) {
            throw new IllegalArgumentException("At least one plugin must be supplied for registration.");
        }

        final List<PluginRegistration> registrations = new ArrayList<>();
        for (var candidate : requested) {
            final var registration = registrationResolver.resolveFile(candidate.file());
            if (!candidate.pluginId().equals(registration.descriptor().id())) {
                throw new PluginRegistryException(
                        "Plugin definition changed after discovery: expected '%s' but file now declares '%s'."
                                .formatted(candidate.pluginId(), registration.descriptor().id()));
            }
            registrations.add(registration);
        }

        return withContext(context -> {
            for (var registration : registrations) {
                if (context.registry().find(registration.descriptor().id()).isPresent()) {
                    throw new PluginRegistryException(
                            "Plugin is already registered: " + registration.descriptor().id());
                }
            }
            register(context, registrations);
            return registrations.stream()
                    .map(registration -> registration.descriptor().id())
                    .toList();
        });
    }

    /**
     * Registers or replaces one plugin using a complete external properties
     * file selected by the user. The plugin identifier is read from the file's
     * {@code plugin.id} property.
     *
     * @param file complete plugin properties file
     * @return registered plugin identifier
     */
    public String registerFromFile(final Path file) {
        Objects.requireNonNull(file, "file");
        return withContext(context -> {
            final var registration = registrationResolver.resolveFile(file);
            register(context, List.of(registration));
            return registration.descriptor().id();
        });
    }

    /**
     * Unregisters selected plugins and their stored configuration.
     *
     * @param pluginIds registered plugin identifiers
     * @return identifiers successfully unregistered
     */
    public List<String> unregister(final List<String> pluginIds) {
        return mutate(pluginIds, (registry, pluginId) -> registry.unregister(pluginId));
    }

    /**
     * Changes selected plugin enabled state.
     *
     * @param pluginIds registered plugin identifiers
     * @param enabled new enabled state
     * @return identifiers successfully updated
     */
    public List<String> setEnabled(
            final List<String> pluginIds,
            final boolean enabled) {
        return mutate(pluginIds,
                (registry, pluginId) -> registry.setEnabled(pluginId, enabled));
    }

    private List<String> mutate(
            final List<String> pluginIds,
            final RegistryMutation mutation) {
        final var requested = List.copyOf(Objects.requireNonNull(pluginIds, "pluginIds"));
        if (requested.isEmpty()) {
            throw new IllegalArgumentException("At least one plugin must be selected.");
        }
        return withContext(context -> {
            for (var pluginId : requested) {
                mutation.apply(context.registry(), pluginId);
            }
            return requested;
        });
    }

    private static void register(
            final AdministrationContext context,
            final List<PluginRegistration> registrations) {
        new ConfigurationRegistrationService(
                new ClasspathConfigurationPropertiesSource(),
                new JdbcConfigurationPropertiesSource(context.database()),
                context.registry(),
                context.bootstrapLoader(),
                context.passwordCipher())
                .register(context.bootstrap(), registrations);
    }

    private static <T> T withContext(
            final Function<AdministrationContext, T> operation) {
        final ConfigurationPasswordCipher passwordCipher = new RsaConfigurationPasswordCipher();
        final var bootstrapLoader = new ApplicationBootstrapPropertiesLoader(passwordCipher);
        final var bootstrap = bootstrapLoader.load(Map.of());
        try (var database = SQLServerResource.initialise(
                bootstrap.toDatabasePoolConfiguration())) {
            final var context = new AdministrationContext(
                    bootstrap,
                    bootstrapLoader,
                    passwordCipher,
                    database,
                    new JdbcPluginRegistry(database));
            return operation.apply(context);
        }
    }


    private record AdministrationContext(
            ApplicationBootstrapProperties bootstrap,
            ApplicationBootstrapPropertiesLoader bootstrapLoader,
            ConfigurationPasswordCipher passwordCipher,
            DatabaseResourceManager database,
            JdbcPluginRegistry registry) {
    }

    @FunctionalInterface
    private interface RegistryMutation {
        void apply(JdbcPluginRegistry registry, String pluginId);
    }
}
