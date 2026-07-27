/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.exception;

/**
 * Indicates that a dataset link could not be discovered or selected safely.
 */
public class DiscoveryException extends OpenDataException {

    /**
     *
     * @param message
     */
    public DiscoveryException(String message) {
        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public DiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
