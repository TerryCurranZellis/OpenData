/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import com.towermarsh.opendata.config.ApplicationConfig;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Compatibility facade used by repositories to borrow pooled connections.
 */
public final class DatabaseConnectionManager implements AutoCloseable {

    private final DatabaseResourceManager resourceManager;

    /**
     * Creates the default SQL Server pool.
     *
     * @param config application database configuration
     */
    public DatabaseConnectionManager(ApplicationConfig config) {
        this(new SQLServerResource(config));
    }

    /**
     * Creates a SQL Server pool using explicit pool settings.
     *
     * @param config application database configuration
     * @param poolConfig pool settings
     */
    public DatabaseConnectionManager(
            ApplicationConfig config,
            DatabasePoolConfig poolConfig) {
        this(new SQLServerResource(config, poolConfig));
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
