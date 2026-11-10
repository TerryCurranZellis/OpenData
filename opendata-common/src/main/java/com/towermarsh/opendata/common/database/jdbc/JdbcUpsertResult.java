/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.database.jdbc;

/**
 * Summary of one generic JDBC upsert operation.
 *
 * @param inserted inserted record count
 * @param updated updated record count
 *
 * @author Terry Curran
 * @since 2.0.0
 * @version 4.1.0
 */
public record JdbcUpsertResult(long inserted, long updated) {

    /**
     * Rejects negative inserted and updated counts.
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
     * @return sum of inserted and updated counts
     */
    public long processed() {
        return Math.addExact(inserted, updated);
    }

    /**
     * Combines two upsert results.
     *
     * @param other result to combine
     * @return combined result, rejecting arithmetic overflow
     */
    public JdbcUpsertResult plus(JdbcUpsertResult other) {
        return new JdbcUpsertResult(
                Math.addExact(inserted, other.inserted),
                Math.addExact(updated, other.updated));
    }
}
