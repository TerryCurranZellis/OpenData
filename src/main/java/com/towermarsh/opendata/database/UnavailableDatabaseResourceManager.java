/*
 * Filename: UnavailableDatabaseResourceManager.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import java.sql.Connection;
import java.sql.SQLException;

/** Database marker used by a dry run; any attempted write is treated as a defect. */
public final class UnavailableDatabaseResourceManager implements DatabaseResourceManager {
    @Override
    public Connection getConnection() throws SQLException {
        throw new SQLException("Database access is disabled during --dry-run.");
    }

    @Override
    public void close() {
        // Nothing to release.
    }
}
