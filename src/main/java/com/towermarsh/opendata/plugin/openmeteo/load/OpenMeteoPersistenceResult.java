/*
 * Filename: OpenMeteoPersistenceResult.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.load;

/** SQL Server row counts for one Open-Meteo load.
 * @param inserted number of inserted rows
 * @param updated number of updated rows
 * @param skipped number of skipped rows
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record OpenMeteoPersistenceResult(long inserted, long updated, long skipped) {
    /** Validates and normalises record components. */
    public OpenMeteoPersistenceResult {
        if (inserted < 0 || updated < 0 || skipped < 0) {
            throw new IllegalArgumentException("Persistence counts must not be negative.");
        }
    }
}
