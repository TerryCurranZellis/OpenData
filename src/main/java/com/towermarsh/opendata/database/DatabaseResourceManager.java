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

/** Provides pooled JDBC resources to repositories. */
public interface DatabaseResourceManager extends AutoCloseable {

    /**
     *
     * @return
     * @throws SQLException
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
     * @param connection
     */
    default void close(final Connection connection) {
        closeQuietly(connection);
    }

    /**
     *
     * @param statement
     */
    default void close(final PreparedStatement statement) {
        closeQuietly(statement);
    }

    /**
     *
     * @param resultSet
     */
    default void close(final ResultSet resultSet) {
        closeQuietly(resultSet);
    }

    /**
     *
     * @param resource
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
     */
    @Override
    void close();
}
