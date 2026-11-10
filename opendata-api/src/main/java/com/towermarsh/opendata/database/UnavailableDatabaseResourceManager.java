/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import java.sql.Connection;

/**
 * Dry-run database resource that rejects every connection request.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
public class UnavailableDatabaseResourceManager implements DatabaseResourceManager {

    /**
     * @return never returns normally
     * @throws DatabaseException whenever database access is attempted
     */
    @Override
    public Connection getConnection() {
        throw new DatabaseException("Database access is disabled during --dry-run.");
    }

    /** Closes this stateless dry-run resource. */
    @Override
    public void close() {
        // No resources are acquired.
    }
}
