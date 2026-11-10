/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example;

import java.util.Objects;

/**
 * One immutable transformed record.
 *
 * @param sourceKey source identifier for the transformed record
 * @param value source value to persist
 *
 * @author Terry Curran
 * @version 4.1.0
 */
public record ExampleRecord(String sourceKey, String value) {

    public ExampleRecord {
        sourceKey = Objects.requireNonNull(
                sourceKey, "sourceKey").trim();
        value = Objects.requireNonNull(value, "value").trim();
        if (sourceKey.isEmpty() || value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Example fields must not be blank");
        }
    }
}
