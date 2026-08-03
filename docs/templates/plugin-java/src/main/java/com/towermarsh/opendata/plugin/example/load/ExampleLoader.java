/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.load;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.plugin.example.transform.model.ExampleRecord;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Owns the example provider's future SQL transaction. */
public final class ExampleLoader {

    private final DatabaseResourceManager database;

    public ExampleLoader(final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    public ExampleLoadResult load(
            final List<ExampleRecord> records,
            final UUID runId) {
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(runId, "runId");

        // TODO Use database.getConnection(), disable auto-commit, write
        // parameterised batches, commit, roll back on failure and restore
        // connection state before closing.
        throw new UnsupportedOperationException(
                "Implement the example transactional load");
    }
}
