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
     * @throws SQLException if no connection is available
     */
    public Connection getConnection() throws SQLException {
        return resourceManager.getConnection();
    }

    /**
     * Returns current pool usage.
     *
     * @return pool snapshot
     */
    public DatabasePoolSnapshot getPoolSnapshot() {
        return resourceManager.getPoolSnapshot();
    }

    @Override
    public void close() throws SQLException {
        resourceManager.close();
    }
}
