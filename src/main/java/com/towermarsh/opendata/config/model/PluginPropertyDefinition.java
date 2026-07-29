/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config.model;

import java.util.Objects;

/**
 * Typed plugin-specific configuration property.
 *
 * @param name property name
 * @param value textual property value
 * @param type declared value type
 * @param sensitive whether the value must be omitted from logs
 * @param description optional description
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record PluginPropertyDefinition(
        String name,
        String value,
        PluginPropertyType type,
        boolean sensitive,
        String description) {

    /** Validates and normalises record components. */

    public PluginPropertyDefinition {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        description = description == null ? "" : description;
    }
}
