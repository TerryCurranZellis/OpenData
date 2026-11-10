/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.config.ApplicationBootstrapPropertiesLoader;
import com.towermarsh.opendata.config.JdbcConfigurationPropertiesSource;
import com.towermarsh.opendata.config.AesConfigurationPasswordCipher;
import com.towermarsh.opendata.core.database.SQLServerResource;
import com.towermarsh.opendata.core.plugin.JdbcPluginRegistry;
import com.towermarsh.opendata.core.plugin.PluginRegistryException;
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
 * @since 3.0.0
 * @version 4.1.0
 */
public final class PluginDetailGateway {

    /**
     * Loads registry metadata and stored configuration for one plugin.
     *
     * @param pluginId registered plugin identifier
     * @return immutable display rows
     */
    public List<ConfigurationDisplayEntry> load(String pluginId) {
        var requestedId = Objects.requireNonNull(pluginId, "pluginId").trim();
        if (requestedId.isEmpty()) {
            throw new IllegalArgumentException("pluginId must not be blank.");
        }

        var passwordCipher = new AesConfigurationPasswordCipher();
        var bootstrap = new ApplicationBootstrapPropertiesLoader(passwordCipher)
                .load(Map.of());

        try (var database = SQLServerResource.initialise(
                bootstrap.toDatabasePoolConfiguration())) {
            var registry = new JdbcPluginRegistry(database);
            var plugin = registry.find(requestedId)
                    .orElseThrow(() -> new PluginRegistryException(
                    "Registered plugin was not found: " + requestedId));
            var properties = new JdbcConfigurationPropertiesSource(database)
                    .loadPluginProperties(plugin.id());

            var result = new ArrayList<ConfigurationDisplayEntry>();
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
