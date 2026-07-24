/*
 * Filename: OpenMeteoConfiguration.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo;

import com.towermarsh.opendata.config.model.PluginDefinition;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/** Typed Open-Meteo API and persistence configuration. */
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

    public static final String ENDPOINT_NAME = "archive";

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

    public DateRange resolveDateRange(final LocalDate today) {
        Objects.requireNonNull(today, "today");
        final LocalDate effectiveEnd = endDate.orElse(includeCurrentDate ? today : today.minusDays(1));
        final LocalDate effectiveStart = startDate.orElse(effectiveEnd.minusDays(defaultStartDaysAgo));
        return new DateRange(effectiveStart, effectiveEnd);
    }

    private static String required(final PluginDefinition definition, final String name) {
        return definition.requireProperty(name);
    }

    private static String value(final PluginDefinition definition, final String name, final String defaultValue) {
        return definition.findProperty(name)
                .map(property -> property.value().trim())
                .filter(text -> !text.isEmpty())
                .orElse(defaultValue);
    }

    private static int integer(final PluginDefinition definition, final String name, final int defaultValue) {
        final String text = value(definition, name, Integer.toString(defaultValue));
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("OpenMeteo property '" + name + "' must be an integer", exception);
        }
    }

    private static double decimal(final PluginDefinition definition, final String name) {
        try {
            return Double.parseDouble(required(definition, name));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("OpenMeteo property '" + name + "' must be a decimal", exception);
        }
    }

    private static boolean bool(final PluginDefinition definition, final String name, final boolean defaultValue) {
        return switch (value(definition, name, Boolean.toString(defaultValue)).toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> throw new IllegalArgumentException("OpenMeteo property '" + name + "' must be a boolean");
        };
    }

    private static Optional<LocalDate> optionalDate(final PluginDefinition definition, final String name) {
        return definition.findProperty(name)
                .map(property -> property.value().trim())
                .filter(text -> !text.isEmpty())
                .map(LocalDate::parse);
    }

    private static String slug(final String value) {
        final String result = value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return result.isBlank() ? "location" : result;
    }

    private static String requireText(final String value, final String name) {
        return requireText(value, name, Integer.MAX_VALUE);
    }

    private static String requireText(
            final String value,
            final String name,
            final int maximumLength) {
        Objects.requireNonNull(value, name);
        final String result = value.trim();
        if (result.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        if (result.length() > maximumLength) {
            throw new IllegalArgumentException(name + " must not exceed " + maximumLength + " characters");
        }
        return result;
    }

    static String sqlIdentifier(final String value, final String name) {
        final String result = requireText(value, name);
        if (!result.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException(name + " is not a safe SQL identifier: " + result);
        }
        return result;
    }

    public record DateRange(LocalDate startDate, LocalDate endDate) {
        public DateRange {
            Objects.requireNonNull(startDate, "startDate");
            Objects.requireNonNull(endDate, "endDate");
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("startDate must not be after endDate");
            }
        }
    }
}
