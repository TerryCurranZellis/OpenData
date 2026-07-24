/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Supplies database connections and owns the lifecycle of the underlying
 * connection resource, normally a connection pool.
 */
public interface DatabaseResourceManager extends AutoCloseable {

    Logger RESOURCE_LOGGER = Logger.getLogger(DatabaseResourceManager.class.getName());

    /**
     * Borrows a connection. Closing the returned connection returns it to the
     * pool rather than closing the physical SQL Server connection.
     *
     * @return pooled JDBC connection
     * @throws SQLException if a connection cannot be obtained
     */
    Connection getConnection() throws SQLException;

    /**
     * Returns a snapshot of pool utilisation.
     *
     * @return current pool snapshot
     */
    DatabasePoolSnapshot getPoolSnapshot();

    /**
     * Indicates whether the resource manager has been closed.
     *
     * @return true when closed
     */
    boolean isClosed();

    /**
     * Closes the pool and all idle physical connections.
     *
     * @throws SQLException if the pool cannot be closed cleanly
     */
    @Override
    void close() throws SQLException;

    /**
     * Compatibility helper. Prefer try-with-resources for new code.
     *
     * @param connection connection to return to the pool
     */
    default void close(Connection connection) {
        closeQuietly(connection);
    }

    /**
     * Compatibility helper. Prefer try-with-resources for new code.
     *
     * @param statement statement to close
     */
    default void close(PreparedStatement statement) {
        closeQuietly(statement);
    }

    /**
     * Compatibility helper. Prefer try-with-resources for new code.
     *
     * @param resultSet result set to close
     */
    default void close(ResultSet resultSet) {
        closeQuietly(resultSet);
    }

    private static void closeQuietly(AutoCloseable resource) {
        if (resource == null) {
            return;
        }
        try {
            resource.close();
        } catch (Exception exception) {
            RESOURCE_LOGGER.log(Level.WARNING, "Unable to close JDBC resource", exception);
        }
    }
}
