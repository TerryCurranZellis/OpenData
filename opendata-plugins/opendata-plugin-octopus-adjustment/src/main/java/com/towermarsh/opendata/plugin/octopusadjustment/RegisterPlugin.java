/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment;

import com.towermarsh.opendata.common.database.SqlResourcePluginRegistrar;

/**
 * Registration hook for the {@code octopus-adjustment} plugin. Creates the plugin's database
 * schema and tables from {@code sql/plugins/octopus-adjustment.sql} when the plugin is registered.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public class RegisterPlugin extends SqlResourcePluginRegistrar {

    /** Creates the registrar. */
    public RegisterPlugin() {
        super(OctopusAdjustmentPlugin.PLUGIN_ID, "sql/plugins/octopus-adjustment.sql");
    }
}
