/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.load;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.database.jdbc.JdbcTransactionTemplate;
import com.towermarsh.opendata.database.jdbc.JdbcUpsertExecutor;
import com.towermarsh.opendata.plugin.octopusadjustment.extract.ExtractedOctopusAdjustment;
import com.towermarsh.opendata.plugin.octopusadjustment.transform.OctopusAdjustmentParseResult;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.UUID;

/**
 * Transactionally persists adjustment billing records and source completion.
 *
 * <p>All writes target adjustment-specific tables. Electricity upserts, gas
 * upserts and file-ledger completion share one transaction.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
public final class OctopusAdjustmentPersistenceRepository {

    private static final AdjustmentElectricityRecordUpsertAdapter ELECTRICITY_ADAPTER
            = new AdjustmentElectricityRecordUpsertAdapter();
    private static final AdjustmentGasRecordUpsertAdapter GAS_ADAPTER
            = new AdjustmentGasRecordUpsertAdapter();

    private final JdbcTransactionTemplate transactionTemplate;

    /**
     * Creates the repository.
     *
     * @param database database resource manager
     * @since 3.1.0
     */
    public OctopusAdjustmentPersistenceRepository(final DatabaseResourceManager database) {
        this.transactionTemplate = new JdbcTransactionTemplate(
                Objects.requireNonNull(database, "database"));
    }

    /**
     * Saves one transformed adjustment batch.
     *
     * @param batch transformed adjustment batch
     * @param runId execution run identifier
     * @return persistence counts
     * @since 3.1.0
     */
    public OctopusAdjustmentPersistenceResult save(
            final OctopusAdjustmentParseResult batch,
            final UUID runId) {
        Objects.requireNonNull(batch, "batch");
        Objects.requireNonNull(runId, "runId");
        return transactionTemplate.execute(
                "Unable to persist Octopus adjustment batch",
                connection -> persistBatch(connection, batch, runId));
    }

    private static OctopusAdjustmentPersistenceResult persistBatch(
            final Connection connection,
            final OctopusAdjustmentParseResult batch,
            final UUID runId) throws SQLException {
        final var electricity = JdbcUpsertExecutor.execute(
                connection,
                batch.electricityRecords(),
                runId,
                ELECTRICITY_ADAPTER);
        final var gas = JdbcUpsertExecutor.execute(
                connection,
                batch.gasRecords(),
                runId,
                GAS_ADAPTER);

        for (var source : batch.sources()) {
            markCompleted(connection, source, runId);
        }

        final var total = electricity.plus(gas);
        return new OctopusAdjustmentPersistenceResult(
                total.inserted(),
                total.updated(),
                0);
    }

    private static void markCompleted(
            final Connection connection,
            final ExtractedOctopusAdjustment source,
            final UUID runId) throws SQLException {
        final String sql = """
                MERGE octopus.adjustment_file WITH (HOLDLOCK) AS target
                USING (SELECT ? AS file_name, ? AS sha256) AS source
                   ON target.file_name = source.file_name
                  AND target.sha256 = source.sha256
                WHEN MATCHED THEN
                    UPDATE SET size_bytes = ?,
                               status = 'COMPLETED',
                               last_run_id = ?,
                               processed_at = SYSUTCDATETIME(),
                               failure_message = NULL
                WHEN NOT MATCHED THEN
                    INSERT (file_name, sha256, size_bytes,
                            status, last_run_id, processed_at)
                    VALUES (?, ?, ?, 'COMPLETED', ?, SYSUTCDATETIME());
                """;
        try (var statement = connection.prepareStatement(sql)) {
            int parameter = 1;
            statement.setString(parameter++, source.fileName());
            statement.setString(parameter++, source.sha256());
            statement.setLong(parameter++, source.sizeBytes());
            statement.setObject(parameter++, runId, Types.VARCHAR);
            statement.setString(parameter++, source.fileName());
            statement.setString(parameter++, source.sha256());
            statement.setLong(parameter++, source.sizeBytes());
            statement.setObject(parameter, runId, Types.VARCHAR);
            statement.executeUpdate();
        }
    }
}
