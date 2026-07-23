/*
 *  Filename: OpenMeteoConfiguration.java
 * 
 *  (C) Copyright Terry Curran 2026. All rights reserved
 * 
 *  This software is provided 'as-is', without any express or implied
 *  warranty.  In no event will the author be held liable for any damages
 *  arising from the use of this software.
 * 
 *  Permission is granted to anyone to use this software for any purpose,
 *  including commercial applications, and to alter it and redistribute it
 *  freely, subject to the following restrictions:
 * 
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgement in the product documentation would be
 *     appreciated but is not required.
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *  3. This notice may not be removed or altered from any source distribution.
 * 
 *  The author may be contacted by email to the following address:
 * 
 *  terry.curran@towermarsh.co.uk
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

/**
 * Typed configuration used by the OpenMeteo plugin.
 *
 * @param endpoint archive API endpoint
 * @param locationName display name stored with each record
 * @param latitude location latitude
 * @param longitude location longitude
 * @param timezone timezone requested from Open-Meteo
 * @param connectTimeout HTTP connection timeout
 * @param requestTimeout complete request timeout
 * @param startDate inclusive first date
 * @param endDate inclusive final date
 * @param defaultStartDaysAgo number of days used when start-date is omitted
 * @param includeCurrentDate whether today's date may be requested
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public record OpenMeteoConfiguration(
        URI endpoint,
        String locationName,
        double latitude,
        double longitude,
        ZoneId timezone,
        Duration connectTimeout,
        Duration requestTimeout,
        Optional<LocalDate> startDate,
        Optional<LocalDate> endDate,
        int defaultStartDaysAgo,
        boolean includeCurrentDate) {

    public static final String ENDPOINT_NAME = "archive";

    public OpenMeteoConfiguration {
        Objects.requireNonNull(endpoint, "endpoint");
        Objects.requireNonNull(locationName, "locationName");
        Objects.requireNonNull(timezone, "timezone");
        Objects.requireNonNull(connectTimeout, "connectTimeout");
        Objects.requireNonNull(requestTimeout, "requestTimeout");
        startDate = startDate == null ? Optional.empty() : startDate;
        endDate = endDate == null ? Optional.empty() : endDate;

        if (locationName.isBlank()) {
            throw new IllegalArgumentException("location-name must not be blank");
        }
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("latitude must be between -90 and 90");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("longitude must be between -180 and 180");
        }
        if (connectTimeout.isZero() || connectTimeout.isNegative()) {
            throw new IllegalArgumentException("connect-timeout must be positive");
        }
        if (requestTimeout.isZero() || requestTimeout.isNegative()) {
            throw new IllegalArgumentException("request-timeout must be positive");
        }
        if (defaultStartDaysAgo < 0) {
            throw new IllegalArgumentException("default-start-days-ago must not be negative");
        }
        if (startDate.isPresent() && endDate.isPresent()
                && startDate.get().isAfter(endDate.get())) {
            throw new IllegalArgumentException("start-date must not be after end-date");
        }
    }

    /**
     * Builds typed configuration from the framework plugin definition.
     *
     * @param definition selected OpenMeteo plugin definition
     * @return typed configuration
     */
    public static OpenMeteoConfiguration from(final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");

        if (!"openmeteo".equalsIgnoreCase(definition.id())) {
            throw new IllegalArgumentException(
                    "Expected plugin id 'openmeteo' but received '" + definition.id() + "'");
        }

        var endpoint = definition.requireEndpoint(ENDPOINT_NAME).uri();

        return new OpenMeteoConfiguration(
                endpoint,
                required(definition, "location-name"),
                doubleProperty(definition, "latitude"),
                doubleProperty(definition, "longitude"),
                ZoneId.of(required(definition, "timezone")),
                Duration.ofSeconds(integerProperty(definition, "connect-timeout-seconds")),
                Duration.ofSeconds(integerProperty(definition, "request-timeout-seconds")),
                optionalDate(definition, "start-date"),
                optionalDate(definition, "end-date"),
                integerProperty(definition, "default-start-days-ago"),
                booleanProperty(definition, "include-current-date"));
    }

    /**
     * Resolves the effective inclusive date range for one invocation.
     *
     * @param today current date in the configured timezone
     * @return resolved range
     */
    public DateRange resolveDateRange(final LocalDate today) {
        Objects.requireNonNull(today, "today");

        var effectiveEnd = endDate.orElse(
                includeCurrentDate ? today : today.minusDays(1));
        var effectiveStart = startDate.orElse(
                effectiveEnd.minusDays(defaultStartDaysAgo));

        if (effectiveStart.isAfter(effectiveEnd)) {
            throw new IllegalArgumentException(
                    "Resolved OpenMeteo start date is after end date");
        }
        return new DateRange(effectiveStart, effectiveEnd);
    }

    private static String required(
            final PluginDefinition definition,
            final String name) {
        return definition.requireProperty(name);
    }

    private static int integerProperty(
            final PluginDefinition definition,
            final String name) {
        try {
            return Integer.parseInt(required(definition, name));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "OpenMeteo property '%s' must be an integer"
                            .formatted(name),
                    exception);
        }
    }

    private static double doubleProperty(
            final PluginDefinition definition,
            final String name) {
        try {
            return Double.parseDouble(required(definition, name));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "OpenMeteo property '%s' must be a decimal number"
                            .formatted(name),
                    exception);
        }
    }

    private static boolean booleanProperty(
            final PluginDefinition definition,
            final String name) {
        return switch (required(definition, name)
                .trim()
                .toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" ->
                true;
            case "false", "no", "0", "off" ->
                false;
            default ->
                throw new IllegalArgumentException(
                        "OpenMeteo property '%s' must be a boolean"
                                .formatted(name));
        };
    }

    private static Optional<LocalDate> optionalDate(
            final PluginDefinition definition,
            final String name) {
        return definition.findProperty(name)
                .map(property -> property.value().trim())
                .filter(value -> !value.isEmpty())
                .map(LocalDate::parse);
    }

    /**
     * Inclusive date range.
     *
     * @param startDate first date
     * @param endDate final date
     */
    public record DateRange(LocalDate startDate, LocalDate endDate) {

        public DateRange {
            Objects.requireNonNull(startDate, "startDate");
            Objects.requireNonNull(endDate, "endDate");
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException(
                        "startDate must not be after endDate");
            }
        }
    }
}
