/*
 * Filename: DatabaseResourceManager.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides pooled JDBC resources to repositories.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public interface DatabaseResourceManager extends AutoCloseable {

    /**
     * Borrows a database connection from the underlying resource.
     *
     * @return borrowed connection
     * @throws SQLException if a connection cannot be obtained
     */
    Connection getConnection() throws SQLException;

    /**
     * Returns current pool usage where the implementation exposes it.
     *
     * @return pool usage snapshot
     */
    default DatabasePoolSnapshot getPoolSnapshot() {
        return new DatabasePoolSnapshot(0, 0, 0, false);
    }

    /**
     *
     * Closes a borrowed JDBC connection.
     *
     * @param connection connection to close
     *
     */
    default void close(final Connection connection) {
        closeQuietly(connection);
    }

    /**
     *
     * Closes a prepared statement.
     *
     * @param statement statement to close
     *
     */
    default void close(final PreparedStatement statement) {
        closeQuietly(statement);
    }

    /**
     *
     * Closes a result set.
     *
     * @param resultSet result set to close
     *
     */
    default void close(final ResultSet resultSet) {
        closeQuietly(resultSet);
    }

    /**
     *
     * Closes a resource while suppressing any secondary close failure.
     *
     * @param resource resource to close
     *
     */
    private static void closeQuietly(final AutoCloseable resource) {
        if (resource == null) {
            return;
        }
        try {
            resource.close();
        } catch (Exception ignored) {
            // A close failure must not hide the original database exception.
        }
    }

    /**
     *
     * Closes the underlying database resource.
     *
     */
    @Override
    void close();
}
