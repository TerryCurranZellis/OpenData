/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.cli;

/**
 * Operation requested for the selected plugins.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public enum PluginCommand {
    /**
     * Execute the selected enabled plugins.
     */
    RUN("run"),
    /**
     * Register or replace the selected plugin definitions.
     */
    REGISTER("register"),
    /**
     * Remove the selected plugins from the persistent registry.
     */
    UNREGISTER("unregister"),
    /**
     * Mark the selected registered plugins as enabled.
     */
    ENABLE("enable"),
    /**
     * Mark the selected registered plugins as disabled.
     */
    DISABLE("disable"),
    /**
     * Display the stored configuration for one selected registered plugin.
     */
    DETAIL("detail");

    /**
     * friendly status name
     */
    private final String displayName;

    PluginCommand(final String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable status description.
     *
     * @return display name
     */
    public String displayName() {
        return displayName;
    }
}
