/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example;

import java.util.List;

/** Template transformation stage. Replace this placeholder with real parsing and validation. */
public final class ExampleTransformer {
    public List<ExampleRecord> transform(final String payload) {
        if (payload == null || payload.isBlank()) {
            return List.of();
        }
        return List.of(new ExampleRecord("replace-me", payload));
    }
}
