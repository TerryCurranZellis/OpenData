/*
 * Filename: PluginRunStatus.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

/** Database and process status for one plugin task.  *
* @author Terry Curran
* @version 17 July 2026
*/
public enum PluginRunStatus {
    RUNNING,
    SUCCESS,
    DRY_RUN,
    FAILED,
    CANCELLED
}
