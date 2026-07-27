package com.towermarsh.opendata.plugin.example;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** SQL persistence template. Replace the placeholder result with a transactional JDBC implementation. */
public final class ExampleRepository {
    private final DatabaseResourceManager database;

    public ExampleRepository(final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    public SaveResult save(final List<ExampleRecord> records, final UUID runId) {
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(runId, "runId");
        // Use database.getConnection(), disable auto-commit, batch statements, commit, and roll back on failure.
        return new SaveResult(records.size(), 0, 0);
    }

    public record SaveResult(long inserted, long updated, long skipped) {
    }
}
