/*
 * Filename: DatabaseRepository.java
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

import java.util.List;
import java.util.Map;

/**
 * Defines database persistence operations.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
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
