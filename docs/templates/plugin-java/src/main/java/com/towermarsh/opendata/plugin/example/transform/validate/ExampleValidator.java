/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.transform.validate;

import com.towermarsh.opendata.plugin.example.transform.model.ExampleRecord;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/** Enforces cross-record rules after example transformation. */
public final class ExampleValidator {

    public List<ExampleRecord> validate(final List<ExampleRecord> records) {
        Objects.requireNonNull(records, "records");
        if (records.isEmpty()) {
            throw new IllegalArgumentException("The example source produced no records");
        }
        final var values = new HashSet<String>();
        for (var record : records) {
            if (!values.add(record.value())) {
                throw new IllegalArgumentException(
                        "Duplicate example value: " + record.value());
            }
        }
        return List.copyOf(records);
    }
}
