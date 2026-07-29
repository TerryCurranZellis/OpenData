/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.transform;

import com.towermarsh.opendata.plugin.example.transform.model.ExampleRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Converts extracted example values into typed domain records. */
public final class ExampleTransformer {

    public List<ExampleRecord> transform(final List<String> extracted) {
        Objects.requireNonNull(extracted, "extracted");
        final var records = new ArrayList<ExampleRecord>();
        for (int index = 0; index < extracted.size(); index++) {
            final String value = extracted.get(index).trim();
            if (!value.isEmpty()) {
                records.add(new ExampleRecord(index + 1L, value));
            }
        }
        return List.copyOf(records);
    }
}
