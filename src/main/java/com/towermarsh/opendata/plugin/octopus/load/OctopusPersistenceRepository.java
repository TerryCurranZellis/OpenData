/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.load;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.database.jdbc.JdbcTransactionTemplate;
import com.towermarsh.opendata.database.jdbc.JdbcUpsertExecutor;
import com.towermarsh.opendata.plugin.octopus.extract.ExtractedOctopusStatement;
import com.towermarsh.opendata.plugin.octopus.transform.OctopusParseResult;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.UUID;

/**
 * Transactionally persists an Octopus statement batch and its file ledger.
 *
 * <p>Electricity and gas records use the same generic upsert executor while
 * retaining separate typed SQL adapters for their different columns.
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
public final class OctopusPersistenceRepository {

    private static final ElectricityRecordUpsertAdapter ELECTRICITY_ADAPTER
            = new ElectricityRecordUpsertAdapter();
    private static final GasRecordUpsertAdapter GAS_ADAPTER
            = new GasRecordUpsertAdapter();

    private final JdbcTransactionTemplate transactionTemplate;

    /**
     * Creates a repository backed by the supplied database resource manager.
     *
     * @param database database resource manager
     * @since 2.0.0
     */
    public OctopusPersistenceRepository(final DatabaseResourceManager database) {
        this.transactionTemplate = new JdbcTransactionTemplate(
                Objects.requireNonNull(database, "database"));
    }

    /**
     * Saves transformed Octopus billing records and statement-file metadata.
     *
     * @param batch transformed Octopus statement batch
     * @param runId plugin execution identifier
     * @return persistence counts
     * @since 2.0.0
     */
    public OctopusPersistenceResult save(
            final OctopusParseResult batch,
            final UUID runId) {
        Objects.requireNonNull(batch, "batch");
        Objects.requireNonNull(runId, "runId");

        return transactionTemplate.execute(
                "Unable to persist Octopus statement batch",
                connection -> persistBatch(connection, batch, runId));
    }

    private static OctopusPersistenceResult persistBatch(
            final Connection connection,
            final OctopusParseResult batch,
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

        for (ExtractedOctopusStatement statement : batch.statements()) {
            markCompleted(connection, statement, runId);
        }

        final var total = electricity.plus(gas);
        return new OctopusPersistenceResult(total.inserted(), total.updated(), 0);
    }

    private static void markCompleted(
            final Connection connection,
            final ExtractedOctopusStatement statement,
            final UUID runId) throws SQLException {
        final String sql = """
                MERGE octopus.statement_file WITH (HOLDLOCK) AS target
                USING (SELECT ? AS file_name, ? AS sha256) AS source
                   ON target.file_name = source.file_name
                  AND target.sha256 = source.sha256
                WHEN MATCHED THEN
                    UPDATE SET statement_date = ?,
                               size_bytes = ?,
                               status = 'COMPLETED',
                               last_run_id = ?,
                               processed_at = SYSUTCDATETIME(),
                               failure_message = NULL
                WHEN NOT MATCHED THEN
                    INSERT (file_name, statement_date, sha256, size_bytes,
                            status, last_run_id, processed_at)
                    VALUES (?, ?, ?, ?, 'COMPLETED', ?, SYSUTCDATETIME());
                """;
        try (var preparedStatement = connection.prepareStatement(sql)) {
            int parameter = 1;
            preparedStatement.setString(parameter++, statement.fileName());
            preparedStatement.setString(parameter++, statement.sha256());
            preparedStatement.setDate(parameter++, Date.valueOf(statement.statementDate()));
            preparedStatement.setLong(parameter++, statement.sizeBytes());
            preparedStatement.setObject(parameter++, runId, Types.OTHER);
            preparedStatement.setString(parameter++, statement.fileName());
            preparedStatement.setDate(parameter++, Date.valueOf(statement.statementDate()));
            preparedStatement.setString(parameter++, statement.sha256());
            preparedStatement.setLong(parameter++, statement.sizeBytes());
            preparedStatement.setObject(parameter, runId, Types.OTHER);
            preparedStatement.executeUpdate();
        }
    }
}
