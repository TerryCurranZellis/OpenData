/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.jdbc;

/**
 * Summary of one generic JDBC upsert operation.
 *
 * @param inserted inserted record count
 * @param updated updated record count
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public record JdbcUpsertResult(long inserted, long updated) {

    /**
     * Validates result counts.
     *
     * @param inserted inserted record count
     * @param updated updated record count
     *
     */
    public JdbcUpsertResult {
        if (inserted < 0 || updated < 0) {
            throw new IllegalArgumentException("Upsert counts must not be negative");
        }
    }

    /**
     * Returns the total records processed.
     *
     * @return total record count
     */
    public long processed() {
        return Math.addExact(inserted, updated);
    }

    /**
     * Combines two upsert results.
     *
     * @param other result to combine
     * @return combined result
     */
    public JdbcUpsertResult plus(final JdbcUpsertResult other) {
        return new JdbcUpsertResult(
                Math.addExact(inserted, other.inserted),
                Math.addExact(updated, other.updated));
    }
}
