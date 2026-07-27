/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.audit;

import java.net.URI;
import java.sql.SQLException;

/**
 * Persists the shared ingestion audit trail.
 */
public interface IngestionAuditRepository {

    /**
     *
     * @param datasetCode
     * @param sourcePageUri
     * @param applicationVersion
     * @return
     * @throws SQLException
     */
    long startRun(String datasetCode, URI sourcePageUri, String applicationVersion)
            throws SQLException;

    /**
     *
     * @param ingestionRunId
     * @param metadata
     * @return
     * @throws SQLException
     */
    long registerSourceFile(long ingestionRunId, SourceFileMetadata metadata)
            throws SQLException;

    /**
     *
     * @param ingestionRunId
     * @param completion
     * @throws SQLException
     */
    void completeRun(long ingestionRunId, IngestionRunCompletion completion)
            throws SQLException;

    /**
     *
     * @param ingestionRunId
     * @param sourceFileId
     * @param sourceRowNumber
     * @param stage
     * @param errorCode
     * @param message
     * @param rawPayload
     * @throws SQLException
     */
    void recordError(
            long ingestionRunId,
            Long sourceFileId,
            Long sourceRowNumber,
            String stage,
            String errorCode,
            String message,
            String rawPayload) throws SQLException;
}
