/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.transform.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * WMO weather interpretation codes returned by Open-Meteo.
 *
 * <p>Each recognised provider code has a typed constant. Unrecognised numeric
 * values resolve to {@link #UNKNOWN}; the original code stays on the record.</p>
 *
 * @author Terry Curran
 * @since 4.2.0
 * @version 4.2.0
 */
public enum WmoWeatherCode {
    CLEAR_SKY(0, "Clear sky"),
    MAINLY_CLEAR(1, "Mainly clear"),
    PARTLY_CLOUDY(2, "Partly cloudy"),
    OVERCAST(3, "Overcast"),
    FOG(45, "Fog"),
    DEPOSITING_RIME_FOG(48, "Depositing rime fog"),
    LIGHT_DRIZZLE(51, "Light drizzle"),
    MODERATE_DRIZZLE(53, "Moderate drizzle"),
    DENSE_DRIZZLE(55, "Dense drizzle"),
    LIGHT_FREEZING_DRIZZLE(56, "Light freezing drizzle"),
    DENSE_FREEZING_DRIZZLE(57, "Dense freezing drizzle"),
    SLIGHT_RAIN(61, "Slight rain"),
    MODERATE_RAIN(63, "Moderate rain"),
    HEAVY_RAIN(65, "Heavy rain"),
    LIGHT_FREEZING_RAIN(66, "Light freezing rain"),
    HEAVY_FREEZING_RAIN(67, "Heavy freezing rain"),
    SLIGHT_SNOWFALL(71, "Slight snowfall"),
    MODERATE_SNOWFALL(73, "Moderate snowfall"),
    HEAVY_SNOWFALL(75, "Heavy snowfall"),
    SNOW_GRAINS(77, "Snow grains"),
    SLIGHT_RAIN_SHOWERS(80, "Slight rain showers"),
    MODERATE_RAIN_SHOWERS(81, "Moderate rain showers"),
    VIOLENT_RAIN_SHOWERS(82, "Violent rain showers"),
    SLIGHT_SNOW_SHOWERS(85, "Slight snow showers"),
    HEAVY_SNOW_SHOWERS(86, "Heavy snow showers"),
    THUNDERSTORM(95, "Thunderstorm"),
    THUNDERSTORM_SLIGHT_HAIL(96, "Thunderstorm with slight hail"),
    THUNDERSTORM_HEAVY_HAIL(99, "Thunderstorm with heavy hail"),
    UNKNOWN(-1, "Unknown");

    private static final Map<Integer, WmoWeatherCode> BY_CODE = Arrays.stream(values())
            .filter(value -> value != UNKNOWN)
            .collect(Collectors.toUnmodifiableMap(WmoWeatherCode::code, Function.identity()));

    private final int code;
    private final String description;

    WmoWeatherCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Returns the numeric interpretation code.
     *
     * @return WMO code, or -1 for {@link #UNKNOWN}
     */
    public int code() {
        return code;
    }

    /**
     * Returns a concise weather description.
     *
     * @return weather description
     */
    public String description() {
        return description;
    }

    /**
     * Resolves a numeric provider code to a typed value.
     *
     * @param code provider WMO code
     * @return matching constant or {@link #UNKNOWN}
     */
    public static WmoWeatherCode fromCode(int code) {
        return BY_CODE.getOrDefault(code, UNKNOWN);
    }

    /**
     * Returns the description for a numeric weather code.
     *
     * @param code WMO code
     * @return description, or "Unknown" for unrecognised codes
     */
    public static String description(int code) {
        return fromCode(code).description();
    }
}
