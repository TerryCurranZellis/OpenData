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
 */
public record PluginDescriptor(
        String id,
        String displayName,
        String description,
        String implementationClass,
        boolean enabled,
        int configurationVersion) {

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

    private static String requireText(
            final String value,
            final String fieldName) {

        Objects.requireNonNull(value, fieldName);
        final String result = value.trim();
        if (result.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank.");
        }
        return result;
    }
}
