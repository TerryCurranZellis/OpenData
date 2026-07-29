/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.model;

/**
 * Represents the outcome of an import operation.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class ImportResult {

    private final String datasetId;
    private final long recordsProcessed;
    private final long recordsFailed;
    private final boolean successful;

    /**
     * Creates an import result.
     *
     * @param datasetId dataset identifier
     * @param recordsProcessed number imported
     * @param recordsFailed number rejected
     * @param successful whether import completed successfully
     */
    public ImportResult(
            String datasetId,
            long recordsProcessed,
            long recordsFailed,
            boolean successful) {

        this.datasetId = datasetId;
        this.recordsProcessed = recordsProcessed;
        this.recordsFailed = recordsFailed;
        this.successful = successful;
    }

    /**
     * Returns the dataset identifier.
     *
     * @return dataset identifier
     */
    public String getDatasetId() {
        return datasetId;
    }

    /**
     * Returns the number of processed records.
     *
     * @return processed record count
     */
    public long getRecordsProcessed() {
        return recordsProcessed;
    }

    /**
     * Returns the number of failed records.
     *
     * @return failed record count
     */
    public long getRecordsFailed() {
        return recordsFailed;
    }

    /**
     * Indicates whether the import succeeded.
     *
     * @return {@code true} when the import succeeded
     */
    public boolean isSuccessful() {
        return successful;
    }
}
