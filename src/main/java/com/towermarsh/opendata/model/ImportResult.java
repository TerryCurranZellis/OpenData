/*
 *  Filename: ImportResult.java
 * 
 *  (C) Copyright Terry Curran 2026. All rights reserved
 * 
 *  This software is provided 'as-is', without any express or implied
 *  warranty.  In no event will the author be held liable for any damages
 *  arising from the use of this software.
 * 
 *  Permission is granted to anyone to use this software for any purpose,
 *  including commercial applications, and to alter it and redistribute it
 *  freely, subject to the following restrictions:
 * 
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgement in the product documentation would be
 *     appreciated but is not required.
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *  3. This notice may not be removed or altered from any source distribution.
 * 
 *  The author may be contacted by email to the following address:
 * 
 *  terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.model;

/**
 * Represents the outcome of an import operation.
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
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
