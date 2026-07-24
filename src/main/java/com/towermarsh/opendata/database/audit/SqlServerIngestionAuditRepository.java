/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.audit;

import com.towermarsh.opendata.database.DatabaseConnectionManager;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

/**
 * SQL Server implementation of the framework ingestion audit repository.
 */
public final class SqlServerIngestionAuditRepository
        implements IngestionAuditRepository {

    private static final String START_RUN_SQL = """
            INSERT INTO core.ingestion_run
                (dataset_id, status, started_at, source_page_url,
                 host_name, application_version)
            SELECT dataset_id, 'STARTED', SYSUTCDATETIME(), ?, ?, ?
            FROM core.dataset
            WHERE dataset_code = ?
            """;

    private static final String SOURCE_FILE_SQL = """
            INSERT INTO core.source_file
                (ingestion_run_id, source_uri, original_file_name, content_type,
                 size_bytes, sha256, downloaded_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String COMPLETE_RUN_SQL = """
            UPDATE core.ingestion_run
            SET status = ?,
                finished_at = ?,
                rows_extracted = ?,
                rows_loaded = ?,
                rows_rejected = ?,
                status_message = ?
            WHERE ingestion_run_id = ?
              AND status = 'STARTED'
            """;

    private static final String ERROR_SQL = """
            INSERT INTO core.ingestion_error
                (ingestion_run_id, source_file_id, source_row_number,
                 processing_stage, error_code, error_message, raw_payload)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private final DatabaseConnectionManager connectionManager;

    public SqlServerIngestionAuditRepository(
            DatabaseConnectionManager connectionManager) {
        this.connectionManager = Objects.requireNonNull(
                connectionManager, "connectionManager");
    }

    @Override
    public long startRun(
            String datasetCode,
            URI sourcePageUri,
            String applicationVersion) throws SQLException {
        Objects.requireNonNull(datasetCode, "datasetCode");
        try (Connection connection = connectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        START_RUN_SQL,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, sourcePageUri == null ? null : sourcePageUri.toString());
            statement.setString(2, hostName());
            statement.setString(3, applicationVersion);
            statement.setString(4, datasetCode);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Unknown dataset code: " + datasetCode);
            }
            return generatedKey(statement, "ingestion run");
        }
    }

    @Override
    public long registerSourceFile(
            long ingestionRunId,
            SourceFileMetadata metadata) throws SQLException {
        Objects.requireNonNull(metadata, "metadata");
        try (Connection connection = connectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        SOURCE_FILE_SQL,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, ingestionRunId);
            statement.setString(2, metadata.sourceUri().toString());
            statement.setString(3, metadata.fileName());
            statement.setString(4, metadata.contentType());
            statement.setLong(5, metadata.sizeBytes());
            statement.setString(6, metadata.sha256());
            statement.setTimestamp(7, Timestamp.from(metadata.downloadedAt()));
            statement.executeUpdate();
            return generatedKey(statement, "source file");
        }
    }

    @Override
    public void completeRun(
            long ingestionRunId,
            IngestionRunCompletion completion) throws SQLException {
        Objects.requireNonNull(completion, "completion");
        try (Connection connection = connectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        COMPLETE_RUN_SQL)) {
            statement.setString(1, completion.status().name());
            statement.setTimestamp(2, Timestamp.from(completion.finishedAt()));
            statement.setLong(3, completion.rowsExtracted());
            statement.setLong(4, completion.rowsLoaded());
            statement.setLong(5, completion.rowsRejected());
            statement.setString(6, completion.message());
            statement.setLong(7, ingestionRunId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException(
                        "Ingestion run is missing or is no longer STARTED: "
                        + ingestionRunId);
            }
        }
    }

    @Override
    public void recordError(
            long ingestionRunId,
            Long sourceFileId,
            Long sourceRowNumber,
            String stage,
            String errorCode,
            String message,
            String rawPayload) throws SQLException {
        try (Connection connection = connectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(ERROR_SQL)) {
            statement.setLong(1, ingestionRunId);
            setNullableLong(statement, 2, sourceFileId);
            setNullableLong(statement, 3, sourceRowNumber);
            statement.setString(4, stage);
            statement.setString(5, errorCode);
            statement.setString(6, message);
            statement.setString(7, rawPayload);
            statement.executeUpdate();
        }
    }

    private static long generatedKey(
            PreparedStatement statement,
            String description) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("No generated key returned for " + description);
            }
            return keys.getLong(1);
        }
    }

    private static void setNullableLong(
            PreparedStatement statement,
            int index,
            Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, java.sql.Types.BIGINT);
        } else {
            statement.setLong(index, value);
        }
    }

    private static String hostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException exception) {
            return "unknown";
        }
    }
}
