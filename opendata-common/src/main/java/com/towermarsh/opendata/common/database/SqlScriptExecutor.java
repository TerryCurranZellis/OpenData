/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.database;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Runs SQL Server scripts whose batches are separated by lines containing only
 * {@code GO}. Scripts are read from classpath resources so each plugin can ship
 * its own schema definition.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class SqlScriptExecutor {

    private static final Pattern GO = Pattern.compile("(?im)^\\s*GO\\s*$");
    private static final Pattern COMMENTS = Pattern.compile("(?s)/\\*.*?\\*/|--[^\\r\\n]*");

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
            final DatabaseResourceManager database,
            final ClassLoader loader,
            final String resource) {
        Objects.requireNonNull(database, "database");
        Objects.requireNonNull(loader, "loader");
        Objects.requireNonNull(resource, "resource");
        final String script;
        try (InputStream in = loader.getResourceAsStream(resource)) {
            if (in == null) {
                throw new DatabaseAccessException("SQL script resource was not found: " + resource);
            }
            script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new DatabaseAccessException("Unable to read SQL script resource: " + resource, exception);
        }
        try (Connection connection = database.getConnection()) {
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
    public static List<String> batches(final String script) {
        final List<String> result = new ArrayList<>();
        for (var batch : GO.split(Objects.requireNonNull(script, "script"))) {
            if (!COMMENTS.matcher(batch).replaceAll("").isBlank()) {
                result.add(batch.strip());
            }
        }
        return List.copyOf(result);
    }

    private static void execute(
            final Connection connection,
            final String script,
            final String name) throws SQLException {
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
