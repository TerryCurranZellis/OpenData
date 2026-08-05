/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.jdbc;

import java.sql.Connection;

/**
 * Database work executed inside one JDBC transaction.
 *
 * @param <T> result type
 *
 * @author Terry Curran
 * @version 2.0.0
 */
@FunctionalInterface
public interface JdbcTransaction<T> {

    /**
     * Executes transactional work.
     *
     * @param connection open connection with auto-commit disabled
     * @return transaction result
     * @throws Exception when processing fails
     */
    T execute(Connection connection) throws Exception;
}
