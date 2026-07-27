/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.audit;

import java.net.URI;
import java.sql.SQLException;

/**
 * Persists the shared ingestion audit trail.
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public interface IngestionAuditRepository {

    long startRun(String datasetCode, URI sourcePageUri, String applicationVersion)
            throws SQLException;

    long registerSourceFile(long ingestionRunId, SourceFileMetadata metadata)
            throws SQLException;

    void completeRun(long ingestionRunId, IngestionRunCompletion completion)
            throws SQLException;

    void recordError(
            long ingestionRunId,
            Long sourceFileId,
            Long sourceRowNumber,
            String stage,
            String errorCode,
            String message,
            String rawPayload) throws SQLException;
}
