/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.transform.validate;

import com.towermarsh.opendata.plugin.openmeteo.exception.OpenMeteoException;
import com.towermarsh.opendata.plugin.openmeteo.extract.OpenMeteoResponse;
import java.util.List;
import java.util.Objects;

/** Validates the parallel daily arrays returned by Open-Meteo.  *
* @author Terry Curran
* @version 1.0.0
*/
public final class OpenMeteoResponseValidator {

    /**
     *
     * @param response
     * @return
     * @throws OpenMeteoException
     */
    public OpenMeteoResponse validate(final OpenMeteoResponse response)
            throws OpenMeteoException {
        Objects.requireNonNull(response, "response");
        final OpenMeteoResponse.Daily daily = response.daily();
        if (daily == null || daily.time() == null) {
            throw new OpenMeteoException("Open-Meteo response did not contain daily data");
        }
        final int expected = daily.time().size();
        validateLength("temperature_2m_max", daily.maximumTemperatures(), expected);
        validateLength("temperature_2m_min", daily.minimumTemperatures(), expected);
        validateLength("temperature_2m_mean", daily.meanTemperatures(), expected);
        validateLength("sunrise", daily.sunrise(), expected);
        validateLength("sunset", daily.sunset(), expected);
        validateLength("daylight_duration", daily.daylightDurationSeconds(), expected);
        validateLength("weather_code", daily.weatherCodes(), expected);
        return response;
    }

    private static void validateLength(
            final String name,
            final List<?> values,
            final int expected) throws OpenMeteoException {
        if (values == null || values.size() != expected) {
            throw new OpenMeteoException(
                    "Open-Meteo daily array '%s' has an unexpected length".formatted(name));
        }
        if (values.stream().anyMatch(Objects::isNull)) {
            throw new OpenMeteoException(
                    "Open-Meteo daily array '%s' contains a null value".formatted(name));
        }
    }
}
