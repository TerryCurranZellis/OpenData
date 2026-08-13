/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.plugin.PluginRegistry;
import com.towermarsh.opendata.plugin.PluginRunStatus;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Loads the read-only plugin information required by the JavaFX main table.
 *
 * <p>Registry metadata continues to come from the existing
 * {@link PluginRegistry}. The only GUI-specific database query reads the latest
 * run audit for each plugin. This keeps SQL and database ownership out of the
 * JavaFX controller while preserving the existing CLI registry contract.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class PluginTableDataService {

    private static final String LATEST_RUNS_SQL = """
            SELECT [PluginId], [Status], [StartedAt]
              FROM
              (
                  SELECT [PluginId], [Status], [StartedAt], [CreatedAt],
                         ROW_NUMBER() OVER
                         (
                             PARTITION BY [PluginId]
                             ORDER BY [StartedAt] DESC, [CreatedAt] DESC
                         ) AS [RowNumber]
                    FROM [core].[PluginRun]
              ) AS [Latest]
             WHERE [RowNumber] = 1
            """;

    private final DatabaseResourceManager database;
    private final PluginRegistry registry;

    /**
     * Creates the GUI plugin-table read service.
     *
     * @param database database resource used for run-audit reads
     * @param registry authoritative persistent plugin registry
     */
    public PluginTableDataService(
            final DatabaseResourceManager database,
            final PluginRegistry registry) {
        this.database = Objects.requireNonNull(database, "database");
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    /**
     * Loads registered plugins together with their most recent run status.
     *
     * @return immutable rows in registry order
     */
    public List<PluginTableEntry> load() {
        final var latestRuns = loadLatestRuns();
        return registry.list().stream()
                .map(plugin -> {
                    final var latest = latestRuns.get(plugin.id().toLowerCase(Locale.ROOT));
                    return new PluginTableEntry(
                            plugin.id(),
                            plugin.description(),
                            plugin.enabled(),
                            latest == null
                                    ? Optional.empty()
                                    : Optional.of(latest.status()),
                            latest == null
                                    ? Optional.empty()
                                    : Optional.of(latest.startedAtUtc()));
                })
                .toList();
    }

    private Map<String, LatestRun> loadLatestRuns() {
        final Map<String, LatestRun> result = new HashMap<>();
        try (var connection = database.getConnection();
                var statement = connection.prepareStatement(LATEST_RUNS_SQL);
                var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                final var pluginId = resultSet.getString("PluginId");
                final var status = PluginRunStatus.valueOf(resultSet.getString("Status"));
                final var startedAt = resultSet.getObject("StartedAt", LocalDateTime.class);
                result.put(pluginId.toLowerCase(Locale.ROOT), new LatestRun(status, startedAt));
            }
            return result;
        } catch (SQLException exception) {
            throw new DatabaseAccessException(
                    "Unable to load latest plugin run information.", exception);
        }
    }

    private record LatestRun(PluginRunStatus status, LocalDateTime startedAtUtc) {

        private LatestRun {
            Objects.requireNonNull(status, "status");
            Objects.requireNonNull(startedAtUtc, "startedAtUtc");
        }
    }
}
