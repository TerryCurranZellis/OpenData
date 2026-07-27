/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import com.towermarsh.opendata.config.DatabasePoolConfiguration;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Compatibility facade used by repositories to borrow pooled connections.
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class DatabaseConnectionManager implements AutoCloseable {

    private final DatabaseResourceManager resourceManager;

    /**
     * Creates a manager using the supplied SQL Server pool configuration.
     *
     * @param configuration SQL Server and connection-pool configuration
     */
    public DatabaseConnectionManager(DatabasePoolConfiguration configuration) {
        this(SQLServerResource.initialise(configuration));
    }

    /**
     * Creates a manager around any database resource implementation.
     *
     * @param resourceManager resource manager
     */
    public DatabaseConnectionManager(DatabaseResourceManager resourceManager) {
        this.resourceManager = Objects.requireNonNull(resourceManager, "resourceManager");
    }

    /**
     * Borrows a connection from the pool.
     *
     * @return pooled connection
     * @throws DatabaseException if there are database issues
     */
    public Connection getConnection() throws DatabaseException {
        try {
            return resourceManager.getConnection();
        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Unable to initialise the SQL Server connection pool.", exception);
        }
    }

    /**
     * Returns current pool usage.
     *
     * @return pool snapshot
     */
    public DatabasePoolSnapshot getPoolSnapshot() {
        return resourceManager.getPoolSnapshot();
    }

    /**
     * Closes the underlying database resource manager.
     *
     * @throws DatabaseException if the database resource cannot be closed
     */
    @Override
    public void close() throws DatabaseException {
        resourceManager.close();

    }
}
