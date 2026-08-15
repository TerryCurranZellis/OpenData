/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.load;

/**
 * Combined persistence counts for one adjustment batch.
 *
 * @param inserted rows inserted
 * @param updated rows updated
 * @param skipped rows skipped
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
public record OctopusAdjustmentPersistenceResult(
        long inserted,
        long updated,
        long skipped) {

    /** Validates non-negative counts. */
    public OctopusAdjustmentPersistenceResult {
        if (inserted < 0 || updated < 0 || skipped < 0) {
            throw new IllegalArgumentException("Persistence counts must not be negative");
        }
    }
}
