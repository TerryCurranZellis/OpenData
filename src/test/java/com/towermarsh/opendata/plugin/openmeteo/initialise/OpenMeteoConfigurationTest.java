/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.initialise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.config.model.PluginEndpointDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyType;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests the Open-Meteo migration to shared typed validation.
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
class OpenMeteoConfigurationTest {

    @Test
    void usesSharedTypedPropertyParsing() {
        final PluginDefinition definition = definition();
        property(definition, "location-name", " Home Station ");
        property(definition, "latitude", "51.674304");
        property(definition, "longitude", "-0.785602");
        property(definition, "timezone", "Europe/London");
        property(definition, "connect-timeout-seconds", "45");
        property(definition, "request-timeout-seconds", "90");
        property(definition, "start-date", "2026-01-01");
        property(definition, "end-date", "2026-07-31");
        property(definition, "default-start-days-ago", "10");
        property(definition, "include-current-date", "yes");
        property(definition, "database.target-schema", "weather");
        property(definition, "database.location-table", "WeatherLocation");
        property(definition, "database.daily-table", "WeatherDaily");
        property(definition, "database.batch-size", "250");
        property(definition, "database.lock-timeout-seconds", "20");

        final OpenMeteoConfiguration configuration
                = OpenMeteoConfiguration.from(definition);

        assertEquals("home-station", configuration.locationKey());
        assertEquals("Home Station", configuration.locationName());
        assertEquals(51.674304, configuration.latitude(), 0.000001);
        assertEquals(-0.785602, configuration.longitude(), 0.000001);
        assertEquals(ZoneId.of("Europe/London"), configuration.timezone());
        assertEquals(Duration.ofSeconds(45), configuration.connectTimeout());
        assertEquals(Duration.ofSeconds(90), configuration.requestTimeout());
        assertEquals(
                Optional.of(LocalDate.of(2026, 1, 1)),
                configuration.startDate());
        assertEquals(
                Optional.of(LocalDate.of(2026, 7, 31)),
                configuration.endDate());
        assertEquals(10, configuration.defaultStartDaysAgo());
        assertTrue(configuration.includeCurrentDate());
        assertEquals("weather", configuration.targetSchema());
        assertEquals("WeatherLocation", configuration.locationTable());
        assertEquals("WeatherDaily", configuration.dailyTable());
        assertEquals(250, configuration.databaseBatchSize());
        assertEquals(Duration.ofSeconds(20), configuration.databaseLockTimeout());
    }

    @Test
    void preservesExistingDefaults() {
        final PluginDefinition definition = definition();
        property(definition, "location-name", "Home");
        property(definition, "latitude", "51.674304");
        property(definition, "longitude", "-0.785602");
        property(definition, "timezone", "Europe/London");

        final OpenMeteoConfiguration configuration
                = OpenMeteoConfiguration.from(definition);

        assertEquals("home", configuration.locationKey());
        assertEquals(Duration.ofSeconds(30), configuration.connectTimeout());
        assertEquals(Duration.ofSeconds(60), configuration.requestTimeout());
        assertEquals(Optional.empty(), configuration.startDate());
        assertEquals(Optional.empty(), configuration.endDate());
        assertEquals(365, configuration.defaultStartDaysAgo());
        assertFalse(configuration.includeCurrentDate());
        assertEquals("openmeteo", configuration.targetSchema());
        assertEquals("Location", configuration.locationTable());
        assertEquals("DailyWeather", configuration.dailyTable());
        assertEquals(500, configuration.databaseBatchSize());
        assertEquals(Duration.ofSeconds(30), configuration.databaseLockTimeout());
    }

    @Test
    void resolvesDefaultDateRange() {
        final OpenMeteoConfiguration configuration = configuration(
                "openmeteo",
                "home",
                Optional.empty(),
                Optional.empty());

        final OpenMeteoConfiguration.DateRange range
                = configuration.resolveDateRange(LocalDate.of(2026, 8, 4));

        assertEquals(LocalDate.of(2000, 1, 1), range.startDate());
        assertEquals(LocalDate.of(2026, 8, 3), range.endDate());
    }

    @Test
    void rejectsInvalidTypedPropertyWithoutExposingItsValue() {
        final PluginDefinition definition = definition();
        property(definition, "location-name", "Home");
        property(definition, "latitude", "not-a-coordinate");
        property(definition, "longitude", "-0.785602");
        property(definition, "timezone", "Europe/London");

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> OpenMeteoConfiguration.from(definition));

        assertEquals(
                "Plugin 'openmeteo' property 'latitude' must be a decimal number.",
                exception.getMessage());
    }

    @Test
    void rejectsUnsafeSqlIdentifierThroughSharedValidation() {
        assertThrows(
                IllegalArgumentException.class,
                () -> configuration(
                        "openmeteo;drop schema core",
                        "home",
                        Optional.empty(),
                        Optional.empty()));
    }

    @Test
    @SuppressWarnings("deprecation")
    void retainedSqlIdentifierProcedureIsDeprecatedAndDelegates() throws Exception {
        assertTrue(OpenMeteoConfiguration.class
                .getDeclaredMethod("sqlIdentifier", String.class, String.class)
                .isAnnotationPresent(Deprecated.class));
        assertEquals(
                "DailyWeather",
                OpenMeteoConfiguration.sqlIdentifier("DailyWeather", "table"));
    }

    private static OpenMeteoConfiguration configuration(
            final String schema,
            final String locationKey,
            final Optional<LocalDate> startDate,
            final Optional<LocalDate> endDate) {
        return new OpenMeteoConfiguration(
                URI.create("https://archive-api.open-meteo.com/v1/archive"),
                locationKey,
                "Home",
                51.674304,
                -0.785602,
                ZoneId.of("Europe/London"),
                Duration.ofSeconds(30),
                Duration.ofSeconds(60),
                startDate,
                endDate,
                365,
                false,
                schema,
                "Location",
                "DailyWeather",
                500,
                Duration.ofSeconds(30));
    }

    private static PluginDefinition definition() {
        final PluginDefinition definition = mock(PluginDefinition.class);
        final PluginEndpointDefinition endpoint = mock(PluginEndpointDefinition.class);
        when(definition.id()).thenReturn("openmeteo");
        when(definition.requireEndpoint(OpenMeteoConfiguration.ENDPOINT_NAME))
                .thenReturn(endpoint);
        when(endpoint.uri()).thenReturn(
                URI.create("https://archive-api.open-meteo.com/v1/archive"));
        when(definition.findProperty(anyString())).thenReturn(Optional.empty());
        return definition;
    }

    private static void property(
            final PluginDefinition definition,
            final String name,
            final String value) {
        when(definition.findProperty(name)).thenReturn(Optional.of(
                new PluginPropertyDefinition(
                        name,
                        value,
                        PluginPropertyType.STRING,
                        false,
                        "test property")));
    }
}
