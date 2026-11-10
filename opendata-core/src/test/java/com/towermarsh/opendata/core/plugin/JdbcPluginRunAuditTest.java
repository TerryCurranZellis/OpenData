/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.plugin;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.plugin.PluginMetrics;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Verifies SQL Server plugin-run audit persistence and failure handling.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
@ExtendWith(MockitoExtension.class)
class JdbcPluginRunAuditTest {

    @Mock
    private DatabaseResourceManager database;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement statement;

    /**
     * Sets up a mock connection and prepared statement for each audit operation.
     *
     * @throws Exception if a mock connection cannot be configured
     */
    @BeforeEach
    void prepare() throws Exception {
        when(database.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
    }

    /** Persists the start time as UTC and records the run identity. */
    @Test
    void startsOneRunWithUtcTimestamp() throws Exception {
        final var id = UUID.randomUUID();
        final var started = Instant.parse("2026-11-10T09:08:07Z");
        when(statement.executeUpdate()).thenReturn(1);

        new JdbcPluginRunAudit(database).started(id, "ofgem", "worker-1", started);

        verify(statement).setObject(1, id);
        verify(statement).setString(2, "ofgem");
        verify(statement).setObject(3, LocalDateTime.of(2026, 11, 10, 9, 8, 7));
        verify(statement).setString(4, "worker-1");
    }

    /** An INSERT returning no row is an audit failure, not a successful start. */
    @Test
    void rejectsMissingAuditStartRow() throws Exception {
        when(statement.executeUpdate()).thenReturn(0);

        assertThrows(DatabaseAccessException.class,
                () -> new JdbcPluginRunAudit(database).started(
                        UUID.randomUUID(), "ofgem", "worker-1", Instant.now()));
    }

    /** Stores counters and limits long failure reports to the SQL column size. */
    @Test
    void completesRunWithCountersAndTruncatedFailureReport() throws Exception {
        final var id = UUID.randomUUID();
        final var started = Instant.parse("2026-11-10T09:08:07Z");
        final var finished = Instant.parse("2026-11-10T09:08:09Z");
        when(statement.executeUpdate()).thenReturn(1);
        final var result = new PluginRunResult(
                "ofgem", id, PluginRunStatus.FAILED,
                started, finished, new PluginMetrics(4, 2, 1, 1),
                Optional.of("X".repeat(PluginFailureReport.MAX_LENGTH + 200)));

        new JdbcPluginRunAudit(database).completed(result);

        verify(statement).setString(1, "FAILED");
        verify(statement).setObject(2, LocalDateTime.of(2026, 11, 10, 9, 8, 9));
        verify(statement).setLong(3, 4);
        verify(statement).setLong(4, 2);
        verify(statement).setLong(5, 1);
        verify(statement).setLong(6, 1);
        final var message = ArgumentCaptor.forClass(String.class);
        verify(statement).setString(org.mockito.ArgumentMatchers.eq(7), message.capture());
        assertEquals(PluginFailureReport.MAX_LENGTH, message.getValue().length());
        verify(statement).setObject(8, id);
    }

    /** A missing audit row must produce a visible failure for the coordinator. */
    @Test
    void rejectsMissingAuditCompletionRow() throws Exception {
        when(statement.executeUpdate()).thenReturn(0);
        final var now = Instant.parse("2026-11-10T09:08:07Z");
        final var result = new PluginRunResult(
                "ofgem", UUID.randomUUID(), PluginRunStatus.SUCCESS,
                now, now, PluginMetrics.ZERO, Optional.empty());

        assertThrows(DatabaseAccessException.class,
                () -> new JdbcPluginRunAudit(database).completed(result));
    }
}
