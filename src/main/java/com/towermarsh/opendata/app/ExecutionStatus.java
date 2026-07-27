package com.towermarsh.opendata.app;

/**
 * Final status reported by one OpenData process invocation.
 */
public enum ExecutionStatus {

    /**
     *
     */
    NOT_STARTED(1, "Not started"),

    /**
     *
     */
    SUCCESS(0, "Successful"),

    /**
     *
     */
    APPLICATION_FAILURE(1, "Application error"),

    /**
     *
     */
    COMMAND_LINE_ERROR(2, "Command-line error"),

    /**
     *
     */
    CONFIGURATION_ERROR(3, "Configuration error"),

    /**
     *
     */
    PLUGIN_FAILURE(4, "One or more plugins failed"),

    /**
     *
     */
    DATABASE_FAILURE(5, "Database failure"),

    /**
     *
     */
    IO_FAILURE(6, "Input/output failure"),

    /**
     *
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
