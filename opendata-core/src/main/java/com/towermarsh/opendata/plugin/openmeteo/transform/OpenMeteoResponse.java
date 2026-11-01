/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.transform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Jackson response model for the Open-Meteo historical weather API.
 *
 * @param latitude latitude of record
 * @param longitude longitude of record
 * @param timezone timezone of record
 * @param daily this is a daily record
 * @author Terry Curran
 * @version 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(
        double latitude,
        double longitude,
        String timezone,
        Daily daily) {

    public OpenMeteoResponse {
        daily = daily == null ? null : new Daily(
                daily.time(),
                daily.maximumTemperatures(),
                daily.minimumTemperatures(),
                daily.meanTemperatures(),
                daily.sunrise(),
                daily.sunset(),
                daily.daylightDurationSeconds(),
                daily.weatherCodes());
    }

    /**
     * Daily weather arrays returned by the Open-Meteo archive API.
     *
     * @param time observation dates
     * @param maximumTemperatures daily maximum temperatures in degrees Celsius
     * @param minimumTemperatures daily minimum temperatures in degrees Celsius
     * @param meanTemperatures daily mean temperatures in degrees Celsius
     * @param sunrise sunrise times
     * @param sunset sunset times
     * @param daylightDurationSeconds daylight duration values in seconds
     * @param weatherCodes WMO weather interpretation codes
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Daily(
            List<String> time,
            @JsonProperty("temperature_2m_max")
            List<Double> maximumTemperatures,
            @JsonProperty("temperature_2m_min")
            List<Double> minimumTemperatures,
            @JsonProperty("temperature_2m_mean")
            List<Double> meanTemperatures,
            List<String> sunrise,
            List<String> sunset,
            @JsonProperty("daylight_duration")
            List<Double> daylightDurationSeconds,
            @JsonProperty("weather_code")
            List<Integer> weatherCodes) {

        public Daily {
            time = immutableCopy(time);
            maximumTemperatures = immutableCopy(maximumTemperatures);
            minimumTemperatures = immutableCopy(minimumTemperatures);
            meanTemperatures = immutableCopy(meanTemperatures);
            sunrise = immutableCopy(sunrise);
            sunset = immutableCopy(sunset);
            daylightDurationSeconds = immutableCopy(daylightDurationSeconds);
            weatherCodes = immutableCopy(weatherCodes);
        }

        @Override
        public List<String> time() {
            return List.copyOf(time);
        }

        @Override
        public List<Double> maximumTemperatures() {
            return List.copyOf(maximumTemperatures);
        }

        @Override
        public List<Double> minimumTemperatures() {
            return List.copyOf(minimumTemperatures);
        }

        @Override
        public List<Double> meanTemperatures() {
            return List.copyOf(meanTemperatures);
        }

        @Override
        public List<String> sunrise() {
            return List.copyOf(sunrise);
        }

        @Override
        public List<String> sunset() {
            return List.copyOf(sunset);
        }

        @Override
        public List<Double> daylightDurationSeconds() {
            return List.copyOf(daylightDurationSeconds);
        }

        @Override
        public List<Integer> weatherCodes() {
            return List.copyOf(weatherCodes);
        }
    }

    private static <T> List<T> immutableCopy(final List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
