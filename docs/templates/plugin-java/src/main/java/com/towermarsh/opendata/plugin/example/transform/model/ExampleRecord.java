/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.transform.model;

import java.util.Objects;

/** One typed example domain record. */
public record ExampleRecord(long sourceRow, String value) {
    public ExampleRecord {
        if (sourceRow < 1) {
            throw new IllegalArgumentException("sourceRow must be positive");
        }
        value = Objects.requireNonNull(value, "value").trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }
}
