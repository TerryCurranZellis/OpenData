/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;

/**
 * Performs a lightweight SQL Server identity and database check.
 */
public final class DatabaseHealthCheck {

    private static final String SQL = """
            SELECT DB_NAME(), ORIGINAL_LOGIN(), USER_NAME()
            """;

    private final DatabaseConnectionManager connectionManager;

    public DatabaseHealthCheck(DatabaseConnectionManager connectionManager) {
        this.connectionManager = Objects.requireNonNull(
                connectionManager, "connectionManager");
    }

    public Result check() throws SQLException {
        try (Connection connection = connectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL);
                ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                throw new SQLException("SQL Server health check returned no row");
            }
            return new Result(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    Instant.now());
        }
    }

    public record Result(
            String databaseName,
            String loginName,
            String databaseUser,
            Instant checkedAt) {
    }
}
