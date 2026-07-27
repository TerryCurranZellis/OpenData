/*
 * Filename: UnavailableDatabaseResourceManager.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import java.sql.Connection;

/**
 * Database marker used by a dry run; any attempted write is treated as a
 * defect.
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class UnavailableDatabaseResourceManager implements DatabaseResourceManager {

    /**
     *
     * @return
     * @throws DatabaseException
     */
    @Override
    /**
     * Always rejects connection requests during a dry run.
     *
     * @return never returns normally
     * @throws DatabaseException always, because database access is disabled
     */
    public Connection getConnection() throws DatabaseException {
        throw new DatabaseException("Database access is disabled during --dry-run.");
    }

    /**
     *
     */
    @Override
    /**
     * Closes the dry-run resource manager.
     */
    public void close() {
        // Nothing to release.
    }
}
