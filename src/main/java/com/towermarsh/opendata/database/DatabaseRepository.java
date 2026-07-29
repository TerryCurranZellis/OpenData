/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import com.towermarsh.opendata.exception.ImportException;

import java.util.List;
import java.util.Map;

/**
 * Defines database persistence operations.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public interface DatabaseRepository {

    /**
     * Inserts records into a database table.
     *
     * @param tableName destination table
     * @param records records to insert
     * @return number of rows inserted
     *
     * @throws ImportException if insertion fails
     */
    long insert(
            String tableName,
            List<Map<String, String>> records)
            throws ImportException;

    /**
     * Checks whether a table exists.
     *
     * @param tableName table name
     * @return true if table exists
     *
     * @throws ImportException if query fails
     */
    boolean tableExists(
            String tableName)
            throws ImportException;
}
