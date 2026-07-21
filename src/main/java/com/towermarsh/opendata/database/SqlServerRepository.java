/*
 *  Filename: SQLServerRepo.java
 * 
 *  (C) Copyright Terry Curran 2026. All rights reserved
 * 
 *  This software is provided 'as-is', without any express or implied
 *  warranty.  In no event will the author be held liable for any damages
 *  arising from the use of this software.
 * 
 *  Permission is granted to anyone to use this software for any purpose,
 *  including commercial applications, and to alter it and redistribute it
 *  freely, subject to the following restrictions:
 * 
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgement in the product documentation would be
 *     appreciated but is not required.
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *  3. This notice may not be removed or altered from any source distribution.
 * 
 *  The author may be contacted by email to the following address:
 * 
 *  terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.database;

import com.towermarsh.opendata.exception.ImportException;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * SQL Server implementation of the database repository.
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
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
    public SqlServerRepository(
            DatabaseConnectionManager connectionManager) {

        this.connectionManager
                = connectionManager;
    }

    @Override
    public long insert(
            String tableName,
            List<Map<String, String>> records)
            throws ImportException {

        if (records.isEmpty()) {
            return 0;
        }

        String sql
                = buildInsertStatement(
                        tableName,
                        records.get(0));

        long inserted = 0;

        try (Connection connection
                = connectionManager.getConnection(); PreparedStatement statement
                = connection.prepareStatement(sql)) {

            for (Map<String, String> record : records) {

                int index = 1;

                for (String column
                        : record.keySet()) {

                    statement.setString(
                            index++,
                            record.get(column));
                }

                statement.addBatch();
            }

            int[] results
                    = statement.executeBatch();

            for (int result : results) {

                inserted += result;
            }

            return inserted;

        } catch (SQLException ex) {

            throw new ImportException(
                    "Database insert failed",
                    ex);
        }
    }

    @Override
    public boolean tableExists(
            String tableName)
            throws ImportException {

        String sql
                = """
            SELECT COUNT(*)
            FROM INFORMATION_SCHEMA.TABLES
            WHERE TABLE_NAME = ?
            """;

        try (Connection connection
                = connectionManager.getConnection(); PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    tableName);

            ResultSet result
                    = statement.executeQuery();

            result.next();

            return result.getInt(1) > 0;

        } catch (SQLException ex) {

            throw new ImportException(
                    "Unable to check table",
                    ex);
        }
    }

    private String buildInsertStatement(
            String tableName,
            Map<String, String> record) {

        String columns
                = String.join(
                        ",",
                        record.keySet());

        String values
                = String.join(
                        ",",
                        record.keySet()
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
