/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

/**
 * Contract implemented by every executable OpenData plugin.
 *
 * @author Terry Curran
 * @version 3.3.0
 */
@FunctionalInterface
public interface OpenDataPlugin {

    /**
     * Executes the plugin using the supplied run-scoped context.
     *
     * @param context plugin execution context containing resolved configuration
     * and runtime resources
     * @return row-count metrics describing the execution result
     * @throws Exception if plugin execution fails
     */
    PluginMetrics execute(PluginExecutionContext context) throws Exception;
}
