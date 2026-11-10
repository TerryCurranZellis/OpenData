/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.plugin;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

/**
 * SQL Server implementation of the generic plugin-run audit.
 *
 * @author Terry Curran
 * @since 1.0.0
 * @version 4.1.0
 */
public final class JdbcPluginRunAudit implements PluginRunAudit {
    private static final String INSERT_SQL = """
            INSERT INTO [core].[PluginRun]
                ([RunId], [PluginId], [Status], [StartedAt], [ThreadName], [HostName])
            VALUES (?, ?, 'RUNNING', ?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE [core].[PluginRun]
               SET [Status] = ?,
                   [CompletedAt] = ?,
                   [RowsRead] = ?,
                   [RowsInserted] = ?,
                   [RowsUpdated] = ?,
                   [RowsSkipped] = ?,
                   [ErrorMessage] = ?
             WHERE [RunId] = ?
            """;

    private final DatabaseResourceManager database;
    private final String hostName;

    /**
     * Creates an audit writer backed by the SQL Server resource manager.
     *
     * @param database database connection provider
     */
    public JdbcPluginRunAudit(final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
        this.hostName = resolveHostName();
    }

    /**
     * Inserts the running audit row and verifies that one row was inserted.
     *
     * @param runId unique run identifier
     * @param pluginId selected plugin identifier
     * @param threadName worker thread name
     * @param startedAt run start time
     */
    @Override
    public void started(
            final UUID runId,
            final String pluginId,
            final String threadName,
            final Instant startedAt) {
        try (var connection = database.getConnection();
                var statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setObject(1, runId);
            statement.setString(2, pluginId);
            statement.setObject(3, utcDateTime(startedAt));
            statement.setString(4, truncate(threadName, 128));
            statement.setString(5, truncate(hostName, 128));
            if (statement.executeUpdate() != 1) {
                throw new DatabaseAccessException(
                        "Plugin run audit start did not insert exactly one row: " + runId);
            }
        } catch (SQLException exception) {
            throw new DatabaseAccessException("Unable to create plugin run audit row for " + pluginId, exception);
        }
    }

    /**
     * Records the final status and counters for an existing run.
     *
     * @param result completed run result
     */
    @Override
    public void completed(final PluginRunResult result) {
        try (var connection = database.getConnection();
                var statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, result.status().name());
            statement.setObject(2, utcDateTime(result.completedAt()));
            statement.setLong(3, result.metrics().read());
            statement.setLong(4, result.metrics().inserted());
            statement.setLong(5, result.metrics().updated());
            statement.setLong(6, result.metrics().skipped());
            statement.setString(7, result.errorMessage().map(value -> truncate(value, PluginFailureReport.MAX_LENGTH)).orElse(null));
            statement.setObject(8, result.runId());
            if (statement.executeUpdate() != 1) {
                throw new DatabaseAccessException("Plugin run audit row was not found: " + result.runId());
            }
        } catch (SQLException exception) {
            throw new DatabaseAccessException("Unable to complete plugin run audit row for " + result.pluginId(), exception);
        }
    }

    /**
     * Utc date time.
     *
     * @param instant input instant
     * @return result of utc date time
     */
    private static LocalDateTime utcDateTime(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * Returns resolve host name.
     *
     * @return result of resolve host name
     */
    private static String resolveHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException exception) {
            return "unknown";
        }
    }

    /**
     * Truncate.
     *
     * @param value value of value
     * @param maxLength input max length
     * @return result of truncate
     */
    private static String truncate(final String value, final int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
