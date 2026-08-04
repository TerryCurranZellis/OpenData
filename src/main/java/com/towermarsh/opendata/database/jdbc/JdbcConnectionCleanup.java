/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Removes connection-scoped state before a pooled JDBC connection is returned.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
@FunctionalInterface
public interface JdbcConnectionCleanup {

    /**
     * Performs connection cleanup after commit or rollback.
     *
     * @param connection open connection
     * @throws SQLException when cleanup fails
     */
    void cleanup(Connection connection) throws SQLException;
}
