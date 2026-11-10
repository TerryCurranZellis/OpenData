/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

/**
 * Unchecked exception for database access and persistence failures.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
public class DatabaseAccessException extends DatabaseException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a database access exception.
     *
     * @param message explanation of the failure
     */
    public DatabaseAccessException(String message) {
        super(message);
    }

    /**
     * Creates a database access exception retaining its cause.
     *
     * @param message explanation of the failure
     * @param cause underlying failure
     */
    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
