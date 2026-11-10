/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

/**
 * Unchecked exception indicating a database operation failure. This contract
 * can be used by Core and provider plugins without depending on Common.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
public class DatabaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message explanation of the database failure
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * @param message explanation of the database failure
     * @param cause underlying exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
