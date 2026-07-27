/*
 * Filename: DatabaseAccessException.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

/** Unchecked wrapper for database bootstrap and persistence failures. */
public final class DatabaseAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     *
     * @param message
     */
    public DatabaseAccessException(final String message) {
        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public DatabaseAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
