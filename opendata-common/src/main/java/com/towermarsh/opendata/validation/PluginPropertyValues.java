/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.validation;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyDefinition;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Provides consistent typed access to resolved plugin property values.
 *
 * <p>Error messages identify the plugin and property but deliberately omit the
 * property value so that sensitive configuration is not accidentally logged.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class PluginPropertyValues {

    private final PluginDefinition definition;

    /**
     * Creates a typed property reader.
     *
     * @param definition resolved plugin definition
     */
    public PluginPropertyValues(final PluginDefinition definition) {
        this.definition = Objects.requireNonNull(definition, "definition");
    }

    /**
     * Returns required non-blank text.
     *
     * @param name property name
     * @return trimmed text
     */
    public String requiredText(final String name) {
        return raw(name).orElseThrow(() -> missing(name));
    }

    /**
     * Returns non-blank text or a default value.
     *
     * @param name property name
     * @param defaultValue fallback value
     * @return trimmed text
     */
    public String text(final String name, final String defaultValue) {
        return raw(name).orElseGet(
                () -> ValidationRules.requireText(defaultValue, name + " default"));
    }

    /**
     * Returns an integer value or a default.
     *
     * @param name property name
     * @param defaultValue fallback value
     * @return parsed integer
     */
    public int integer(final String name, final int defaultValue) {
        return parse(name, Integer.toString(defaultValue), Integer::parseInt, "an integer");
    }

    /**
     * Returns a long value or a default.
     *
     * @param name property name
     * @param defaultValue fallback value
     * @return parsed long
     */
    public long longValue(final String name, final long defaultValue) {
        return parse(name, Long.toString(defaultValue), Long::parseLong, "a long integer");
    }

    /**
     * Returns a double value or a default.
     *
     * @param name property name
     * @param defaultValue fallback value
     * @return parsed double
     */
    public double doubleValue(final String name, final double defaultValue) {
        return parse(name, Double.toString(defaultValue), Double::parseDouble, "a decimal number");
    }

    /**
     * Returns a required double value.
     *
     * @param name property name
     * @return parsed double
     */
    public double requiredDouble(final String name) {
        return parseRequired(name, Double::parseDouble, "a decimal number");
    }

    /**
     * Returns a decimal value or a default.
     *
     * @param name property name
     * @param defaultValue fallback value
     * @return parsed decimal
     */
    public BigDecimal decimal(final String name, final BigDecimal defaultValue) {
        Objects.requireNonNull(defaultValue, "defaultValue");
        return parse(name, defaultValue.toPlainString(), BigDecimal::new, "a decimal number");
    }

    /**
     * Returns a boolean value or a default.
     *
     * @param name property name
     * @param defaultValue fallback value
     * @return parsed boolean
     */
    public boolean booleanValue(final String name, final boolean defaultValue) {
        return parse(
                name,
                Boolean.toString(defaultValue),
                PluginPropertyValues::parseBoolean,
                "a boolean");
    }

    /**
     * Returns an ISO-8601 duration or a default.
     *
     * @param name property name
     * @param defaultValue fallback value
     * @return parsed duration
     */
    public Duration duration(final String name, final Duration defaultValue) {
        Objects.requireNonNull(defaultValue, "defaultValue");
        return parse(name, defaultValue.toString(), Duration::parse, "an ISO-8601 duration");
    }

    /**
     * Returns a required ISO local date.
     *
     * @param name property name
     * @return parsed date
     */
    public LocalDate requiredDate(final String name) {
        return parseRequired(name, LocalDate::parse, "an ISO date in yyyy-MM-dd form");
    }

    /**
     * Returns an optional ISO local date.
     *
     * @param name property name
     * @return optional parsed date
     */
    public Optional<LocalDate> optionalDate(final String name) {
        return raw(name).map(value -> parseValue(
                name,
                value,
                LocalDate::parse,
                "an ISO date in yyyy-MM-dd form"));
    }

    /**
     * Returns a required path.
     *
     * @param name property name
     * @return parsed path
     */
    public Path requiredPath(final String name) {
        return parseRequired(name, Path::of, "a file-system path");
    }

    /**
     * Returns a URI or a default.
     *
     * @param name property name
     * @param defaultValue fallback value
     * @return parsed URI
     */
    public URI uri(final String name, final URI defaultValue) {
        Objects.requireNonNull(defaultValue, "defaultValue");
        return parse(name, defaultValue.toString(), URI::create, "a URI");
    }

    /**
     * Parses one required property with a caller-supplied parser.
     *
     * @param name property name
     * @param parser parser implementation
     * @param expectedDescription expected format description
     * @param <T> parsed type
     * @return parsed value
     */
    public <T> T parseRequired(
            final String name,
            final ValueParser<T> parser,
            final String expectedDescription) {
        return parseValue(name, requiredText(name), parser, expectedDescription);
    }

    /**
     * Parses one optional property, applying a textual default when absent.
     *
     * @param name property name
     * @param defaultValue textual fallback value
     * @param parser parser implementation
     * @param expectedDescription expected format description
     * @param <T> parsed type
     * @return parsed value
     */
    public <T> T parse(
            final String name,
            final String defaultValue,
            final ValueParser<T> parser,
            final String expectedDescription) {
        final String value = raw(name).orElseGet(
                () -> ValidationRules.requireText(defaultValue, name + " default"));
        return parseValue(name, value, parser, expectedDescription);
    }

    private Optional<String> raw(final String name) {
        ValidationRules.requireText(name, "property name");
        return definition.findProperty(name)
                .map(PluginPropertyDefinition::value)
                .map(String::trim)
                .filter(value -> !value.isEmpty());
    }

    private <T> T parseValue(
            final String name,
            final String value,
            final ValueParser<T> parser,
            final String expectedDescription) {
        Objects.requireNonNull(parser, "parser");
        ValidationRules.requireText(expectedDescription, "expectedDescription");
        try {
            return parser.parse(value);
        } catch (RuntimeException exception) {
            throw invalid(name, expectedDescription, exception);
        } catch (Exception exception) {
            throw invalid(name, expectedDescription, exception);
        }
    }

    private IllegalArgumentException missing(final String name) {
        return new IllegalArgumentException(
                "Plugin '" + definition.id() + "' requires property '" + name + "'.");
    }

    private IllegalArgumentException invalid(
            final String name,
            final String expectedDescription,
            final Exception cause) {
        return new IllegalArgumentException(
                "Plugin '" + definition.id() + "' property '" + name
                        + "' must be " + expectedDescription + '.',
                cause);
    }

    private static boolean parseBoolean(final String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> throw new IllegalArgumentException("Unsupported boolean value");
        };
    }
}
