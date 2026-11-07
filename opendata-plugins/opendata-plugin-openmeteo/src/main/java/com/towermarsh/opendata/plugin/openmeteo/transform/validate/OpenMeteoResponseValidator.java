/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.transform.validate;

import com.towermarsh.opendata.plugin.openmeteo.transform.OpenMeteoResponse;
import java.util.List;
import java.util.Objects;

/**
 * Validates the parallel daily arrays returned by Open-Meteo.
 *
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public class OpenMeteoResponseValidator {

    /**
     *
     * @param response
     * @return
     * @throws IllegalArgumentException
     */
    public OpenMeteoResponse validate(OpenMeteoResponse response)
            throws IllegalArgumentException {
        Objects.requireNonNull(response, "response");
        var daily = response.daily();
        if (daily == null || daily.time() == null) {
            throw new IllegalArgumentException("Open-Meteo response did not contain daily data");
        }
        var expected = daily.time().size();
        validateLength("temperature_2m_max", daily.maximumTemperatures(), expected);
        validateLength("temperature_2m_min", daily.minimumTemperatures(), expected);
        validateLength("temperature_2m_mean", daily.meanTemperatures(), expected);
        validateLength("sunrise", daily.sunrise(), expected);
        validateLength("sunset", daily.sunset(), expected);
        validateLength("daylight_duration", daily.daylightDurationSeconds(), expected);
        validateLength("weather_code", daily.weatherCodes(), expected);
        return response;
    }

    /**
     * Validates length.
     *
     * @param name requested name
     * @param values value of values
     * @param expected input expected
     * @throws IllegalArgumentException if validate length fails
     */
    private static void validateLength(
            String name,
            List<?> values,
            int expected) throws IllegalArgumentException {
        if (values == null || values.size() != expected) {
            throw new IllegalArgumentException(
                    "Open-Meteo daily array '%s' has an unexpected length".formatted(name));
        }
        if (values.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "Open-Meteo daily array '%s' contains a null value".formatted(name));
        }
    }
}
