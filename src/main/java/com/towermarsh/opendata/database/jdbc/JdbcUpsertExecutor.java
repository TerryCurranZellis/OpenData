/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Runs the common record-by-record upsert control flow using a typed adapter.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class JdbcUpsertExecutor {

    private JdbcUpsertExecutor() {
        // Utility class.
    }

    /**
     * Inserts or updates every supplied record.
     *
     * @param connection open transaction connection
     * @param records records to process
     * @param context operation context
     * @param adapter record-specific SQL adapter
     * @param <T> record type
     * @param <C> context type
     * @return upsert counts
     * @throws SQLException when persistence fails
     */
    public static <T, C> JdbcUpsertResult execute(
            final Connection connection,
            final Iterable<T> records,
            final C context,
            final JdbcUpsertAdapter<T, C> adapter) throws SQLException {
        Objects.requireNonNull(connection, "connection");
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(adapter, "adapter");

        long inserted = 0;
        long updated = 0;
        for (T record : records) {
            final T requiredRecord = Objects.requireNonNull(record, "record");
            if (adapter.exists(connection, requiredRecord, context)) {
                adapter.update(connection, requiredRecord, context);
                updated++;
            } else {
                adapter.insert(connection, requiredRecord, context);
                inserted++;
            }
        }
        return new JdbcUpsertResult(inserted, updated);
    }
}
