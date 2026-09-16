/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.audit;

import java.time.Instant;
import java.util.Objects;

/**
 * Final counters and status for an ingestion run.
 *
 * @param status final ingestion status
 * @param rowsExtracted number of extracted source rows
 * @param rowsLoaded number of rows loaded into the target tables
 * @param rowsRejected number of rejected rows
 * @param finishedAt time when ingestion finished
 * @param message completion detail message
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public record IngestionRunCompletion(
        IngestionStatus status,
        long rowsExtracted,
        long rowsLoaded,
        long rowsRejected,
        Instant finishedAt,
        String message) {

    /**
     * Validates and normalises record components.
     *
     * @param status final ingestion status
     * @param rowsExtracted number of extracted source rows
     * @param rowsLoaded number of rows loaded into the target tables
     * @param rowsRejected number of rejected rows
     * @param finishedAt time when ingestion finished
     * @param message completion detail message
     *
     */
    public IngestionRunCompletion {
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(finishedAt, "finishedAt");
        if (status == IngestionStatus.STARTED) {
            throw new IllegalArgumentException("A completed run cannot have STARTED status");
        }
        if (rowsExtracted < 0 || rowsLoaded < 0 || rowsRejected < 0) {
            throw new IllegalArgumentException("row counts cannot be negative");
        }
        message = message == null || message.isBlank() ? null : message.trim();
    }
}
