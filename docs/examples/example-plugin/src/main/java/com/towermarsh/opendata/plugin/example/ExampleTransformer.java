/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example;

import java.util.List;

/**
 * Placeholder transformation; replace with real parsing and validation.
 *
 * @author Terry Curran
 * @version 4.1.0
 */
public final class ExampleTransformer {

    /**
     * Transform.
     *
     * @param payload input payload
     * @return result of transform
     */
    public List<ExampleRecord> transform(final String payload) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException(
                    "Example source returned no data");
        }
        return List.of(new ExampleRecord(
                "replace-me",
                payload));
    }
}
