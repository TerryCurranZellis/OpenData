/*
 * Filename: SQLServerResource.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import com.towermarsh.opendata.config.DatabasePoolConfiguration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Singleton SQL Server resource backed by Apache Commons DBCP.
 *
 * <p>
 * The pool is thread-safe. Each repository obtains a connection for its own
 * transaction and returns it with try-with-resources. A JDBC connection is
 * never shared between plugin threads.</p>
 */
public final class SQLServerResource implements DatabaseResourceManager {

    private static final Logger LOGGER = Logger.getLogger(SQLServerResource.class.getName());
    private static final String POOL_URL_PREFIX = "jdbc:apache:commons:dbcp:";
    private static SQLServerResource instance;

    private final String poolName;
    private final String poolUrl;
    private final GenericObjectPool<PoolableConnection> connectionPool;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * Creates and prepares the singleton SQL Server resource.
     *
     * @param configuration database pool configuration
     */
    private SQLServerResource(final DatabasePoolConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration");
        poolName = configuration.poolName();
        poolUrl = POOL_URL_PREFIX + poolName;
        try {
            Class.forName(configuration.driverClass());
            Class.forName("org.apache.commons.dbcp2.PoolingDriver");

            final ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                    configuration.jdbcUrl(), configuration.user(), configuration.password());
            final var poolableFactory = new PoolableConnectionFactory(connectionFactory, null);
            poolableFactory.setValidationQuery(configuration.validationQuery());
            poolableFactory.setValidationQueryTimeout(configuration.maxWait());

            final var poolConfig = new GenericObjectPoolConfig<PoolableConnection>();
            poolConfig.setMaxTotal(configuration.maxTotal());
            poolConfig.setMaxIdle(configuration.maxIdle());
            poolConfig.setMinIdle(configuration.minIdle());
            poolConfig.setMaxWait(configuration.maxWait());
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setBlockWhenExhausted(true);

            connectionPool = new GenericObjectPool<>(poolableFactory, poolConfig);
            poolableFactory.setPool(connectionPool);
            final var poolingDriver = (PoolingDriver) DriverManager.getDriver(POOL_URL_PREFIX);
            poolingDriver.registerPool(poolName, connectionPool);
            connectionPool.preparePool();
            LOGGER.log(Level.INFO,
                    "SQL Server pool {0} initialised; maxTotal={1}, minIdle={2}",
                    new Object[]{poolName, configuration.maxTotal(), configuration.minIdle()});
        } catch (ClassNotFoundException | SQLException exception) {
            throw new DatabaseAccessException("Unable to initialise SQL Server connection pool.", exception);
        } catch (Exception exception) {
            throw new DatabaseAccessException("Unable to prepare SQL Server connection pool.", exception);
        }
    }

    /**
     * Initialises the singleton SQL Server resource when required.
     *
     * @param configuration database pool configuration
     * @return initialised singleton resource
     */
    public static synchronized SQLServerResource initialise(final DatabasePoolConfiguration configuration) {
        if (instance != null && !instance.closed.get()) {
            return instance;
        }
        instance = new SQLServerResource(configuration);
        return instance;
    }

    /**
     * Returns the already-initialised singleton SQL Server resource.
     *
     * @return singleton SQL Server resource
     */
    public static synchronized SQLServerResource getInstance() {
        if (instance == null || instance.closed.get()) {
            throw new IllegalStateException("SQLServerResource has not been initialised.");
        }
        return instance;
    }

    @Override
    /**
     * Borrows a connection from the registered DBCP pool.
     *
     * @return pooled SQL Server connection
     * @throws DatabaseException if the pool is closed or a connection cannot be obtained
     */
    public Connection getConnection() throws DatabaseException {
        if (closed.get()) {
            throw new DatabaseException("SQL Server connection pool is closed.");
        }
        try {
            return DriverManager.getConnection(poolUrl);

        } catch (SQLException exception) {
            throw new DatabaseException("Unable to initialise the SQL Server connection pool.", exception);

        }
    }

    @Override
    /**
     * Closes a borrowed JDBC connection.
     *
     * @param connection connection to close
     */
    public void close(final Connection connection) {
        closeAndLog(connection, "connection");
    }

    @Override
    /**
     * Closes a prepared statement.
     *
     * @param statement statement to close
     */
    public void close(final PreparedStatement statement) {
        closeAndLog(statement, "prepared statement");
    }

    @Override
    /**
     * Closes a result set.
     *
     * @param resultSet result set to close
     */
    public void close(final ResultSet resultSet) {
        closeAndLog(resultSet, "result set");
    }

    @Override
    /**
     * Closes the registered SQL Server connection pool.
     */
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        try {
            final var poolingDriver = (PoolingDriver) DriverManager.getDriver(POOL_URL_PREFIX);
            poolingDriver.closePool(poolName);
            LOGGER.log(Level.INFO, "SQL Server pool {0} closed.", poolName);
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to close SQL Server pool " + poolName, exception);
        } finally {
            synchronized (SQLServerResource.class) {
                instance = null;
            }
        }
    }

    /**
     * Returns the number of active pooled connections.
     *
     * @return active connection count
     */
    public int activeConnections() {
        return connectionPool.getNumActive();
    }

    /**
     * Returns the number of idle pooled connections.
     *
     * @return idle connection count
     */
    public int idleConnections() {
        return connectionPool.getNumIdle();
    }

    @Override
    public DatabasePoolSnapshot getPoolSnapshot() {
        return new DatabasePoolSnapshot(
                activeConnections(),
                idleConnections(),
                connectionPool.getMaxTotal(),
                closed.get());
    }

    /**
     * Closes a JDBC resource and reports close failures consistently.
     *
     * @param resource resource to close
     * @param description resource description for error reporting
     */
    private static void closeAndLog(final AutoCloseable resource, final String description) {
        if (resource == null) {
            return;
        }
        try {
            resource.close();
        } catch (Exception exception) {
            throw new DatabaseException("Unable to close JDBC " + description + '.', exception);
        }
    }
}
