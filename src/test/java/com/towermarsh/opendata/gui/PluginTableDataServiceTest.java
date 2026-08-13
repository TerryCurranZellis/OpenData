/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.plugin.PluginDescriptor;
import com.towermarsh.opendata.plugin.PluginRegistry;
import com.towermarsh.opendata.plugin.PluginRunStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests the read-only registry/run-audit composition used by the GUI table.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
class PluginTableDataServiceTest {

    @Test
    void combinesRegistryRowsWithLatestRunAndLeavesNeverRunPluginBlank() throws Exception {
        final var database = mock(DatabaseResourceManager.class);
        final var registry = mock(PluginRegistry.class);
        final var connection = mock(Connection.class);
        final var statement = mock(PreparedStatement.class);
        final var resultSet = mock(ResultSet.class);

        when(registry.list()).thenReturn(List.of(
                descriptor("ofgem", "Ofgem Energy Price Cap", true),
                descriptor("octopus", "Octopus Energy Statements", false)));
        when(database.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("PluginId")).thenReturn("OfGem");
        when(resultSet.getString("Status")).thenReturn("SUCCESS");
        final var startedAt = LocalDateTime.of(2026, 8, 13, 18, 45);
        when(resultSet.getObject("StartedAt", LocalDateTime.class)).thenReturn(startedAt);

        final var rows = new PluginTableDataService(database, registry).load();

        assertEquals(2, rows.size());
        assertEquals("ofgem", rows.get(0).pluginId());
        assertEquals(PluginRunStatus.SUCCESS, rows.get(0).lastRunStatus().orElseThrow());
        assertEquals(startedAt, rows.get(0).lastRunStartedAtUtc().orElseThrow());
        assertEquals("octopus", rows.get(1).pluginId());
        assertEquals(false, rows.get(1).enabled());
        assertEquals(true, rows.get(1).lastRunStatus().isEmpty());
        assertEquals(true, rows.get(1).lastRunStartedAtUtc().isEmpty());
    }

    @Test
    void translatesLatestRunQueryFailure() throws Exception {
        final var database = mock(DatabaseResourceManager.class);
        final var registry = mock(PluginRegistry.class);
        when(database.getConnection()).thenThrow(new SQLException("database unavailable"));

        assertThrows(DatabaseAccessException.class,
                () -> new PluginTableDataService(database, registry).load());
    }

    private static PluginDescriptor descriptor(
            final String id,
            final String description,
            final boolean enabled) {
        return new PluginDescriptor(
                id,
                id,
                description,
                "example." + id,
                enabled,
                1);
    }
}
