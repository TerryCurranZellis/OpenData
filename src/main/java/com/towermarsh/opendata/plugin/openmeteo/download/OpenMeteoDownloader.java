/*
 * Filename: OpenMeteoDownloader.java
 *
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.download;

import com.towermarsh.opendata.plugin.openmeteo.config.OpenMeteoConfiguration;
import com.towermarsh.opendata.plugin.openmeteo.exception.OpenMeteoException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Objects;
import java.util.logging.Logger;

/** Downloads raw JSON from the Open-Meteo archive API.  *
* @author Terry Curran
* @version 17 July 2026
*/
public final class OpenMeteoDownloader {
    private static final Logger LOGGER =
            Logger.getLogger(OpenMeteoDownloader.class.getName());
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

    /**
     * Creates a downloader using a default HTTP client.
     *
     * @param configuration typed Open-Meteo configuration
     */
    public OpenMeteoDownloader(final OpenMeteoConfiguration configuration) {
        this(
                configuration,
                HttpClient.newBuilder()
                        .connectTimeout(configuration.connectTimeout())
                        .build());
    }

    OpenMeteoDownloader(
            final OpenMeteoConfiguration configuration,
            final HttpClient httpClient) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
    }

    /**
     * Downloads weather history for the configured default date range.
     *
     * @return raw JSON response
     * @throws OpenMeteoException if the API call fails
     */
    public String download() throws OpenMeteoException {
        final var today = LocalDate.now(configuration.timezone());
        final var range = configuration.resolveDateRange(today);
        return download(range.startDate(), range.endDate());
    }

    /**
     * Downloads weather history for an explicit date range.
     *
     * @param startDate inclusive start date
     * @param endDate inclusive end date
     * @return raw JSON response
     * @throws OpenMeteoException if the API call fails
     */
    public String download(final LocalDate startDate, final LocalDate endDate)
            throws OpenMeteoException {
        Objects.requireNonNull(startDate, "startDate");
        Objects.requireNonNull(endDate, "endDate");
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must not be after endDate");
        }

        final var request = buildRequest(startDate, endDate);

        LOGGER.info(() -> "Downloading Open-Meteo history for %s from %s to %s"
                .formatted(configuration.locationName(), startDate, endDate));
        final HttpResponse<String> response;
        try {
            response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new OpenMeteoException("Open-Meteo request was interrupted", exception);
        } catch (IOException exception) {
            throw new OpenMeteoException(
                    "Unable to call the Open-Meteo archive API",
                    exception);
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new OpenMeteoException(
                    "Open-Meteo returned HTTP %d: %s"
                            .formatted(response.statusCode(), abbreviated(response.body())));
        }
        return response.body();
    }

    /**
     * Builds the HTTP request for one Open-Meteo query.
     *
     * @param startDate inclusive start date
     * @param endDate inclusive end date
     * @return configured HTTP request
     */
    HttpRequest buildRequest(final LocalDate startDate, final LocalDate endDate) {
        return HttpRequest.newBuilder()
                .uri(buildUri(startDate, endDate))
                .GET()
                .timeout(configuration.requestTimeout())
                .header("Accept", "application/json")
                .header("User-Agent", "OpenData-OpenMeteo/1.0")
                .build();
    }

    /**
     * Builds the query URI for one Open-Meteo request.
     *
     * @param startDate inclusive start date
     * @param endDate inclusive end date
     * @return request URI
     */
    URI buildUri(final LocalDate startDate, final LocalDate endDate) {
        final String separator = configuration.endpoint().toString().contains("?") ? "&" : "?";
        final String query = "latitude=" + configuration.latitude()
                + "&longitude=" + configuration.longitude()
                + "&start_date=" + startDate
                + "&end_date=" + endDate
                + "&daily=" + encode(DAILY_VARIABLES)
                + "&timezone=" + encode(configuration.timezone().getId());
        return URI.create(configuration.endpoint() + separator + query);
    }

    /**
     * URL-encodes a query parameter value.
     *
     * @param value value to encode
     * @return encoded query parameter value
     */
    private static String encode(final String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    /**
     * Abbreviates response text for concise error reporting.
     *
     * @param value text to abbreviate
     * @return abbreviated text
     */
    private static String abbreviated(final String value) {
        if (value == null) {
            return "";
        }
        final String normalised = value.replaceAll("\\s+", " ").trim();
        return normalised.length() <= 500
                ? normalised
                : normalised.substring(0, 500) + "...";
    }
}
