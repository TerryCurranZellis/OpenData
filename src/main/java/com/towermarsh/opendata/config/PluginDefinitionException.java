/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

/**
 * Thrown when a plugin properties file cannot be converted into a valid
 * structured plugin definition.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class PluginDefinitionException extends RuntimeException {

    /**
     *
     *
     * Creates a new plugin definition exception.
     *
     * @param message the detail message
     *
     */
    public PluginDefinitionException(final String message) {
        super(message);
    }

    /**
     * Creates a new plugin definition exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public PluginDefinitionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
