/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Binds one typed record to a prepared JDBC statement.
 *
 * @param <T> record type
 *
 * @author Terry Curran
 * @version 2.0.0
 */
@FunctionalInterface
public interface JdbcStatementBinder<T> {

    /**
     * Binds one record.
     *
     * @param statement prepared statement
     * @param record record to bind
     * @throws SQLException when binding fails
     */
    void bind(PreparedStatement statement, T record) throws SQLException;
}
