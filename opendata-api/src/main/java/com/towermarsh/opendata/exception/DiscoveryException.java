/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.exception;

/**
 * Indicates that a dataset link could not be discovered or selected safely.
 *
 * @author Terry Curran
 * @since 1.0.0
 * @version 4.1.0
 */
public class DiscoveryException extends OpenDataException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new discovery exception.
     *
     * @param message the detail message
     */
    public DiscoveryException(String message) {
        super(message);
    }

    /**
     * Creates a new discovery exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     *
     */
    public DiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
