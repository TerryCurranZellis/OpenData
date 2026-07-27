/*
 * Filename: OpenMeteoConfiguration.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.config;

import com.towermarsh.opendata.config.model.PluginDefinition;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Typed Open-Meteo API and persistence configuration. *
 *
 * @param endpoint endpoint name
 * @param locationKey location key id
 * @param locationName location name
 * @param latitude location latitude
 * @param longitude location longitude
 * @param timezone location timezone e.g. Europe/London
 * @param connectTimeout how long to try to connect
 * @param requestTimeout how long to wait for a result to be returned
 * @param startDate start date for query default 2000-01-01
 * @param endDate end date for query default today - 1 day
 * @param defaultStartDaysAgo get data for this many days - ignored now
 * @param includeCurrentDate data includes today
 * @param targetSchema where the database table is
 * @param locationTable name of the table
 * @param dailyTable data is recorded by day
 * @param databaseBatchSize how many records to load in a batch
 * @param databaseLockTimeout how long to keep database connection open
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record OpenMeteoConfiguration(
        URI endpoint,
        String locationKey,
        String locationName,
        double latitude,
        double longitude,
        ZoneId timezone,
        Duration connectTimeout,
        Duration requestTimeout,
        Optional<LocalDate> startDate,
        Optional<LocalDate> endDate,
        int defaultStartDaysAgo,
        boolean includeCurrentDate,
        String targetSchema,
        String locationTable,
        String dailyTable,
        int databaseBatchSize,
        Duration databaseLockTimeout) {

    /**
     * default end point
     */
    public static final String ENDPOINT_NAME = "archive";

    /**
     * Validates and normalises record components.
     */
    public OpenMeteoConfiguration {
        Objects.requireNonNull(endpoint, "endpoint");
        locationKey = requireText(locationKey, "location-key", 100);
        locationName = requireText(locationName, "location-name", 200);
        Objects.requireNonNull(timezone, "timezone");
        requireText(timezone.getId(), "timezone", 100);
        Objects.requireNonNull(connectTimeout, "connectTimeout");
        Objects.requireNonNull(requestTimeout, "requestTimeout");
        startDate = startDate == null ? Optional.empty() : startDate;
        endDate = endDate == null ? Optional.empty() : endDate;
        targetSchema = sqlIdentifier(targetSchema, "database.target-schema");
        locationTable = sqlIdentifier(locationTable, "database.location-table");
        dailyTable = sqlIdentifier(dailyTable, "database.daily-table");
        Objects.requireNonNull(databaseLockTimeout, "databaseLockTimeout");
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("latitude must be between -90 and 90");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("longitude must be between -180 and 180");
        }
        if (connectTimeout.isZero() || connectTimeout.isNegative()
                || requestTimeout.isZero() || requestTimeout.isNegative()) {
            throw new IllegalArgumentException("HTTP timeouts must be positive");
        }
        if (databaseLockTimeout.isZero() || databaseLockTimeout.isNegative()) {
            throw new IllegalArgumentException("database lock timeout must be positive");
        }
        if (databaseLockTimeout.toMillis() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("database lock timeout exceeds the SQL Server integer limit");
        }
        if (defaultStartDaysAgo < 0) {
            throw new IllegalArgumentException("default-start-days-ago must not be negative");
        }
        if (databaseBatchSize < 1 || databaseBatchSize > 10_000) {
            throw new IllegalArgumentException("database.batch-size must be between 1 and 10000");
        }
        if (startDate.isPresent() && endDate.isPresent() && startDate.get().isAfter(endDate.get())) {
            throw new IllegalArgumentException("start-date must not be after end-date");
        }
    }

    /**
     * Get the configuration settings
     *
     * @param definition definition
     * @return the settings
     */
    public static OpenMeteoConfiguration from(final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        if (!"openmeteo".equalsIgnoreCase(definition.id())) {
            throw new IllegalArgumentException("Expected plugin id 'openmeteo' but received '" + definition.id() + "'");
        }
        return new OpenMeteoConfiguration(
                definition.requireEndpoint(ENDPOINT_NAME).uri(),
                value(definition, "location-key", slug(value(definition, "location-name", "location"))),
                required(definition, "location-name"),
                decimal(definition, "latitude"),
                decimal(definition, "longitude"),
                ZoneId.of(required(definition, "timezone")),
                Duration.ofSeconds(integer(definition, "connect-timeout-seconds", 30)),
                Duration.ofSeconds(integer(definition, "request-timeout-seconds", 60)),
                optionalDate(definition, "start-date"),
                optionalDate(definition, "end-date"),
                integer(definition, "default-start-days-ago", 365),
                bool(definition, "include-current-date", false),
                value(definition, "database.target-schema", "openmeteo"),
                value(definition, "database.location-table", "Location"),
                value(definition, "database.daily-table", "DailyWeather"),
                integer(definition, "database.batch-size", 500),
                Duration.ofSeconds(integer(definition, "database.lock-timeout-seconds", 30)));
    }

    /**
     * Resolve the date range
     * <p>
     * Default values
     * <table>
     * <caption>Default date values</caption>
     * <tr>
     * <th>Property</th>
     * <th>Default value</th>
     * </tr>
     * <tr>
     * <td>StartDate</td>
     * <td>2000-01-01</td>
     * </tr>
     * <tr>
     * <td>EndDate</td>
     * <td>Current date minus one day</td>
     * </tr>
     * </table>
     *
     * @param today today's date
     * @return the date range required
     */
    public DateRange resolveDateRange(final LocalDate today) {
        Objects.requireNonNull(today, "today");
        final LocalDate effectiveStart = startDate.orElse(LocalDate.of(2000, 1, 1));
        final LocalDate effectiveEnd = endDate.orElse(today.minusDays(1));
        return new DateRange(effectiveStart, effectiveEnd);
    }

    /**
     * Returns a required plugin property value.
     *
     * @param definition plugin definition
     * @param name property name
     * @return property value
     */
    private static String required(final PluginDefinition definition, final String name) {
        return definition.requireProperty(name);
    }

    /**
     * Returns a plugin property value or a default.
     *
     * @param definition plugin definition
     * @param name property name
     * @param defaultValue fallback value
     * @return resolved property value
     */
    private static String value(final PluginDefinition definition, final String name, final String defaultValue) {
        return definition.findProperty(name)
                .map(property -> property.value().trim())
                .filter(text -> !text.isEmpty())
                .orElse(defaultValue);
    }

    /**
     * Returns an integer plugin property or a default.
     *
     * @param definition plugin definition
     * @param name property name
     * @param defaultValue fallback value
     * @return parsed integer value
     */
    private static int integer(final PluginDefinition definition, final String name, final int defaultValue) {
        final String text = value(definition, name, Integer.toString(defaultValue));
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("OpenMeteo property '" + name + "' must be an integer", exception);
        }
    }

    /**
     * Returns a required decimal plugin property.
     *
     * @param definition plugin definition
     * @param name property name
     * @return parsed decimal value
     */
    private static double decimal(final PluginDefinition definition, final String name) {
        try {
            return Double.parseDouble(required(definition, name));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("OpenMeteo property '" + name + "' must be a decimal", exception);
        }
    }

    /**
     * Returns a boolean plugin property or a default.
     *
     * @param definition plugin definition
     * @param name property name
     * @param defaultValue fallback value
     * @return parsed boolean value
     */
    private static boolean bool(final PluginDefinition definition, final String name, final boolean defaultValue) {
        return switch (value(definition, name, Boolean.toString(defaultValue)).toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" ->
                true;
            case "false", "no", "0", "off" ->
                false;
            default ->
                throw new IllegalArgumentException("OpenMeteo property '" + name + "' must be a boolean");
        };
    }

    /**
     * Returns an optional date property.
     *
     * @param definition plugin definition
     * @param name property name
     * @return optional parsed date
     */
    private static Optional<LocalDate> optionalDate(final PluginDefinition definition, final String name) {
        return definition.findProperty(name)
                .map(property -> property.value().trim())
                .filter(text -> !text.isEmpty())
                .map(LocalDate::parse);
    }

    /**
     * Converts free text into a stable slug.
     *
     * @param value text to normalise
     * @return slug value
     */
    private static String slug(final String value) {
        final var result = value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return result.isBlank() ? "location" : result;
    }

    /**
     * Returns a required non-blank text value.
     *
     * @param value value to validate
     * @param name field name for error reporting
     * @return trimmed text value
     */
    private static String requireText(final String value, final String name) {
        return requireText(value, name, Integer.MAX_VALUE);
    }

    private static String requireText(
            final String value,
            final String name,
            final int maximumLength) {
        Objects.requireNonNull(value, name);
        final var result = value.trim();
        if (result.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        if (result.length() > maximumLength) {
            throw new IllegalArgumentException(name + " must not exceed " + maximumLength + " characters");
        }
        return result;
    }

    /**
     * Validates one SQL identifier used by the plugin-local load package.
     *
     * @param value identifier value to validate
     * @param name property name for error reporting
     * @return validated SQL identifier
     */
    public static String sqlIdentifier(final String value, final String name) {
        final var result = requireText(value, name);
        if (!result.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException(name + " is not a safe SQL identifier: " + result);
        }
        return result;
    }

    /**
     *
     * Inclusive Open-Meteo query date range.
     *
     * @param startDate inclusive start date
     * @param endDate inclusive end date
     */
    public record DateRange(LocalDate startDate, LocalDate endDate) {

        /**
         * Validates and normalises record components.
         */
        public DateRange {
            Objects.requireNonNull(startDate, "startDate");
            Objects.requireNonNull(endDate, "endDate");
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("startDate must not be after endDate");
            }
        }
    }
}
