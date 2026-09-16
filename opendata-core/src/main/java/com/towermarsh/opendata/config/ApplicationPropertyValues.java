/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import com.towermarsh.opendata.validation.ValidationRules;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Typed, case-insensitive access to application configuration properties.
 *
 * @author Terry Curran
 * @version 2.1
 */
public final class ApplicationPropertyValues {

    private final Map<String, String> values;

    /**
     * Creates a property reader from raw application values.
     *
     * @param values raw properties
     */
    public ApplicationPropertyValues(final Map<String, String> values) {
        Objects.requireNonNull(values, "values");
        final Map<String, String> normalised = new LinkedHashMap<>();
        values.forEach((key, value) -> normalised.put(normalise(key), value));
        this.values = Map.copyOf(normalised);
    }

    /** Returns a required, trimmed text value. */
    public String requiredText(final String name) {
        final var value = values.get(normalise(name));
        try {
            return ValidationRules.requireText(value, name);
        } catch (IllegalArgumentException exception) {
            throw new OpenDataConfigurationException(
                    "Required application property is missing: " + name,
                    exception);
        }
    }

    /** Returns trimmed text or the supplied default when absent or blank. */
    public String text(final String name, final String defaultValue) {
        final var value = values.get(normalise(name));
        if (value == null || value.isBlank()) {
            return Objects.requireNonNull(defaultValue, "defaultValue").trim();
        }
        return value.trim();
    }

    /** Returns an integer or the supplied default when absent or blank. */
    public int integer(final String name, final int defaultValue) {
        final var value = values.get(normalise(name));
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new OpenDataConfigurationException("Application property must be an integer: " + name, exception);
        }
    }

    /** Returns a Boolean or the supplied default when absent or blank. */
    public boolean bool(final String name, final boolean defaultValue) {
        final var value = values.get(normalise(name));
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> throw new OpenDataConfigurationException(
                    "Application property must be a boolean: " + name);
        };
    }

    /** Returns an ISO-8601 duration or the supplied default when absent. */
    public Duration duration(final String name, final Duration defaultValue) {
        final var value = values.get(normalise(name));
        if (value == null || value.isBlank()) {
            return Objects.requireNonNull(defaultValue, "defaultValue");
        }
        try {
            return Duration.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new OpenDataConfigurationException("Application property must be an ISO-8601 duration: " + name, exception);
        }
    }

    /** Returns whether a non-blank property is present. */
    public boolean contains(final String name) {
        final var value = values.get(normalise(name));
        return value != null && !value.isBlank();
    }

    private static String normalise(final String value) {
        return Objects.requireNonNull(value, "property name").trim().toLowerCase(Locale.ROOT);
    }
}
