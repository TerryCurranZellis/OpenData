/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.transform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import static com.towermarsh.opendata.foundation.CollectionCopies.immutableOrEmpty;

/**
 * Jackson response model for the Open-Meteo historical weather API.
 *
 * @param latitude latitude of record
 * @param longitude longitude of record
 * @param timezone timezone of record
 * @param daily this is a daily record
 * @author Terry Curran
 * @since 1.0.0
 * @version 4.1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(
        double latitude,
        double longitude,
        String timezone,
        Daily daily) {

    /**
     * Normalises the nested daily response to a defensive immutable snapshot.
     *
     * @param latitude location latitude
     * @param longitude location longitude
     * @param timezone resolved API timezone
     * @param daily daily weather arrays, if supplied
     */
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
     *
     * @author Terry Curran
     * @version 4.1.0
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

        /**
         * Copies the daily observation arrays into immutable lists.
         *
         * @param time observation dates
         * @param maximumTemperatures daily high temperatures
         * @param minimumTemperatures daily low temperatures
         * @param meanTemperatures daily mean temperatures
         * @param sunrise sunrise observations
         * @param sunset sunset observations
         * @param daylightDurationSeconds daylight duration observations
         * @param weatherCodes WMO weather codes
         */
        public Daily {
            time = immutableOrEmpty(time);
            maximumTemperatures = immutableOrEmpty(maximumTemperatures);
            minimumTemperatures = immutableOrEmpty(minimumTemperatures);
            meanTemperatures = immutableOrEmpty(meanTemperatures);
            sunrise = immutableOrEmpty(sunrise);
            sunset = immutableOrEmpty(sunset);
            daylightDurationSeconds = immutableOrEmpty(daylightDurationSeconds);
            weatherCodes = immutableOrEmpty(weatherCodes);
        }

        /**
         * Returns an immutable snapshot of observation dates.
         *
         * @return observation dates
         */
        @Override
        public List<String> time() {
            return List.copyOf(time);
        }

        /**
         * Returns an immutable snapshot of daily maximum temperatures.
         *
         * @return daily high temperatures in degrees Celsius
         */
        @Override
        public List<Double> maximumTemperatures() {
            return List.copyOf(maximumTemperatures);
        }

        /**
         * Returns an immutable snapshot of daily minimum temperatures.
         *
         * @return daily low temperatures in degrees Celsius
         */
        @Override
        public List<Double> minimumTemperatures() {
            return List.copyOf(minimumTemperatures);
        }

        /**
         * Returns an immutable snapshot of daily mean temperatures.
         *
         * @return daily mean temperatures in degrees Celsius
         */
        @Override
        public List<Double> meanTemperatures() {
            return List.copyOf(meanTemperatures);
        }

        /**
         * Returns an immutable snapshot of sunrise times.
         *
         * @return sunrise times
         */
        @Override
        public List<String> sunrise() {
            return List.copyOf(sunrise);
        }

        /**
         * Returns an immutable snapshot of sunset times.
         *
         * @return sunset times
         */
        @Override
        public List<String> sunset() {
            return List.copyOf(sunset);
        }

        /**
         * Returns an immutable snapshot of daylight duration values.
         *
         * @return daylight duration values in seconds
         */
        @Override
        public List<Double> daylightDurationSeconds() {
            return List.copyOf(daylightDurationSeconds);
        }

        /**
         * Returns an immutable snapshot of weather interpretation codes.
         *
         * @return WMO weather codes
         */
        @Override
        public List<Integer> weatherCodes() {
            return List.copyOf(weatherCodes);
        }
    }

}
