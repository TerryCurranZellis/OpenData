/*
 * Filename: OpenDataPlugin.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

/** Contract implemented by every executable OpenData plugin. */
@FunctionalInterface
public interface OpenDataPlugin {

    /**
     *
     * @param context
     * @return
     * @throws Exception
     */
    PluginMetrics execute(PluginExecutionContext context) throws Exception;
}
