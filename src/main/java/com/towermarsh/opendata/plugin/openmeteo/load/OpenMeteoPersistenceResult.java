/*
 * Filename: OpenMeteoPersistenceResult.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.load;

/** SQL Server row counts for one Open-Meteo load. */
public record OpenMeteoPersistenceResult(long inserted, long updated, long skipped) {
    public OpenMeteoPersistenceResult {
        if (inserted < 0 || updated < 0 || skipped < 0) {
            throw new IllegalArgumentException("Persistence counts must not be negative.");
        }
    }
}
