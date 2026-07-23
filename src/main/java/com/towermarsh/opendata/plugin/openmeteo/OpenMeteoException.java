package com.towermarsh.opendata.plugin.openmeteo;

/**
 * Indicates that OpenMeteo download or response processing failed.
 */
public final class OpenMeteoException extends Exception {

    public OpenMeteoException(final String message) {
        super(message);
    }

    public OpenMeteoException(
            final String message,
            final Throwable cause) {
        super(message, cause);
    }
}
