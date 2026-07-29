/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.app;

/**
 * Final status reported by one OpenData process invocation.
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public enum ExecutionStatus {

    /**
     * Not yet started
     */
    NOT_STARTED(1, "Not started"),

    /**
     * Successfully completed
     */
    SUCCESS(0, "Successful"),

    /**
     * Application error
     */
    APPLICATION_FAILURE(1, "Application error"),

    /**
     * Command line error
     */
    COMMAND_LINE_ERROR(2, "Command line error"),

    /**
     * Configuration  error
     */
    CONFIGURATION_ERROR(3, "Configuration error"),

    /**
     * Plugin error
     */
    PLUGIN_FAILURE(4, "One or more plugins failed"),

    /**
     * Database error
     */
    DATABASE_FAILURE(5, "Database failure"),

    /**
     * I/O error
     */
    IO_FAILURE(6, "Input/output failure"),

    /**
     * Thread interruption
     */
    INTERRUPTED(130, "Interrupted");

    /**
     * status code number to return to os
     */
    private final int statusCode;
    /**
     * friendly status name
     */
    private final String displayName;

    ExecutionStatus(final int statusCode, final String displayName) {
        this.statusCode = statusCode;
        this.displayName = displayName;
    }

    /**
     * Returns the numeric application status code.
     *
     * @return status code
     */
    public int statusCode() {
        return statusCode;
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
