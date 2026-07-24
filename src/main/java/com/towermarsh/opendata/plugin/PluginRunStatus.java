/*
 * Filename: PluginRunStatus.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

/** Database and process status for one plugin task. */
public enum PluginRunStatus {
    RUNNING,
    SUCCESS,
    DRY_RUN,
    FAILED,
    CANCELLED
}
