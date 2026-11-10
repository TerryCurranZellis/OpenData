/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.database.jdbc;

import java.sql.Connection;

/**
 * Database work executed inside one JDBC transaction.
 *
 * @param <T> result type
 *
 * @author Terry Curran
 * @since 2.0.0
 * @version 4.1.0
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
