/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.audit;

/**
 * Persisted lifecycle status for one dataset ingestion run.
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public enum IngestionStatus {

    /**
     * Started access
     */
    STARTED,

    /**
     * Completed access
     */
    SUCCEEDED,

    /**
     * completed but not everything is accepted
     */
    SUCCEEDED_WITH_REJECTIONS,

    /**
     * failed completely
     */
    FAILED,

    /**
     * cancelled
     */
    CANCELLED
}
