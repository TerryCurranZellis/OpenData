/*
 * Filename: OpenDataConfigurationException.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

/** Raised when application, database, execution, or override configuration is invalid. */
public final class OpenDataConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     *
     * @param message
     */
    public OpenDataConfigurationException(final String message) {
        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public OpenDataConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
