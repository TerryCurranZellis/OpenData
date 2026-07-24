/*
 * Filename: ExecutionStatus.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.app;

/** Final status reported by one OpenData process invocation. */
public enum ExecutionStatus {
    NOT_STARTED,
    SUCCESS,
    PLUGIN_FAILURE,
    COMMAND_LINE_ERROR,
    CONFIGURATION_ERROR,
    INTERRUPTED,
    APPLICATION_FAILURE
}
