/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.initialise;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.validation.PluginPropertyValues;
import com.towermarsh.opendata.validation.SqlIdentifiers;
import com.towermarsh.opendata.validation.ValidationRules;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Typed Open-Meteo API and persistence configuration.
 *
 * @param endpoint Open-Meteo archive endpoint
 * @param locationKey stable location key
 * @param locationName location display name
 * @param latitude location latitude
 * @param longitude location longitude
 * @param timezone location timezone, for example {@code Europe/London}
 * @param connectTimeout HTTP connection timeout
 * @param requestTimeout HTTP request timeout
 * @param startDate optional query start date
 * @param endDate optional query end date
 * @param defaultStartDaysAgo retained legacy relative-range setting
 * @param includeCurrentDate whether queries may include the current date
 * @param targetSchema target database schema
 * @param locationTable location table name
 * @param dailyTable daily observation table name
 * @param databaseBatchSize staging insert batch size
 * @param databaseLockTimeout SQL Server application-lock timeout
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
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
     * Name of the Open-Meteo archive endpoint in the plugin definition.
     *
     * @since 2.0.0
     */
    public static final String ENDPOINT_NAME = "archive";

    /**
     * Validates and normalises record components.
     *
     * @since 2.0.0
     */
    public OpenMeteoConfiguration {
        Objects.requireNonNull(endpoint, "endpoint");
        locationKey = ValidationRules.requireText(locationKey, "location-key", 100);
        locationName = ValidationRules.requireText(locationName, "location-name", 200);
        latitude = ValidationRules.requireRange(latitude, -90.0, 90.0, "latitude");
        longitude = ValidationRules.requireRange(longitude, -180.0, 180.0, "longitude");

        timezone = Objects.requireNonNull(timezone, "timezone");
        ValidationRules.requireText(timezone.getId(), "timezone", 100);
        connectTimeout = ValidationRules.requirePositive(
                connectTimeout,
                "connect-timeout-seconds");
        requestTimeout = ValidationRules.requirePositive(
                requestTimeout,
                "request-timeout-seconds");

        startDate = startDate == null ? Optional.empty() : startDate;
        endDate = endDate == null ? Optional.empty() : endDate;
        defaultStartDaysAgo = ValidationRules.requireNonNegative(
                defaultStartDaysAgo,
                "default-start-days-ago");

        targetSchema = SqlIdentifiers.requireSafe(
                targetSchema,
                "database.target-schema");
        locationTable = SqlIdentifiers.requireSafe(
                locationTable,
                "database.location-table");
        dailyTable = SqlIdentifiers.requireSafe(
                dailyTable,
                "database.daily-table");
        databaseBatchSize = ValidationRules.requireRange(
                databaseBatchSize,
                1,
                10_000,
                "database.batch-size");
        databaseLockTimeout = ValidationRules.requirePositive(
                databaseLockTimeout,
                "database.lock-timeout-seconds");

        if (databaseLockTimeout.compareTo(
                Duration.ofMillis(Integer.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException(
                    "database lock timeout exceeds the SQL Server integer limit");
        }
        if (startDate.isPresent() && endDate.isPresent()) {
            ValidationRules.requireDateOrder(
                    startDate.get(),
                    endDate.get(),
                    "Open-Meteo query range");
        }
    }

    /**
     * Builds typed Open-Meteo configuration from a resolved plugin definition.
     *
     * @param definition resolved plugin definition
     * @return typed Open-Meteo configuration
     * @since 2.0.0
     */
    public static OpenMeteoConfiguration from(final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        if (!"openmeteo".equalsIgnoreCase(definition.id())) {
            throw new IllegalArgumentException(
                    "Expected plugin id 'openmeteo' but received '"
                            + definition.id() + "'");
        }

        final PluginPropertyValues properties = new PluginPropertyValues(definition);
        final String locationName = properties.requiredText("location-name");

        return new OpenMeteoConfiguration(
                definition.requireEndpoint(ENDPOINT_NAME).uri(),
                properties.text("location-key", slug(locationName)),
                locationName,
                properties.requiredDouble("latitude"),
                properties.requiredDouble("longitude"),
                properties.parseRequired(
                        "timezone",
                        ZoneId::of,
                        "an IANA timezone id"),
                Duration.ofSeconds(properties.integer(
                        "connect-timeout-seconds",
                        30)),
                Duration.ofSeconds(properties.integer(
                        "request-timeout-seconds",
                        60)),
                properties.optionalDate("start-date"),
                properties.optionalDate("end-date"),
                properties.integer("default-start-days-ago", 365),
                properties.booleanValue("include-current-date", false),
                properties.text("database.target-schema", "openmeteo"),
                properties.text("database.location-table", "Location"),
                properties.text("database.daily-table", "DailyWeather"),
                properties.integer("database.batch-size", 500),
                Duration.ofSeconds(properties.integer(
                        "database.lock-timeout-seconds",
                        30)));
    }

    /**
     * Resolves the inclusive query date range.
     *
     * <p>The default start date is {@code 2000-01-01}. The default end date is
     * the supplied current date minus one day.
     *
     * @param today current date in the configured location timezone
     * @return inclusive query date range
     * @since 2.0.0
     */
    public DateRange resolveDateRange(final LocalDate today) {
        Objects.requireNonNull(today, "today");
        final LocalDate effectiveStart = startDate.orElse(LocalDate.of(2000, 1, 1));
        final LocalDate effectiveEnd = endDate.orElse(today.minusDays(1));
        return new DateRange(effectiveStart, effectiveEnd);
    }

    private static String slug(final String value) {
        final String result = ValidationRules.requireText(value, "location-name")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return result.isBlank() ? "location" : result;
    }

    /**
     * Inclusive Open-Meteo query date range.
     *
     * @param startDate inclusive start date
     * @param endDate inclusive end date
     *
     * @since 2.0.0
     */
    public record DateRange(LocalDate startDate, LocalDate endDate) {

        /**
         * Validates the inclusive date order.
         *
         * @since 2.0.0
         */
        public DateRange {
            ValidationRules.requireDateOrder(
                    startDate,
                    endDate,
                    "Open-Meteo query range");
        }
    }
}
