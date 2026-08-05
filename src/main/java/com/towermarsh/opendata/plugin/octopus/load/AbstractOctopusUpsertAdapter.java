/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.load;

import com.towermarsh.opendata.database.jdbc.JdbcUpsertAdapter;
import com.towermarsh.opendata.validation.ValidationRules;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

/**
 * Common JDBC implementation for Octopus electricity and gas record upserts.
 *
 * <p>Subclasses provide only their SQL text and record-specific parameter
 * bindings. Connection, statement, existence-query, insert, and update control
 * flow is implemented once here.
 *
 * @param <T> Octopus billing record type
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
abstract class AbstractOctopusUpsertAdapter<T>
        implements JdbcUpsertAdapter<T, UUID> {

    private final String existsSql;
    private final String insertSql;
    private final String updateSql;

    /**
     * Creates an adapter using the supplied SQL statements.
     *
     * @param existsSql row-existence query
     * @param insertSql row insertion statement
     * @param updateSql row update statement
     * @since 2.0.0
     */
    AbstractOctopusUpsertAdapter(
            final String existsSql,
            final String insertSql,
            final String updateSql) {
        this.existsSql = ValidationRules.requireText(existsSql, "existsSql");
        this.insertSql = ValidationRules.requireText(insertSql, "insertSql");
        this.updateSql = ValidationRules.requireText(updateSql, "updateSql");
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
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

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
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

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
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

    /**
     * Binds the natural key beginning at the supplied parameter index.
     *
     * @param statement prepared statement
     * @param record record whose key is bound
     * @param index first parameter index
     * @return next unused parameter index
     * @throws SQLException when binding fails
     * @since 2.0.0
     */
    protected abstract int bindKey(
            PreparedStatement statement,
            T record,
            int index) throws SQLException;

    /**
     * Binds one insert operation.
     *
     * @param statement prepared insert statement
     * @param record record to insert
     * @param runId plugin execution identifier
     * @throws SQLException when binding fails
     * @since 2.0.0
     */
    protected abstract void bindInsert(
            PreparedStatement statement,
            T record,
            UUID runId) throws SQLException;

    /**
     * Binds one update operation.
     *
     * @param statement prepared update statement
     * @param record record to update
     * @param runId plugin execution identifier
     * @throws SQLException when binding fails
     * @since 2.0.0
     */
    protected abstract void bindUpdate(
            PreparedStatement statement,
            T record,
            UUID runId) throws SQLException;
}
