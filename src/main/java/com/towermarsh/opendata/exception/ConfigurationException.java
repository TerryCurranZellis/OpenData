package com.towermarsh.opendata.exception;

/**
 * Thrown when application or plugin configuration cannot be loaded,
 * parsed or validated.
 */
public final class ConfigurationException extends RuntimeException {

    public ConfigurationException(final String message) {
        super(message);
    }

    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
