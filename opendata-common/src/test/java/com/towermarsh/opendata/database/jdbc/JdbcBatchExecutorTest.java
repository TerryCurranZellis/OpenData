/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.database.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests reusable JDBC batch execution.
 *
 * @author Terry Curran
 * @since 2.0.0
 * @version 4.1.0
 */
class JdbcBatchExecutorTest {

    @Test
    void executesConfiguredBatchSizesAndCountsResults() throws Exception {
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement("INSERT INTO test(value) VALUES (?)"))
                .thenReturn(statement);
        when(statement.executeBatch())
                .thenReturn(new int[]{1, Statement.SUCCESS_NO_INFO})
                .thenReturn(new int[]{1});

        var affected = JdbcBatchExecutor.execute(
                connection,
                "INSERT INTO test(value) VALUES (?)",
                List.of("one", "two", "three"),
                2,
                (current, value) -> current.setString(1, value));

        assertEquals(3, affected);
        verify(statement, times(3)).addBatch();
        verify(statement, times(2)).executeBatch();
    }
    /**
     * A binder accepting a base type can process records of a subtype.
     *
     * @throws Exception when the mocked JDBC execution fails
     */
    @Test
    void supportsSubtypeRecordsWithSupertypeBinder() throws Exception {
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        when(connection.prepareStatement("INSERT INTO numbers(value) VALUES (?)"))
                .thenReturn(statement);
        when(statement.executeBatch()).thenReturn(new int[]{1, 1});
        JdbcStatementBinder<Number> binder =
                (current, number) -> current.setInt(1, number.intValue());

        var affected = JdbcBatchExecutor.execute(
                connection, "INSERT INTO numbers(value) VALUES (?)",
                List.of(1, 2), 2, binder);

        assertEquals(2, affected);
        verify(statement, times(2)).setInt(org.mockito.ArgumentMatchers.eq(1),
                org.mockito.ArgumentMatchers.anyInt());
    }

}
