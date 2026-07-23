/*
 * Filename: LoadService.java
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
package com.towermarsh.opendata.etl;

import com.towermarsh.opendata.database.DatabaseRepository;
import com.towermarsh.opendata.exception.ImportException;
import com.towermarsh.opendata.model.ImportResult;

import java.util.List;
import java.util.Map;

/**
 * Loads transformed data into persistent storage.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class LoadService {

    private final DatabaseRepository repository;

    /**
     * Creates a load service.
     *
     * @param repository database repository
     */
    public LoadService(
            DatabaseRepository repository) {

        this.repository = repository;
    }

    /**
     * Loads records into a database table.
     *
     * @param datasetId dataset identifier
     * @param tableName destination table
     * @param records records to load
     *
     * @return import result
     *
     * @throws ImportException if loading fails
     */
    public ImportResult load(
            String datasetId,
            String tableName,
            List<Map<String, String>> records)
            throws ImportException {

        long inserted
                = repository.insert(
                        tableName,
                        records);

        return new ImportResult(
                datasetId,
                inserted,
                records.size() - inserted,
                true);
    }
}
