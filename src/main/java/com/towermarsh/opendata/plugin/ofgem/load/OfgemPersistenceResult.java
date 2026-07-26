/*
 * Filename: OfgemPersistenceResult.java
 *
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.load;

/** Persistence counts returned to the generic plugin coordinator. */
public record OfgemPersistenceResult(long inserted, long updated, long skipped) {
    public OfgemPersistenceResult {
        if (inserted < 0 || updated < 0 || skipped < 0) {
            throw new IllegalArgumentException("Persistence counts must not be negative");
        }
    }
}
