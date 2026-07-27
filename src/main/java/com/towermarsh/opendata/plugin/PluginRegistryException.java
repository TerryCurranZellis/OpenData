package com.towermarsh.opendata.plugin;

/**
 * Indicates invalid or missing plugin registry metadata.
 */
public final class PluginRegistryException extends RuntimeException {

    /**
     *
     * @param message
     */
    public PluginRegistryException(final String message) {
        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public PluginRegistryException(
            final String message,
            final Throwable cause) {

        super(message, cause);
    }
}
