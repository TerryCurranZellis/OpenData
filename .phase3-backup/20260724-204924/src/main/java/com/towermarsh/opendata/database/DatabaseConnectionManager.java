/*
 * Filename: DatabaseConnectionManager.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.database;

import com.towermarsh.opendata.config.ApplicationConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages JDBC database connections.
 *
 * <p>
 * This class provides centralised connection creation so database configuration
 * is handled consistently.</p>
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class DatabaseConnectionManager {

    private final ApplicationConfig config;

    /**
     * Creates a database connection manager.
     *
     * @param config application configuration
     */
    public DatabaseConnectionManager(
            ApplicationConfig config) {

        this.config = config;
    }

    /**
     * Opens a database connection.
     *
     * @return JDBC connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                config.bootstrap().values().get("database.url"),
                config.bootstrap().values().get("database.user"),
                config.bootstrap().values().get("database.password"));
    }
}
