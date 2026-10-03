/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.database;

/**
 * Unchecked wrapper for database bootstrap and persistence failures.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public class DatabaseAccessException extends DatabaseException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new database access exception.
     *
     * @param message the detail message
     *
     */
    public DatabaseAccessException(String message) {
        super(message);
    }

    /**
     * Creates a new database access exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     *
     */
    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
