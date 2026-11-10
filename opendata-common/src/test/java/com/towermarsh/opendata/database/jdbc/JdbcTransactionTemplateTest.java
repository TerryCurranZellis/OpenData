/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.database.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.towermarsh.opendata.common.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

/**
 * Tests shared JDBC transaction handling.
 *
 * @author Terry Curran
 * @since 2.0.0
 * @version 4.1.0
 */
class JdbcTransactionTemplateTest {

    @Test
    void commitsSuccessfulWorkAndRestoresAutoCommit() throws SQLException {
        final DatabaseResourceManager database = mock(DatabaseResourceManager.class);
        final Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);

        final String result = new JdbcTransactionTemplate(database).execute(
                "Unable to save data",
                current -> "saved");

        assertEquals("saved", result);
        final InOrder order = inOrder(connection);
        order.verify(connection).setAutoCommit(false);
        order.verify(connection).commit();
        order.verify(connection).setAutoCommit(true);
        order.verify(connection).close();
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void rollsBackCheckedFailureAndWrapsIt() throws SQLException {
        final DatabaseResourceManager database = mock(DatabaseResourceManager.class);
        final Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);

        assertThrows(
                DatabaseAccessException.class,
                () -> new JdbcTransactionTemplate(database).execute(
                        "Unable to save data",
                        current -> {
                            throw new SQLException("failed");
                        }));

        verify(connection).rollback();
        verify(connection).setAutoCommit(true);
    }

    /**
     * A runtime cleanup failure must not prevent restoring the connection's
     * original auto-commit mode after a successful commit.
     *
     * @throws SQLException if mock setup fails
     */
    @Test
    void restoresAutoCommitWhenCleanupThrowsUncheckedException() throws SQLException {
        final DatabaseResourceManager database = mock(DatabaseResourceManager.class);
        final Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        final var cleanupFailure = new IllegalStateException("cleanup failed");

        final var thrown = assertThrows(IllegalStateException.class,
                () -> new JdbcTransactionTemplate(database).execute(
                        "Unable to save data", current -> "saved",
                        current -> {
                            throw cleanupFailure;
                        }));

        assertSame(cleanupFailure, thrown);
        verify(connection).commit();
        verify(connection, never()).rollback();
        final InOrder order = inOrder(connection);
        order.verify(connection).setAutoCommit(false);
        order.verify(connection).commit();
        order.verify(connection).setAutoCommit(true);
        order.verify(connection).close();
    }

    /**
     * Rollback and unchecked cleanup errors are suppressed on the primary
     * database failure so the original cause remains diagnosable.
     *
     * @throws SQLException if mock setup fails
     */
    @Test
    void preservesOriginalSqlFailureWhenCleanupAlsoFails() throws SQLException {
        final DatabaseResourceManager database = mock(DatabaseResourceManager.class);
        final Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        final var original = new SQLException("insert failed");
        final var cleanupFailure = new IllegalStateException("cleanup failed");
        final var rollbackFailure = new SQLException("rollback failed");
        org.mockito.Mockito.doThrow(rollbackFailure).when(connection).rollback();

        final var thrown = assertThrows(DatabaseAccessException.class,
                () -> new JdbcTransactionTemplate(database).execute(
                        "Unable to save data", current -> {
                            throw original;
                        }, current -> {
                            throw cleanupFailure;
                        }));

        assertSame(original, thrown.getCause());
        assertEquals(2, original.getSuppressed().length);
        assertSame(rollbackFailure, original.getSuppressed()[0]);
        assertSame(cleanupFailure, original.getSuppressed()[1]);
        verify(connection).setAutoCommit(true);
        verify(connection).close();
    }

    /**
     * Fatal errors still trigger rollback and are never replaced by a
     * secondary failure during connection cleanup.
     *
     * @throws SQLException if mock setup fails
     */
    @Test
    void preservesOriginalErrorWhenCleanupFails() throws SQLException {
        final DatabaseResourceManager database = mock(DatabaseResourceManager.class);
        final Connection connection = mock(Connection.class);
        when(database.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        final var original = new AssertionError("fatal execution failure");
        final var cleanupFailure = new SQLException("cleanup failed");

        final var thrown = assertThrows(AssertionError.class,
                () -> new JdbcTransactionTemplate(database).execute(
                        "Unable to save data", current -> {
                            throw original;
                        }, current -> {
                            throw cleanupFailure;
                        }));

        assertSame(original, thrown);
        assertTrue(java.util.Arrays.asList(original.getSuppressed()).contains(cleanupFailure));
        verify(connection).rollback();
        verify(connection).setAutoCommit(true);
    }


}
