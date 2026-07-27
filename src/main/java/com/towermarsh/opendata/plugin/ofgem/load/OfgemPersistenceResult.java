/*
 * Filename: OfgemPersistenceResult.java
 *
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.load;

/** Persistence counts returned to the generic plugin coordinator.
 * @param inserted number of inserted rows
 * @param updated number of updated rows
 * @param skipped number of skipped rows
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record OfgemPersistenceResult(long inserted, long updated, long skipped) {
    /** Validates and normalises record components. */
    public OfgemPersistenceResult {
        if (inserted < 0 || updated < 0 || skipped < 0) {
            throw new IllegalArgumentException("Persistence counts must not be negative");
        }
    }
}
