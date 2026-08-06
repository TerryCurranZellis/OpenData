/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.exception;

/**
 * Exception raised when a plugin step fails.
 *
 * <p>All plugin exceptions should use this class (or its subclasses) rather
 * than declaring plugin-specific exception types. The {@code pluginName} field
 * identifies which plugin raised the exception so that it can be reported in
 * logs and audit records without having to inspect the stack trace.
 *
 * <h2>Example</h2>
 * <pre>
 *   throw new PluginException("octopus", "Failed to read PDF input directory", cause);
 * </pre>
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class PluginException extends OpenDataException {

    private final String pluginName;

    /**
     * Creates a new plugin exception.
     *
     * @param pluginName the stable identifier of the plugin that raised this
     *                   exception (e.g. {@code "octopus"}, {@code "ofgem"})
     * @param message    the detail message
     */
    public PluginException(final String pluginName, final String message) {
        super("[" + requirePluginName(pluginName) + "] " + message);
        this.pluginName = pluginName;
    }

    /**
     * Creates a new plugin exception with a cause.
     *
     * @param pluginName the stable identifier of the plugin that raised this
     *                   exception
     * @param message    the detail message
     * @param cause      the underlying cause
     */
    public PluginException(
            final String pluginName,
            final String message,
            final Throwable cause) {
        super("[" + requirePluginName(pluginName) + "] " + message, cause);
        this.pluginName = pluginName;
    }

    /**
     * Returns the stable identifier of the plugin that raised this exception.
     *
     * @return plugin identifier; never {@code null} or blank
     */
    public String pluginName() {
        return pluginName;
    }

    private static String requirePluginName(final String pluginName) {
        if (pluginName == null || pluginName.isBlank()) {
            throw new IllegalArgumentException("pluginName must not be null or blank");
        }
        return pluginName;
    }
}
