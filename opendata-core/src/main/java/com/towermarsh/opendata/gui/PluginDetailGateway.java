/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.config.ApplicationBootstrapPropertiesLoader;
import com.towermarsh.opendata.config.JdbcConfigurationPropertiesSource;
import com.towermarsh.opendata.config.RsaConfigurationPasswordCipher;
import com.towermarsh.opendata.database.SQLServerResource;
import com.towermarsh.opendata.plugin.JdbcPluginRegistry;
import com.towermarsh.opendata.plugin.PluginRegistryException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Loads one registered plugin's stored configuration for the JavaFX detail
 * dialog.
 *
 * <p>The gateway owns the short-lived database resources required by the read.
 * The JavaFX controller never performs JDBC work directly.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
public final class PluginDetailGateway {

    /**
     * Loads registry metadata and stored configuration for one plugin.
     *
     * @param pluginId registered plugin identifier
     * @return immutable display rows
     */
    public List<ConfigurationDisplayEntry> load(final String pluginId) {
        final var requestedId = Objects.requireNonNull(pluginId, "pluginId").trim();
        if (requestedId.isEmpty()) {
            throw new IllegalArgumentException("pluginId must not be blank.");
        }

        final var passwordCipher = new RsaConfigurationPasswordCipher();
        final var bootstrap = new ApplicationBootstrapPropertiesLoader(passwordCipher)
                .load(Map.of());

        try (var database = SQLServerResource.initialise(
                bootstrap.toDatabasePoolConfiguration())) {
            final var registry = new JdbcPluginRegistry(database);
            final var plugin = registry.find(requestedId)
                    .orElseThrow(() -> new PluginRegistryException(
                    "Registered plugin was not found: " + requestedId));
            final var properties = new JdbcConfigurationPropertiesSource(database)
                    .loadPluginProperties(plugin.id());

            final List<ConfigurationDisplayEntry> result = new ArrayList<>();
            result.add(new ConfigurationDisplayEntry("Plugin ID", plugin.id()));
            result.add(new ConfigurationDisplayEntry("Display name", plugin.displayName()));
            result.add(new ConfigurationDisplayEntry("Description", plugin.description()));
            result.add(new ConfigurationDisplayEntry(
                    "Implementation class", plugin.implementationClass()));
            result.add(new ConfigurationDisplayEntry(
                    "Enabled", plugin.enabled() ? "Yes" : "No"));
            result.add(new ConfigurationDisplayEntry(
                    "Configuration version", Integer.toString(plugin.configurationVersion())));
            result.addAll(ConfigurationDisplayMasker.entries(properties));
            return List.copyOf(result);
        }
    }
}
