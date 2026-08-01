/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

/**
 * Unchecked exception signalling an irrecoverable database operation failure.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public class DatabaseException extends RuntimeException {

    /**
     * Creates a new database exception.
     *
     * @param message the detail message
     */
    public DatabaseException(final String message) {
        super(message);
    }

    /**
     *
     * Creates a new database exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public DatabaseException(
            final String message,
            final Throwable cause) {
        super(message, cause);
    }
}
