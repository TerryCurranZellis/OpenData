/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.database;

import com.towermarsh.opendata.database.DatabaseException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.Connection;

/**
 * Rejects database connections during non-writing plugin dry runs.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
public class UnavailableDatabaseResourceManager implements DatabaseResourceManager {

    /**
     * Rejects every attempt to borrow a connection.
     *
     * @return never returns normally
     * @throws DatabaseException always, as database access is disabled
     */
    @Override
    public Connection getConnection() throws DatabaseException {
        throw new DatabaseException("Database access is disabled during --dry-run.");
    }

    /**
     * Closes the no-op resource manager.
     */
    @Override
    public void close() {
        // No resources to close.
    }
}
