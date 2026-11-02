/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricsupply;

import com.towermarsh.opendata.common.database.SqlResourcePluginRegistrar;

/**
 * Registration hook for the {@code electric-supply} plugin. Creates the plugin's database
 * schema and tables from {@code sql/plugins/electric-supply.sql} when the plugin is registered.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class RegisterPlugin extends SqlResourcePluginRegistrar {

    /** Creates the registrar. */
    public RegisterPlugin() {
        super(ElectricSupplyPlugin.PLUGIN_ID, "sql/plugins/electric-supply.sql");
    }
}
