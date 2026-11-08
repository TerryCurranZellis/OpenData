/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.database;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.plugin.PluginRegistrar;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Base {@link PluginRegistrar} that runs one idempotent SQL script from the
 * classpath.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public abstract class SqlResourcePluginRegistrar implements PluginRegistrar {

    private static final Logger LOGGER = Logger.getLogger(SqlResourcePluginRegistrar.class.getName());

    private final String pluginId;
    private final String scriptResource;

    /**
     * Creates the registrar.
     *
     * @param pluginId plugin identifier
     * @param scriptResource classpath resource of the schema script, e.g. {@code sql/plugins/x.sql}
     */
    protected SqlResourcePluginRegistrar(String pluginId, String scriptResource) {
        this.pluginId = Objects.requireNonNull(pluginId, "pluginId");
        this.scriptResource = Objects.requireNonNull(scriptResource, "scriptResource");
    }

    /**
     * {@inheritDoc}
     *
     * @return result of plugin id
     */
    @Override
    public String pluginId() {
        return pluginId;
    }

    /**
     * {@inheritDoc}
     *
     * @param database input database
     */
    @Override
    public void register(DatabaseResourceManager database) {
        LOGGER.info(() -> "Creating database objects for plugin %s from %s".formatted(pluginId, scriptResource));
        try {
            SqlScriptExecutor.executeResource(database, getClass().getClassLoader(), scriptResource);
        } catch (DatabaseAccessException exception) {
            throw new DatabaseAccessException(
                    "Plugin %s could not create its database objects: %s"
                            .formatted(pluginId, exception.getMessage()), exception);
        }
    }
}
