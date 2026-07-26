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
    NOT_STARTED("Not started"),
    SUCCESS("Successful"),
    PLUGIN_FAILURE("One or more plugins failed"),
    COMMAND_LINE_ERROR("Command-line error"),
    CONFIGURATION_ERROR("Configuration error"),
    INTERRUPTED("Interrupted"),
    APPLICATION_FAILURE("Application error");

    private final String displayName;

    ExecutionStatus(final String displayName) {
        this.displayName = displayName;
    }

    /** Human-readable status text for logs and operator output. */
    public String displayName() {
        return displayName;
    }
}
