/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.exception;

/**
 * Thrown when application or plugin configuration cannot be loaded, parsed or
 * validated.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class ConfigurationException extends RuntimeException {

    /**
     * Creates a new configuration exception.
     *
     * @param message the detail message
     *
     */
    public ConfigurationException(final String message) {
        super(message);
    }

    /**
     * Creates a new configuration exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     *
     */
    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
