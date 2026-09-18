/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.jdbc;

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
 * @version 2.0.0
 */
class JdbcBatchExecutorTest {

    @Test
    void executesConfiguredBatchSizesAndCountsResults() throws Exception {
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement("INSERT INTO test(value) VALUES (?)"))
                .thenReturn(statement);
        when(statement.executeBatch())
                .thenReturn(new int[]{1, Statement.SUCCESS_NO_INFO})
                .thenReturn(new int[]{1});

        final int affected = JdbcBatchExecutor.execute(
                connection,
                "INSERT INTO test(value) VALUES (?)",
                List.of("one", "two", "three"),
                2,
                (current, value) -> current.setString(1, value));

        assertEquals(3, affected);
        verify(statement, times(3)).addBatch();
        verify(statement, times(2)).executeBatch();
    }
}
