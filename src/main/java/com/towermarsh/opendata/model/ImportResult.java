/*
 * Filename: ImportResult.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.model;

/**
 * Represents the outcome of an import operation.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
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

    public String getDatasetId() {
        return datasetId;
    }

    public long getRecordsProcessed() {
        return recordsProcessed;
    }

    public long getRecordsFailed() {
        return recordsFailed;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
