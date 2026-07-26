/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.load;

/** Row counts returned by the example transactional loader. */
public record ExampleLoadResult(long inserted, long updated, long skipped) {
    public ExampleLoadResult {
        if (inserted < 0 || updated < 0 || skipped < 0) {
            throw new IllegalArgumentException("Load counts must not be negative");
        }
    }
}
