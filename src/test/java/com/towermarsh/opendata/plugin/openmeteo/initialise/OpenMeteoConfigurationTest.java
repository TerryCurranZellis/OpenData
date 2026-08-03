/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.initialise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 1.0.0
 */
class OpenMeteoConfigurationTest {
    @Test
    void resolvesRelativeRangeAndExcludesCurrentDateByDefault() {
        final var configuration = configuration("openmeteo", "home", 6, false);

        final var range = configuration.resolveDateRange(LocalDate.of(2026, 7, 24));

        assertEquals(LocalDate.of(2000, 1, 1), range.startDate());
        assertEquals(LocalDate.of(2026, 7, 23), range.endDate());
    }

    @Test
    void rejectsUnsafeSqlIdentifier() {
        assertThrows(IllegalArgumentException.class,
                () -> configuration("openmeteo;drop schema core", "home", 6, false));
    }

    @Test
    void rejectsLocationKeyLongerThanDatabaseColumn() {
        assertThrows(IllegalArgumentException.class,
                () -> configuration("openmeteo", "x".repeat(101), 6, false));
    }

    private static OpenMeteoConfiguration configuration(
            final String schema,
            final String locationKey,
            final int daysAgo,
            final boolean includeToday) {
        return new OpenMeteoConfiguration(
                URI.create("https://archive-api.open-meteo.com/v1/archive"),
                locationKey,
                "Home",
                51.674304,
                -0.785602,
                ZoneId.of("Europe/London"),
                Duration.ofSeconds(30),
                Duration.ofSeconds(60),
                Optional.empty(),
                Optional.empty(),
                daysAgo,
                includeToday,
                schema,
                "Location",
                "DailyWeather",
                500,
                Duration.ofSeconds(30));
    }
}
