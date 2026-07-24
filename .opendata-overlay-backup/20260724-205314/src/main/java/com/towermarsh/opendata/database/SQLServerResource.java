/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import com.towermarsh.opendata.config.ApplicationConfig;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * SQL Server resource manager backed by an Apache Commons DBCP connection pool.
 *
 * <p>The class deliberately has an explicit lifecycle rather than a global
 * singleton. This permits separate pools for additional databases or workloads
 * in later phases and makes orderly application shutdown possible.</p>
 */
public final class SQLServerResource implements DatabaseResourceManager {

    public static final String DATABASE_NAME = "OpenData";
    public static final String DEFAULT_USER = "OpenData";
    public static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String DEFAULT_JDBC_URL =
            "jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true";

    private static final Logger LOGGER = Logger.getLogger(SQLServerResource.class.getName());

    private final BasicDataSource dataSource;

    /**
     * Creates a pool using the standard pool limits.
     *
     * @param config application database configuration
     */
    public SQLServerResource(ApplicationConfig config) {
        this(config, DatabasePoolConfig.defaults());
    }

    /**
     * Creates a pool using explicit limits.
     *
     * @param config application database configuration
     * @param poolConfig pool settings
     */
    public SQLServerResource(
            ApplicationConfig config,
            DatabasePoolConfig poolConfig) {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(poolConfig, "poolConfig");

        String url = requireText(config.getDatabaseUrl(), "database URL");
        String user = requireText(config.getDatabaseUser(), "database user");
        String password = requireText(config.getDatabasePassword(), "database password");

        BasicDataSource configured = new BasicDataSource();
        configured.setDriverClassName(JDBC_DRIVER);
        configured.setUrl(url);
        configured.setUsername(user);
        configured.setPassword(password);
        configured.setInitialSize(poolConfig.initialSize());
        configured.setMinIdle(poolConfig.minIdle());
        configured.setMaxIdle(poolConfig.maxIdle());
        configured.setMaxTotal(poolConfig.maxTotal());
        configured.setMaxWaitMillis(poolConfig.maxWait().toMillis());
        configured.setMinEvictableIdleTimeMillis(
                poolConfig.minEvictableIdleTime().toMillis());
        configured.setTimeBetweenEvictionRunsMillis(60_000L);
        configured.setTestOnBorrow(poolConfig.testOnBorrow());
        configured.setTestWhileIdle(true);
        configured.setValidationQuery(poolConfig.validationQuery());
        configured.setValidationQueryTimeout(
                poolConfig.validationQueryTimeoutSeconds());
        configured.setDefaultAutoCommit(true);
        configured.setRollbackOnReturn(true);
        configured.setAutoCommitOnReturn(true);
        configured.setAccessToUnderlyingConnectionAllowed(false);
        this.dataSource = configured;

        LOGGER.log(
                Level.CONFIG,
                "Configured SQL Server pool for user {0}; maximum connections {1}",
                new Object[]{user, poolConfig.maxTotal()});
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (isClosed()) {
            throw new SQLException("SQL Server connection pool is closed");
        }
        return dataSource.getConnection();
    }

    @Override
    public DatabasePoolSnapshot getPoolSnapshot() {
        return new DatabasePoolSnapshot(
                dataSource.getNumActive(),
                dataSource.getNumIdle(),
                dataSource.getMaxTotal(),
                dataSource.isClosed());
    }

    @Override
    public boolean isClosed() {
        return dataSource.isClosed();
    }

    @Override
    public void close() throws SQLException {
        if (!dataSource.isClosed()) {
            LOGGER.fine("Closing SQL Server connection pool");
            dataSource.close();
        }
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
        return value.trim();
    }
}
