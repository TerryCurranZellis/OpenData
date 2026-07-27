/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;

/**
 * Performs a lightweight SQL Server identity and database check.
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class DatabaseHealthCheck {

    private static final String SQL = """
            SELECT DB_NAME(), ORIGINAL_LOGIN(), USER_NAME()
            """;

    private final DatabaseConnectionManager connectionManager;

    /**
     * Creates a health check that uses the supplied connection manager.
     *
     * @param connectionManager connection manager used to query SQL Server
     */
    public DatabaseHealthCheck(DatabaseConnectionManager connectionManager) {
        this.connectionManager = Objects.requireNonNull(
                connectionManager, "connectionManager");
    }

    /**
     * Executes a lightweight SQL Server health check query.
     *
     * @return database identity details captured during the check
     * @throws DatabaseException if the health check query cannot be completed successfully
     */
    public Result check() throws DatabaseException {
        try (var connection = connectionManager.getConnection(); 
             var statement = connection.prepareStatement(SQL); 
             var resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                throw new DatabaseException("SQL Server health check returned no row");
            }
            return new Result(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    Instant.now());
        } catch (SQLException ex) {
            throw new DatabaseException("SQL Server health check returned no row");
        }
    }

    public record Result(
            String databaseName,
            String loginName,
            String databaseUser,
            Instant checkedAt) {

    }
}
