/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import com.towermarsh.opendata.database.DatabaseResourceManager;

/**
 * Registration hook owned by each plugin.
 *
 * <p>By convention every plugin provides a public class named
 * {@code RegisterPlugin} with a public no-argument constructor in the same
 * package as its {@link OpenDataPlugin} implementation. The application calls
 * {@link #register(DatabaseResourceManager)} whenever the plugin is registered
 * so the plugin can create its own database schema and tables. Implementations
 * must be idempotent.</p>
 *
 * @author Terry Curran
 * @since 3.1.0
 * @version 4.1.0
 */
public interface PluginRegistrar {

    /** Conventional simple class name of a plugin registration class. */
    String CONVENTIONAL_CLASS_NAME = "RegisterPlugin";

    /**
     * Returns the plugin identifier this registrar belongs to.
     *
     * @return plugin identifier
     */
    String pluginId();

    /**
     * Creates or upgrades the plugin's database objects. Safe to call repeatedly.
     *
     * @param database database resource manager
     */
    void register(DatabaseResourceManager database);
}
