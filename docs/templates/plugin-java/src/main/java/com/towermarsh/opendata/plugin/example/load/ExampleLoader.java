/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.load;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.database.jdbc.JdbcBatchExecutor;
import com.towermarsh.opendata.database.jdbc.JdbcTransactionTemplate;
import com.towermarsh.opendata.plugin.example.transform.model.ExampleRecord;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Demonstrates a provider-owned SQL batch inside the shared transaction boundary.
 *
 * @since 2.0.0
 */
public final class ExampleLoader {

    private static final int BATCH_SIZE = 500;

    private static final String INSERT_SQL = """
            INSERT INTO example.ExampleData
                (SourceRow, Value, LastRunId)
            VALUES (?, ?, ?)
            """;

    private final JdbcTransactionTemplate transactions;

    /**
     * Creates the example loader.
     *
     * @param database database resource manager
     * @since 2.0.0
     */
    public ExampleLoader(final DatabaseResourceManager database) {
        transactions = new JdbcTransactionTemplate(
                Objects.requireNonNull(database, "database"));
    }

    /**
     * Inserts transformed example records.
     *
     * <p>Replace the example table and choose the provider's real idempotency
     * strategy before using this template in write mode.
     *
     * @param records records to load
     * @param runId plugin run identifier
     * @return load counts
     * @since 2.0.0
     */
    public ExampleLoadResult load(
            final List<ExampleRecord> records,
            final UUID runId) {
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(runId, "runId");

        return transactions.execute(
                "Unable to persist example records",
                connection -> {
                    final int inserted = JdbcBatchExecutor.execute(
                            connection,
                            INSERT_SQL,
                            records,
                            BATCH_SIZE,
                            (statement, record) -> {
                                statement.setLong(1, record.sourceRow());
                                statement.setString(2, record.value());
                                statement.setObject(3, runId);
                            });
                    return new ExampleLoadResult(inserted, 0, 0);
                });
    }
}
