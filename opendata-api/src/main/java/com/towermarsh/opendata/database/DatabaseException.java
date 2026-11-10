/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

/**
 * Unchecked exception indicating a failed database operation.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
public class DatabaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a database exception.
     *
     * @param message explanation of the failure
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Creates a database exception retaining its cause.
     *
     * @param message explanation of the failure
     * @param cause underlying failure
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
