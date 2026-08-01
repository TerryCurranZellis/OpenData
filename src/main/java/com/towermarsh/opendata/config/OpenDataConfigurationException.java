/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

/**
 * Raised when application, database, execution, or override configuration is
 * invalid.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class OpenDataConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new configuration exception.
     *
     * @param message the detail message
     */
    public OpenDataConfigurationException(final String message) {
        super(message);
    }

    /**
     * Creates a new configuration exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public OpenDataConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
