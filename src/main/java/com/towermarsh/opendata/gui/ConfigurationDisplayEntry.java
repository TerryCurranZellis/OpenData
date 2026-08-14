/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import java.util.Objects;

/**
 * One read-only property/value row displayed by a JavaFX information dialog.
 *
 * @param property display property name
 * @param value display value, already masked when sensitive
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public record ConfigurationDisplayEntry(String property, String value) {

    /** Validates and normalises the display values. */
    public ConfigurationDisplayEntry {
        property = Objects.requireNonNull(property, "property").trim();
        if (property.isEmpty()) {
            throw new IllegalArgumentException("property must not be blank.");
        }
        value = Objects.requireNonNullElse(value, "");
    }
}
