/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import java.util.Objects;

/**
 * Immutable metadata describing an installed properties-based plugin.
 *
 * @param id stable command-line plugin identifier
 * @param displayName human-readable plugin name
 * @param description plugin description
 * @param implementationClass configured implementation class
 * @param enabled whether the plugin may be executed
 * @param configurationVersion plugin configuration version
 *
 * @author Terry Curran
 * @since 1.0.0
 * @version 4.1.0
 */
public record PluginDescriptor(
        String id,
        String displayName,
        String description,
        String implementationClass,
        boolean enabled,
        int configurationVersion) {

    /**
     * Validates required plugin metadata and normalises text fields.
     *
     * @param id stable plugin identifier
     * @param displayName name shown to the user
     * @param description plugin description
     * @param implementationClass executable plugin class
     * @param enabled whether normal execution is permitted
     * @param configurationVersion plugin configuration version
     */
    public PluginDescriptor {
        id = requireText(id, "id");
        displayName = requireText(displayName, "displayName");
        description = description == null ? "" : description.trim();
        implementationClass = requireText(
                implementationClass,
                "implementationClass");

        if (configurationVersion < 1) {
            throw new IllegalArgumentException(
                    "configurationVersion must be at least 1.");
        }
    }

    /**
     * Returns a required non-blank text value.
     *
     * @param value value to validate
     * @param fieldName field name for error reporting
     * @return trimmed text value
     */
    private static String requireText(
            String value,
            String fieldName) {

        Objects.requireNonNull(value, fieldName);
        var result = value.trim();
        if (result.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank.");
        }
        return result;
    }
}
