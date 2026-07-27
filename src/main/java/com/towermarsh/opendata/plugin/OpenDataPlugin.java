/*
 * Filename: OpenDataPlugin.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

/** Contract implemented by every executable OpenData plugin.  *
* @author Terry Curran
* @version 17 July 2026
*/
@FunctionalInterface
public interface OpenDataPlugin {
    PluginMetrics execute(PluginExecutionContext context) throws Exception;
}
