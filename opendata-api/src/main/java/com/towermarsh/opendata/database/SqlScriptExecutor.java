/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Executes classpath SQL resources with SQL Server GO-delimited batches.
 * Available to all modules through the dependency-neutral API module.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
public final class SqlScriptExecutor {

    private static final Pattern GO = Pattern.compile("(?im)^\\s*GO\\s*$");
    private static final Pattern COMMENTS = Pattern.compile("(?s)/\\*.*?\\*/|--[^\\r\\n]*");

    private SqlScriptExecutor() {
        // Static utility.
    }

    /**
     * Executes a SQL classpath resource using a managed connection.
     *
     * @param database manager supplying a JDBC connection
     * @param loader class loader containing the resource
     * @param resource resource path, without a leading slash
     * @throws DatabaseAccessException on resource or SQL failures
     */
    public static void executeResource(DatabaseResourceManager database, ClassLoader loader, String resource) {
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
     * Splits a SQL script into non-empty, executable batches.
     *
     * @param script complete SQL script
     * @return immutable list of batches in original order
     */
    public static List<String> batches(String script) {
        var result = new ArrayList<String>();
        for (var batch : GO.split(Objects.requireNonNull(script, "script"))) {
            if (!COMMENTS.matcher(batch).replaceAll("").isBlank()) {
                result.add(batch.strip());
            }
        }
        return List.copyOf(result);
    }

    /**
     * @param connection JDBC connection
     * @param script SQL script to execute
     * @param name resource name for diagnostics
     * @throws SQLException if executing a batch fails
     */
    private static void execute(Connection connection, String script, String name) throws SQLException {
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
