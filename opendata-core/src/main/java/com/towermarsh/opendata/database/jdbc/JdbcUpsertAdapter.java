/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Defines record-specific existence, insert, and update operations.
 *
 * @param <T> record type
 * @param <C> operation context type, such as a plugin run identifier
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public interface JdbcUpsertAdapter<T, C> {

    /**
     * Tests whether the record already exists.
     *
     * @param connection open transaction connection
     * @param record record to test
     * @param context operation context
     * @return true when an existing row is present
     * @throws SQLException when the query fails
     */
    boolean exists(Connection connection, T record, C context) throws SQLException;

    /**
     * Inserts one record.
     *
     * @param connection open transaction connection
     * @param record record to insert
     * @param context operation context
     * @throws SQLException when insertion fails
     */
    void insert(Connection connection, T record, C context) throws SQLException;

    /**
     * Updates one record.
     *
     * @param connection open transaction connection
     * @param record record to update
     * @param context operation context
     * @throws SQLException when updating fails
     */
    void update(Connection connection, T record, C context) throws SQLException;
}
