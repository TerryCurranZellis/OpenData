/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import com.towermarsh.opendata.plugin.JdbcPluginRegistry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Registers application configuration and validated plugin definitions in SQL
 * Server, then updates the bootstrap file for database-backed operation.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class ConfigurationRegistrationService {

    private final ConfigurationPropertiesSource applicationFileSource;
    private final JdbcConfigurationPropertiesSource databaseSource;
    private final JdbcPluginRegistry pluginRegistry;
    private final ApplicationBootstrapPropertiesLoader bootstrapLoader;
    private final ConfigurationPasswordCipher passwordCipher;

    /**
     * Creates the registration service.
     *
     * @param applicationFileSource classpath application property source
     * @param databaseSource database-backed property source
     * @param pluginRegistry persistent plugin registry
     * @param bootstrapLoader bootstrap file loader
     * @param passwordCipher password cipher
     */
    public ConfigurationRegistrationService(
            final ConfigurationPropertiesSource applicationFileSource,
            final JdbcConfigurationPropertiesSource databaseSource,
            final JdbcPluginRegistry pluginRegistry,
            final ApplicationBootstrapPropertiesLoader bootstrapLoader,
            final ConfigurationPasswordCipher passwordCipher) {
        this.applicationFileSource = Objects.requireNonNull(
                applicationFileSource, "applicationFileSource");
        this.databaseSource = Objects.requireNonNull(databaseSource, "databaseSource");
        this.pluginRegistry = Objects.requireNonNull(pluginRegistry, "pluginRegistry");
        this.bootstrapLoader = Objects.requireNonNull(bootstrapLoader, "bootstrapLoader");
        this.passwordCipher = Objects.requireNonNull(passwordCipher, "passwordCipher");
    }

    /**
     * Registers or replaces the selected plugins and refreshes application
     * configuration in the database.
     *
     * @param bootstrapProperties resolved bootstrap properties with plain password
     * @param plugins validated plugins to register
     */
    public void register(
            final ApplicationBootstrapProperties bootstrapProperties,
            final List<PluginRegistration> plugins) {
        Objects.requireNonNull(bootstrapProperties, "bootstrapProperties");
        Objects.requireNonNull(plugins, "plugins");
        if (plugins.isEmpty()) {
            throw new IllegalArgumentException("At least one plugin must be supplied for registration.");
        }

        final var applicationProperties = new LinkedHashMap<>(
                ApplicationRuntimeConfiguration.defaultPropertyValues());
        applicationProperties.putAll(applicationFileSource.loadApplicationProperties());
        applicationProperties.put("application.version", bootstrapProperties.applicationVersion());
        applicationProperties.put("application.use-database-properties", "true");
        applicationProperties.put("database.url", bootstrapProperties.databaseUrl());
        applicationProperties.put("database.user", bootstrapProperties.databaseUser());
        applicationProperties.put(
                "database.password",
                passwordCipher.encrypt(bootstrapProperties.databasePassword()));

        databaseSource.saveApplicationProperties(
                applicationProperties,
                List.of("database.password"));
        plugins.forEach(plugin -> pluginRegistry.register(
                plugin.descriptor(), plugin.properties()));
        bootstrapLoader.store(bootstrapProperties.withUseDatabaseProperties(true));
    }
}
