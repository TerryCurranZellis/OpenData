/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.audit;

import com.towermarsh.opendata.database.DatabaseConnectionManager;
import com.towermarsh.opendata.database.DatabaseException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * SQL Server implementation of the framework ingestion audit repository.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class SqlServerIngestionAuditRepository
        implements IngestionAuditRepository {

    /**
     * insert data set details
     */
    private static final String START_RUN_SQL = """
            INSERT INTO core.ingestion_run
                (dataset_id, status, started_at, source_page_url,
                 host_name, application_version)
            SELECT dataset_id, 'STARTED', SYSUTCDATETIME(), ?, ?, ?
            FROM core.dataset
            WHERE dataset_code = ?
            """;

    /**
     * insert source file detail
     */
    private static final String SOURCE_FILE_SQL = """
            INSERT INTO core.source_file
                (ingestion_run_id, source_uri, original_file_name, content_type,
                 size_bytes, sha256, downloaded_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    /**
     * insert completion status
     */
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

    /**
     * insert error state
     */
    private static final String ERROR_SQL = """
            INSERT INTO core.ingestion_error
                (ingestion_run_id, source_file_id, source_row_number,
                 processing_stage, error_code, error_message, raw_payload)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private final DatabaseConnectionManager connectionManager;

    /**
     * Instantiate
     *
     * @param connectionManager connection
     */
    public SqlServerIngestionAuditRepository(
            DatabaseConnectionManager connectionManager) {
        this.connectionManager = Objects.requireNonNull(
                connectionManager, "connectionManager");
    }

    /**
     * start run details
     *
     * @param datasetCode
     * @param sourcePageUri
     * @param applicationVersion
     * @return
     * @throws DatabaseException
     */
    @Override
    public long startRun(
            String datasetCode,
            URI sourcePageUri,
            String applicationVersion) throws DatabaseException {
        Objects.requireNonNull(datasetCode, "datasetCode");
        try (var connection = connectionManager.getConnection(); var statement
                = connection.prepareStatement(START_RUN_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, sourcePageUri == null ? null : sourcePageUri.toString());
            statement.setString(2, hostName());
            statement.setString(3, applicationVersion);
            statement.setString(4, datasetCode);
            if (statement.executeUpdate() != 1) {
                throw new DatabaseException("Unknown dataset code: " + datasetCode);
            }
            return generatedKey(statement, "ingestion run");
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * register file details
     *
     * @param ingestionRunId
     * @param metadata
     * @return
     * @throws DatabaseException
     */
    @Override
    public long registerSourceFile(
            long ingestionRunId,
            SourceFileMetadata metadata) throws DatabaseException {
        Objects.requireNonNull(metadata, "metadata");
        try (var connection = connectionManager.getConnection(); var statement
                = connection.prepareStatement(SOURCE_FILE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, ingestionRunId);
            statement.setString(2, metadata.sourceUri().toString());
            statement.setString(3, metadata.fileName());
            statement.setString(4, metadata.contentType());
            statement.setLong(5, metadata.sizeBytes());
            statement.setString(6, metadata.sha256());
            statement.setTimestamp(7, Timestamp.from(metadata.downloadedAt()));
            statement.executeUpdate();
            return generatedKey(statement, "source file");
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * final status
     *
     * @param ingestionRunId
     * @param completion
     * @throws DatabaseException
     */
    @Override
    public void completeRun(
            long ingestionRunId,
            IngestionRunCompletion completion) throws DatabaseException {
        Objects.requireNonNull(completion, "completion");
        try (var connection = connectionManager.getConnection(); var statement
                = connection.prepareStatement(COMPLETE_RUN_SQL)) {
            statement.setString(1, completion.status().name());
            statement.setTimestamp(2, Timestamp.from(completion.finishedAt()));
            statement.setLong(3, completion.rowsExtracted());
            statement.setLong(4, completion.rowsLoaded());
            statement.setLong(5, completion.rowsRejected());
            statement.setString(6, completion.message());
            statement.setLong(7, ingestionRunId);
            if (statement.executeUpdate() != 1) {
                throw new DatabaseException(
                        "Ingestion run is missing or is no longer STARTED: "
                        + ingestionRunId);
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * record errors
     *
     * @param ingestionRunId
     * @param sourceFileId
     * @param sourceRowNumber
     * @param stage
     * @param errorCode
     * @param message
     * @param rawPayload
     * @throws DatabaseException
     */
    @Override
    public void recordError(
            long ingestionRunId,
            Long sourceFileId,
            Long sourceRowNumber,
            String stage,
            String errorCode,
            String message,
            String rawPayload) throws DatabaseException {
        try (Connection connection = connectionManager.getConnection(); var statement
                = connection.prepareStatement(ERROR_SQL)) {
            statement.setLong(1, ingestionRunId);
            setNullableLong(statement, 2, sourceFileId);
            setNullableLong(statement, 3, sourceRowNumber);
            statement.setString(4, stage);
            statement.setString(5, errorCode);
            statement.setString(6, message);
            statement.setString(7, rawPayload);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * generate table keys
     *
     * @param statement
     * @param description
     * @return
     * @throws DatabaseException
     */
    private static long generatedKey(PreparedStatement statement, String description) throws DatabaseException {
        try (var keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("No generated key returned for " + description);
            }
            return keys.getLong(1);
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * fix null values
     *
     * @param statement
     * @param index
     * @param value
     * @throws DatabaseException
     */
    private static void setNullableLong(
            PreparedStatement statement,
            int index,
            Long value) throws DatabaseException {
        try {
            if (value == null) {
                statement.setNull(index, java.sql.Types.BIGINT);
            } else {
                statement.setLong(index, value);
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * get name from url
     *
     * @return
     */
    private static String hostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException exception) {
            return "unknown";
        }
    }
}
