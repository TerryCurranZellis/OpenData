/*
 * Filename: SqlServerRepository.java
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

import com.towermarsh.opendata.exception.ImportException;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * SQL Server implementation of the database repository.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class SqlServerRepository
        implements DatabaseRepository {

    private final DatabaseConnectionManager connectionManager;

    /**
     * Creates a SQL Server repository.
     *
     * @param connectionManager database connection manager
     */
    public SqlServerRepository(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Insert data into the database table
     *
     * @param tableName database table name
     * @param records list of records to insert
     * @return number of records inserted
     * @throws ImportException
     */
    @Override
    public long insert(String tableName, List<Map<String, String>> records) throws ImportException {
        if (records.isEmpty()) {
            return 0;
        }
        var sql = buildInsertStatement(tableName, records.get(0));
        var inserted = 0L;
        try (var connection
                = connectionManager.getConnection(); var statement
                = connection.prepareStatement(sql)) {
            for (var record : records) {
                var index = 1;
                for (var column : record.keySet()) {
                    statement.setString(index++, record.get(column));
                }
                statement.addBatch();
            }
            var results = statement.executeBatch();
            for (var result : results) {
                inserted += result;
            }
            return inserted;
        } catch (SQLException ex) {
            throw new ImportException("Database insert failed", ex);
        }
    }

    /**
     * Check if the database table exists
     *
     * @param tableName name of table
     * @return true or false
     * @throws ImportException
     */
    @Override
    public boolean tableExists(String tableName) throws ImportException {
        var sql = """
            SELECT COUNT(*)
            FROM INFORMATION_SCHEMA.TABLES
            WHERE TABLE_NAME = ?
            """;
        try (var connection
                = connectionManager.getConnection(); var statement
                = connection.prepareStatement(sql)) {
            statement.setString(1, tableName);
            var result = statement.executeQuery();
            result.next();
            return result.getInt(1) > 0;
        } catch (SQLException ex) {
            throw new ImportException("Unable to check table", ex);
        }
    }

    /**
     * Build the insert statement
     *
     * @param tableName name of table
     * @param record list of records
     * @return
     */
    private String buildInsertStatement(String tableName, Map<String, String> record) {
        var columns = String.join(",", record.keySet());
        var values = String.join(",", record.keySet()
                .stream()
                .map(c -> "?")
                .toList());
        return "INSERT INTO "
                + tableName
                + " ("
                + columns
                + ") VALUES ("
                + values
                + ")";
    }
}
