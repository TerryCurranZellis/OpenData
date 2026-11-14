/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.database.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Verifies completed-source queries and SQL identifier validation.
 *
 * @author Terry Curran
 * @since 4.2.0
 * @version 4.2.0
 */
class JdbcCompletedFileKeysTest {

    @Test
    void normalisesFileIdentity() {
        assertEquals("statement.pdf|abcd",
                JdbcCompletedFileKeys.key("Statement.PDF", "ABCD"));
    }

    @Test
    void readsCompletedKeysFromQualifiedTable() throws SQLException {
        var database = mock(DatabaseResourceManager.class);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(database.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(contains("[octopus].[statement_file]")))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(results);
        when(results.next()).thenReturn(true, false);
        when(results.getString(1)).thenReturn("Statement.PDF");
        when(results.getString(2)).thenReturn("ABCD");

        var keys = JdbcCompletedFileKeys.read(
                database, "octopus", "statement_file", "Unable to read files");

        assertEquals(Set.of("statement.pdf|abcd"), keys);
        verify(connection).prepareStatement(contains("[octopus].[statement_file]"));
    }

    @Test
    void rejectsUnsafeIdentifiers() {
        var database = mock(DatabaseResourceManager.class);
        assertThrows(IllegalArgumentException.class,
                () -> JdbcCompletedFileKeys.read(
                        database, "octopus", "file];DROP TABLE x", "Failed"));
    }

    @Test
    void wrapsJdbcFailureWithProviderMessage() throws SQLException {
        var database = mock(DatabaseResourceManager.class);
        when(database.getConnection()).thenThrow(new SQLException("database unavailable"));

        var failure = assertThrows(DatabaseAccessException.class,
                () -> JdbcCompletedFileKeys.read(
                        database, "octopus", "statement_file", "Unable to read files"));

        assertEquals("Unable to read files", failure.getMessage());
    }
}
