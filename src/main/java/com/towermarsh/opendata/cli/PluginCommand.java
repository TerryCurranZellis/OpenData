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
    RUN,
    /** 
     * Register or replace the selected plugin definitions. 
     */
    REGISTER,
    /** 
     * Remove the selected plugins from the persistent registry. 
     */
    UNREGISTER,
    /** 
     * Mark the selected registered plugins as enabled. 
     */
    ENABLE,
    /** 
     * Mark the selected registered plugins as disabled. 
     */
    DISABLE
}
