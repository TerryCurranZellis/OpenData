/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Safe placeholder for provider-owned transactional persistence.
 *
 * @author Terry Curran
 * @version 4.1.0
 */
public final class ExampleRepository {

    private final DatabaseResourceManager database;

    public ExampleRepository(
            final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(
                database, "database");
    }

    /**
     * Save.
     *
     * @param records input records
     * @param runId requested run id
     * @return result of save
     */
    public SaveResult save(
            final List<ExampleRecord> records,
            final UUID runId) {
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(runId, "runId");

        // TODO Obtain a connection from database, use one bounded transaction,
        // parameterise data values, roll back on failure and return real counts.
        throw new UnsupportedOperationException(
                "Implement example database persistence");
    }

    /**
     * Creates a save result instance.
     *
     * @param inserted input inserted
     * @param updated input updated
     * @param skipped input skipped
     */
    public record SaveResult(
            long inserted,
            long updated,
            long skipped) {
    }
}
