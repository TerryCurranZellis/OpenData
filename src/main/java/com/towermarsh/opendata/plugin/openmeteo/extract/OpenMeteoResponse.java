/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.extract;

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

    }
}
