/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.load;

import com.towermarsh.opendata.database.jdbc.JdbcUpsertAdapter;
import com.towermarsh.opendata.validation.ValidationRules;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

/**
 * Common JDBC control flow for adjustment electricity and gas upserts.
 *
 * @param <T> billing record type
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
abstract class AbstractAdjustmentUpsertAdapter<T>
        implements JdbcUpsertAdapter<T, UUID> {

    private final String existsSql;
    private final String insertSql;
    private final String updateSql;

    AbstractAdjustmentUpsertAdapter(
            final String existsSql,
            final String insertSql,
            final String updateSql) {
        this.existsSql = ValidationRules.requireText(existsSql, "existsSql");
        this.insertSql = ValidationRules.requireText(insertSql, "insertSql");
        this.updateSql = ValidationRules.requireText(updateSql, "updateSql");
    }

    /** {@inheritDoc} */
    @Override
    public final boolean exists(
            final Connection connection,
            final T record,
            final UUID runId) throws SQLException {
        Objects.requireNonNull(connection, "connection");
        Objects.requireNonNull(record, "record");
        Objects.requireNonNull(runId, "runId");
        try (PreparedStatement statement = connection.prepareStatement(existsSql)) {
            bindKey(statement, record, 1);
            try (var result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void insert(
            final Connection connection,
            final T record,
            final UUID runId) throws SQLException {
        Objects.requireNonNull(connection, "connection");
        Objects.requireNonNull(record, "record");
        Objects.requireNonNull(runId, "runId");
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            bindInsert(statement, record, runId);
            statement.executeUpdate();
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void update(
            final Connection connection,
            final T record,
            final UUID runId) throws SQLException {
        Objects.requireNonNull(connection, "connection");
        Objects.requireNonNull(record, "record");
        Objects.requireNonNull(runId, "runId");
        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            bindUpdate(statement, record, runId);
            statement.executeUpdate();
        }
    }

    protected abstract int bindKey(
            PreparedStatement statement,
            T record,
            int index) throws SQLException;

    protected abstract void bindInsert(
            PreparedStatement statement,
            T record,
            UUID runId) throws SQLException;

    protected abstract void bindUpdate(
            PreparedStatement statement,
            T record,
            UUID runId) throws SQLException;
}
