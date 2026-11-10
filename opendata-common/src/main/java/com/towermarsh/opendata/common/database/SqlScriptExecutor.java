/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.database;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.database.SqlBatchParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Runs SQL Server scripts whose batches are separated by lines containing only
 * {@code GO}. Scripts are read from classpath resources so each plugin can ship
 * its own schema definition.
 *
 * @author Terry Curran
 * @since 3.1.0
 * @version 4.1.0
 */
public class SqlScriptExecutor {

    private SqlScriptExecutor() {
    }

    /**
     * Executes a classpath SQL resource.
     *
     * @param database database resource manager
     * @param loader class loader holding the resource
     * @param resource absolute classpath resource name, without leading slash
     */
    public static void executeResource(
            DatabaseResourceManager database,
            ClassLoader loader,
            String resource) {
        Objects.requireNonNull(database, "database");
        Objects.requireNonNull(loader, "loader");
        Objects.requireNonNull(resource, "resource");
        String script;
        try (var in = loader.getResourceAsStream(resource)) {
            if (in == null) {
                throw new DatabaseAccessException("SQL script resource was not found: " + resource);
            }
            script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new DatabaseAccessException("Unable to read SQL script resource: " + resource, exception);
        }
        try (var connection = database.getConnection()) {
            execute(connection, script, resource);
        } catch (SQLException exception) {
            throw new DatabaseAccessException(
                    "Unable to run SQL script " + resource + ": " + exception.getMessage(), exception);
        }
    }

    /**
     * Splits a script into executable batches, dropping empty and comment-only batches.
     *
     * @param script script text
     * @return ordered batches
     */
    public static List<String> batches(String script) {
        return SqlBatchParser.batches(script);
    }

    /**
     * Execute.
     *
     * @param connection input connection
     * @param script input script
     * @param name requested name
     * @throws SQLException if execute fails
     */
    private static void execute(
            Connection connection,
            String script,
            String name) throws SQLException {
        var number = 0;
        for (var batch : batches(script)) {
            number++;
            try (var statement = connection.createStatement()) {
                statement.execute(batch);
            } catch (SQLException exception) {
                throw new SQLException(
                        "batch %d of %s failed: %s".formatted(number, name, exception.getMessage()),
                        exception.getSQLState(), exception.getErrorCode(), exception);
            }
        }
    }
}
