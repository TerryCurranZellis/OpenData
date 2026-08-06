/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.etl;

import com.towermarsh.opendata.database.DatabaseRepository;
import com.towermarsh.opendata.exception.ImportException;
import com.towermarsh.opendata.model.ImportResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Loads transformed data into persistent storage.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class LoadService {

    private final Loader loader;

    /**
     * Creates a load service.
     *
     * @param repository database repository
     */
    public LoadService(
            DatabaseRepository repository) {

        this.loader = Objects.requireNonNull(repository, "repository")::insert;
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
                = loader.insert(
                        tableName,
                        records);

        return new ImportResult(
                datasetId,
                inserted,
                records.size() - inserted,
                true);
    }

    @FunctionalInterface
    private interface Loader {
        long insert(
                String tableName,
                List<Map<String, String>> records)
                throws ImportException;
    }
}
