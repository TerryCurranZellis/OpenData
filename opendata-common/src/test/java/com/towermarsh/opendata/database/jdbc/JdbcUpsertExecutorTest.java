/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests generic typed upsert control flow.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
class JdbcUpsertExecutorTest {

    @Test
    void delegatesRecordSpecificInsertAndUpdateOperations() throws SQLException {
        final Connection connection = mock(Connection.class);
        final List<String> operations = new ArrayList<>();
        final JdbcUpsertAdapter<String, String> adapter = new JdbcUpsertAdapter<>() {
            @Override
            public boolean exists(
                    final Connection current,
                    final String record,
                    final String context) {
                return record.startsWith("existing");
            }

            @Override
            public void insert(
                    final Connection current,
                    final String record,
                    final String context) {
                operations.add("insert:" + record + ':' + context);
            }

            @Override
            public void update(
                    final Connection current,
                    final String record,
                    final String context) {
                operations.add("update:" + record + ':' + context);
            }
        };

        final JdbcUpsertResult result = JdbcUpsertExecutor.execute(
                connection,
                List.of("new-electricity", "existing-gas"),
                "run-1",
                adapter);

        assertEquals(1, result.inserted());
        assertEquals(1, result.updated());
        assertEquals(2, result.processed());
        assertEquals(
                List.of("insert:new-electricity:run-1", "update:existing-gas:run-1"),
                operations);
    }
}
