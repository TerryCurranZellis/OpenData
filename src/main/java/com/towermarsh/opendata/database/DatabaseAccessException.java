/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

/**
 * Unchecked wrapper for database bootstrap and persistence failures.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class DatabaseAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new database access exception.
     *
     * @param message the detail message
     *
     */
    public DatabaseAccessException(final String message) {
        super(message);
    }

    /**
     * Creates a new database access exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     *
     */
    public DatabaseAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
