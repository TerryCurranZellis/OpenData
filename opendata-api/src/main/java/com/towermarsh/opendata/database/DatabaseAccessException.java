/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

/**
 * Unchecked wrapper for SQL bootstrap or persistence failures.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
public class DatabaseAccessException extends DatabaseException {

    private static final long serialVersionUID = 1L;

    /** @param message description of the failure */
    public DatabaseAccessException(String message) {
        super(message);
    }

    /**
     * @param message description of the failure
     * @param cause underlying failure
     */
    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
