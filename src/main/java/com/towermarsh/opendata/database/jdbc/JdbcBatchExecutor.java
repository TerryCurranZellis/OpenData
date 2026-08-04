/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * Executes typed prepared-statement batches with consistent result counting.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class JdbcBatchExecutor {

    private JdbcBatchExecutor() {
        // Utility class.
    }

    /**
     * Binds and executes records in batches.
     *
     * @param connection open connection
     * @param sql parameterised SQL statement
     * @param records records to write
     * @param batchSize maximum records per execution
     * @param binder record binder
     * @param <T> record type
     * @return affected-row count
     * @throws SQLException when statement execution fails
     */
    public static <T> int execute(
            final Connection connection,
            final String sql,
            final Iterable<T> records,
            final int batchSize,
            final JdbcStatementBinder<T> binder) throws SQLException {
        Objects.requireNonNull(connection, "connection");
        if (sql == null || sql.isBlank()) {
            throw new IllegalArgumentException("sql must not be blank");
        }
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(binder, "binder");
        if (batchSize < 1) {
            throw new IllegalArgumentException("batchSize must be positive");
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int pending = 0;
            int affected = 0;
            for (T record : records) {
                binder.bind(statement, Objects.requireNonNull(record, "record"));
                statement.addBatch();
                pending++;
                if (pending == batchSize) {
                    affected += count(statement.executeBatch());
                    pending = 0;
                }
            }
            if (pending > 0) {
                affected += count(statement.executeBatch());
            }
            return affected;
        }
    }

    private static int count(final int[] results) throws SQLException {
        int affected = 0;
        for (int result : results) {
            if (result == Statement.EXECUTE_FAILED) {
                throw new SQLException("A JDBC batch entry failed");
            }
            if (result == Statement.SUCCESS_NO_INFO) {
                affected++;
            } else {
                affected += Math.max(result, 0);
            }
        }
        return affected;
    }
}
