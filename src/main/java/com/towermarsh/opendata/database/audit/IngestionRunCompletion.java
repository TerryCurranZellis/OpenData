/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.audit;

import java.time.Instant;
import java.util.Objects;

/**
 * Final counters and status for an ingestion run.
 */
public record IngestionRunCompletion(
        IngestionStatus status,
        long rowsExtracted,
        long rowsLoaded,
        long rowsRejected,
        Instant finishedAt,
        String message) {

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
