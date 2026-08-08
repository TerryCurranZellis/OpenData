/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

/**
 * Tests shared JDBC transaction handling.
 *
 * @author Terry Curran
 * @version 2.0.0
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
}
