/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.database.jdbc;

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
 * @since 2.0.0
 * @version 4.1.0
 */
class JdbcUpsertExecutorTest {

    @Test
    void delegatesRecordSpecificInsertAndUpdateOperations() throws SQLException {
        var connection = mock(Connection.class);
        var operations = new ArrayList<String>();
        final JdbcUpsertAdapter<String, String> adapter = new JdbcUpsertAdapter<>() {
            @Override
            public boolean exists(
                    Connection current,
                    String record,
                    String context) {
                return record.startsWith("existing");
            }

            @Override
            public void insert(
                    Connection current,
                    String record,
                    String context) {
                operations.add("insert:" + record + ':' + context);
            }

            @Override
            public void update(
                    Connection current,
                    String record,
                    String context) {
                operations.add("update:" + record + ':' + context);
            }
        };

        var result = JdbcUpsertExecutor.execute(
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
    /**
     * Confirms an adapter declared for a base record type handles subtype inputs.
     *
     * @throws SQLException when a mocked SQL operation fails
     */
    @Test
    void supportsSubtypeRecordsWithSupertypeAdapter() throws SQLException {
        var connection = mock(Connection.class);
        var seen = new ArrayList<Integer>();
        JdbcUpsertAdapter<Number, String> adapter = new JdbcUpsertAdapter<>() {
            @Override
            public boolean exists(Connection current, Number record, String context) {
                return record.intValue() == 2;
            }

            @Override
            public void insert(Connection current, Number record, String context) {
                seen.add(record.intValue());
            }

            @Override
            public void update(Connection current, Number record, String context) {
                seen.add(record.intValue());
            }
        };

        var result = JdbcUpsertExecutor.execute(connection, List.of(1, 2), "batch", adapter);

        assertEquals(1, result.inserted());
        assertEquals(1, result.updated());
        assertEquals(List.of(1, 2), seen);
    }

}
