/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.download;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.plugin.openmeteo.config.OpenMeteoConfiguration;
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
class OpenMeteoDownloaderTest {

    @Test
    void buildsEncodedRequestWithCurrentUserAgent() {
        final var downloader = new OpenMeteoDownloader(configuration());
        final var request = downloader.buildRequest(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 2));

        assertEquals(
                "OpenData-OpenMeteo/1.0",
                request.headers().firstValue("User-Agent").orElseThrow());
        assertTrue(request.uri().toString().contains("start_date=2026-01-01"));
        assertTrue(request.uri().toString().contains("end_date=2026-01-02"));
        assertTrue(request.uri().toString().contains("timezone=Europe%2FLondon"));
    }

    private static OpenMeteoConfiguration configuration() {
        return new OpenMeteoConfiguration(
                URI.create("https://archive-api.open-meteo.com/v1/archive"),
                "home",
                "Home",
                51.5,
                -0.1,
                ZoneId.of("Europe/London"),
                Duration.ofSeconds(10),
                Duration.ofSeconds(30),
                Optional.empty(),
                Optional.empty(),
                365,
                false,
                "openmeteo",
                "Location",
                "DailyWeather",
                500,
                Duration.ofSeconds(30));
    }
}
