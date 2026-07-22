package com.towermarsh.opendata.config;

/**
 * Thrown when a plugin properties file cannot be converted into a valid
 * structured plugin definition.
 */
public final class PluginDefinitionException extends RuntimeException {

    public PluginDefinitionException(final String message) {
        super(message);
    }

    public PluginDefinitionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
