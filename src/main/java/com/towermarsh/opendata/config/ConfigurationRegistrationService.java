/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import com.towermarsh.opendata.plugin.PluginDescriptor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Registers file-backed application and plugin properties in the database and
 * updates the bootstrap file for future database-backed runs.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class ConfigurationRegistrationService {

    private final ConfigurationPropertiesSource fileSource;
    private final JdbcConfigurationPropertiesSource databaseSource;
    private final ApplicationBootstrapPropertiesLoader bootstrapLoader;
    private final ConfigurationPasswordCipher passwordCipher;

    /**
     * Creates the registration service.
     *
     * @param fileSource file-backed property source
     * @param databaseSource database-backed property source
     * @param bootstrapLoader bootstrap file loader
     * @param passwordCipher password cipher
     */
    public ConfigurationRegistrationService(
            final ConfigurationPropertiesSource fileSource,
            final JdbcConfigurationPropertiesSource databaseSource,
            final ApplicationBootstrapPropertiesLoader bootstrapLoader,
            final ConfigurationPasswordCipher passwordCipher) {
        this.fileSource = Objects.requireNonNull(fileSource, "fileSource");
        this.databaseSource = Objects.requireNonNull(databaseSource, "databaseSource");
        this.bootstrapLoader = Objects.requireNonNull(bootstrapLoader, "bootstrapLoader");
        this.passwordCipher = Objects.requireNonNull(passwordCipher, "passwordCipher");
    }

    /**
     * Registers application and plugin properties.
     *
     * @param bootstrapProperties resolved bootstrap properties with a plain-text password
     * @param plugins installed plugins to register
     */
    public void register(
            final ApplicationBootstrapProperties bootstrapProperties,
            final List<PluginDescriptor> plugins) {
        Objects.requireNonNull(bootstrapProperties, "bootstrapProperties");
        Objects.requireNonNull(plugins, "plugins");

        final var applicationProperties = new LinkedHashMap<>(ApplicationRuntimeConfiguration.defaultPropertyValues());
        applicationProperties.putAll(fileSource.loadApplicationProperties());
        applicationProperties.put("application.version", bootstrapProperties.applicationVersion());
        applicationProperties.put("application.use-database-properties", "true");
        applicationProperties.put("database.url", bootstrapProperties.databaseUrl());
        applicationProperties.put("database.user", bootstrapProperties.databaseUser());
        applicationProperties.put(
                "database.password",
                passwordCipher.encrypt(bootstrapProperties.databasePassword()));

        databaseSource.saveApplicationProperties(applicationProperties, List.of("database.password"));

        for (var plugin : plugins) {
            final Map<String, String> pluginProperties = new LinkedHashMap<>(
                    fileSource.loadPluginProperties(plugin.id()));
            databaseSource.savePluginProperties(plugin.id(), pluginProperties);
        }

        bootstrapLoader.store(bootstrapProperties.withUseDatabaseProperties(true));
    }
}
