/*
 *  Filename: OpenMeteoApiClient.java
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Downloads and parses historical daily weather data from Open-Meteo.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class OpenMeteoApiClient {

    private static final Logger LOGGER
            = Logger.getLogger(OpenMeteoApiClient.class.getName());

    private static final String DAILY_VARIABLES = String.join(",",
            "temperature_2m_max",
            "temperature_2m_min",
            "temperature_2m_mean",
            "sunrise",
            "sunset",
            "daylight_duration",
            "weather_code");

    private final OpenMeteoConfiguration configuration;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenMeteoApiClient(
            final OpenMeteoConfiguration configuration) {
        this(
                configuration,
                HttpClient.newBuilder()
                        .connectTimeout(configuration.connectTimeout())
                        .build(),
                new ObjectMapper());
    }

    OpenMeteoApiClient(
            final OpenMeteoConfiguration configuration,
            final HttpClient httpClient,
            final ObjectMapper objectMapper) {
        this.configuration
                = Objects.requireNonNull(configuration, "configuration");
        this.httpClient
                = Objects.requireNonNull(httpClient, "httpClient");
        this.objectMapper
                = Objects.requireNonNull(objectMapper, "objectMapper");
    }

    /**
     * Downloads the configured historical date range.
     *
     * @return immutable list of daily records
     * @throws com.towermarsh.opendata.plugin.openmeteo.OpenMeteoException
     */
    public List<DailyWeatherRecord> download()
            throws OpenMeteoException {
        var today = LocalDate.now(configuration.timezone());
        var range = configuration.resolveDateRange(today);
        return download(range.startDate(), range.endDate());
    }

    /**
     * Downloads an inclusive historical date range.
     *
     * @param startDate first date
     * @param endDate final date
     * @return immutable list of daily records
     * @throws com.towermarsh.opendata.plugin.openmeteo.OpenMeteoException
     */
    public List<DailyWeatherRecord> download(
            final LocalDate startDate,
            final LocalDate endDate)
            throws OpenMeteoException {

        Objects.requireNonNull(startDate, "startDate");
        Objects.requireNonNull(endDate, "endDate");

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "startDate must not be after endDate");
        }

        var uri = buildUri(startDate, endDate);
        var request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .timeout(configuration.requestTimeout())
                .header("Accept", "application/json")
                .header("User-Agent", "OpenData-OpenMeto/1.0")
                .build();

        LOGGER.info(() -> "Downloading Open-Meteo history for %s from %s to %s"
                .formatted(
                        configuration.locationName(),
                        startDate,
                        endDate));

        final HttpResponse<String> response;
        try {
            response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(
                            StandardCharsets.UTF_8));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new OpenMeteoException(
                    "Open-Meteo request was interrupted",
                    exception);
        } catch (IOException exception) {
            throw new OpenMeteoException(
                    "Unable to call the Open-Meteo archive API",
                    exception);
        }

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {
            throw new OpenMeteoException(
                    "Open-Meteo returned HTTP %d: %s"
                            .formatted(
                                    response.statusCode(),
                                    abbreviated(response.body())));
        }

        try {
            return parseResponse(response.body());
        } catch (JsonProcessingException exception) {
            LOGGER.log(
                    Level.WARNING,
                    "Open-Meteo returned invalid JSON",
                    exception);
            throw new OpenMeteoException(
                    "Unable to parse the Open-Meteo response",
                    exception);
        }
    }

    URI buildUri(
            final LocalDate startDate,
            final LocalDate endDate) {
        var separator = configuration.endpoint()
                .toString()
                .contains("?") ? "&" : "?";

        var query = "latitude=" + configuration.latitude()
                + "&longitude=" + configuration.longitude()
                + "&start_date=" + startDate
                + "&end_date=" + endDate
                + "&daily=" + encode(DAILY_VARIABLES)
                + "&timezone=" + encode(
                        configuration.timezone().getId());

        return URI.create(
                configuration.endpoint() + separator + query);
    }

    List<DailyWeatherRecord> parseResponse(
            final String json)
            throws JsonProcessingException {

        var response = objectMapper.readValue(
                json,
                OpenMeteoResponse.class);

        if (response.daily() == null) {
            throw new JsonProcessingException(
                    "Open-Meteo response did not contain daily data") {
            };
        }

        validateLengths(response.daily());

        var results = new ArrayList<DailyWeatherRecord>();
        for (var index = 0;
                index < response.daily().time().size();
                index++) {

            var weatherCode
                    = response.daily().weatherCodes().get(index);
            var daylightMinutes = Math.round(
                    response.daily()
                            .daylightDurationSeconds()
                            .get(index) / 60.0);

            results.add(new DailyWeatherRecord(
                    LocalDate.parse(
                            response.daily().time().get(index),
                            DateTimeFormatter.ISO_LOCAL_DATE),
                    configuration.locationName(),
                    configuration.latitude(),
                    configuration.longitude(),
                    requiredNumber(
                            response.daily()
                                    .minimumTemperatures()
                                    .get(index),
                            "temperature_2m_min",
                            index),
                    requiredNumber(
                            response.daily()
                                    .maximumTemperatures()
                                    .get(index),
                            "temperature_2m_max",
                            index),
                    requiredNumber(
                            response.daily()
                                    .meanTemperatures()
                                    .get(index),
                            "temperature_2m_mean",
                            index),
                    LocalDateTime.parse(
                            response.daily()
                                    .sunrise()
                                    .get(index),
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            .toLocalTime(),
                    LocalDateTime.parse(
                            response.daily()
                                    .sunset()
                                    .get(index),
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            .toLocalTime(),
                    daylightMinutes,
                    weatherCode,
                    WmoWeatherCode.description(weatherCode)));
        }

        return List.copyOf(results);
    }

    private static void validateLengths(
            final OpenMeteoResponse.Daily daily)
            throws JsonProcessingException {

        if (daily.time() == null) {
            throw invalid("daily.time is missing");
        }

        var expected = daily.time().size();
        validateLength(
                "temperature_2m_max",
                daily.maximumTemperatures(),
                expected);
        validateLength(
                "temperature_2m_min",
                daily.minimumTemperatures(),
                expected);
        validateLength(
                "temperature_2m_mean",
                daily.meanTemperatures(),
                expected);
        validateLength("sunrise", daily.sunrise(), expected);
        validateLength("sunset", daily.sunset(), expected);
        validateLength(
                "daylight_duration",
                daily.daylightDurationSeconds(),
                expected);
        validateLength(
                "weather_code",
                daily.weatherCodes(),
                expected);
    }

    private static void validateLength(
            final String name,
            final List<?> values,
            final int expected)
            throws JsonProcessingException {
        if (values == null || values.size() != expected) {
            throw invalid(
                    "Daily array '%s' has an unexpected length"
                            .formatted(name));
        }
    }

    private static double requiredNumber(
            final Double value,
            final String name,
            final int index) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Open-Meteo value '%s' is null at index %d"
                            .formatted(name, index));
        }
        return value;
    }

    private static JsonProcessingException invalid(
            final String message) {
        return new JsonProcessingException(message) {
        };
    }

    private static String encode(final String value) {
        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8)
                .replace("+", "%20");
    }

    private static String abbreviated(final String value) {
        if (value == null) {
            return "";
        }
        var normalised = value.replaceAll("\\s+", " ").trim();
        return normalised.length() <= 500
                ? normalised
                : normalised.substring(0, 500) + "...";
    }
}
