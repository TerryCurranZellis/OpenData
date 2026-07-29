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
 * @version 17 July 2026
 */
public class DiscoveryException extends OpenDataException {

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
