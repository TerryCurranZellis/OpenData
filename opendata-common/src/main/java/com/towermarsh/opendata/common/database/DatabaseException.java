/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.database;

/**
 * Unchecked exception signalling an irrecoverable database operation failure.
 *
 * @author Terry Curran
 * @since 1.0.0
 * @version 4.1.0
 * @deprecated Use {@link com.towermarsh.opendata.database.DatabaseException}.
 */
@Deprecated(since = "4.1.0")
public class DatabaseException extends com.towermarsh.opendata.database.DatabaseException {

    /**
     * Creates a new database exception.
     *
     * @param message the detail message
     */
    public DatabaseException(String message) {
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
            String message,
            Throwable cause) {
        super(message, cause);
    }
}
