/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.audit;

/**
 * Persisted lifecycle status for one dataset ingestion run.
 */
public enum IngestionStatus {
    STARTED,
    SUCCEEDED,
    SUCCEEDED_WITH_REJECTIONS,
    FAILED,
    CANCELLED
}
