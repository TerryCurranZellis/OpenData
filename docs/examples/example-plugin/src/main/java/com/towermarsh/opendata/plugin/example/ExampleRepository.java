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

/** Safe placeholder for provider-owned transactional persistence. */
public final class ExampleRepository {

    private final DatabaseResourceManager database;

    public ExampleRepository(
            final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(
                database, "database");
    }

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

    public record SaveResult(
            long inserted,
            long updated,
            long skipped) {
    }
}
