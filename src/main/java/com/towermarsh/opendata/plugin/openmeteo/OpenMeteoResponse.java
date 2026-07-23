package com.towermarsh.opendata.plugin.openmeteo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Jackson response model for the Open-Meteo historical weather API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(
        double latitude,
        double longitude,
        String timezone,
        Daily daily) {

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
