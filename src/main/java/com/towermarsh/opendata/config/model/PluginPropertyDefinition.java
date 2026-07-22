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
 */
public record PluginPropertyDefinition(
        String name,
        String value,
        PluginPropertyType type,
        boolean sensitive,
        String description) {

    public PluginPropertyDefinition {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        description = description == null ? "" : description;
    }
}
