/*
 * Filename: OpenMeteoTransformer.java
 *
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.transform;

import com.towermarsh.opendata.plugin.openmeteo.config.OpenMeteoConfiguration;
import com.towermarsh.opendata.plugin.openmeteo.exception.OpenMeteoException;
import com.towermarsh.opendata.plugin.openmeteo.extract.OpenMeteoResponse;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.DailyWeatherRecord;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.WmoWeatherCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Converts a validated Open-Meteo API response into domain records.  *
* @author Terry Curran
* @version 17 July 2026
*/
public final class OpenMeteoTransformer {

    /**
     *
     * @param response
     * @param configuration
     * @return
     * @throws OpenMeteoException
     */
    public List<DailyWeatherRecord> transform(
            final OpenMeteoResponse response,
            final OpenMeteoConfiguration configuration) throws OpenMeteoException {
        Objects.requireNonNull(response, "response");
        Objects.requireNonNull(configuration, "configuration");
        final var daily = response.daily();
        final var results = new ArrayList<DailyWeatherRecord>(daily.time().size());
        try {
            for (int index = 0; index < daily.time().size(); index++) {
                final int weatherCode = daily.weatherCodes().get(index);
                final long daylightMinutes =
                        Math.round(daily.daylightDurationSeconds().get(index) / 60.0);
                results.add(new DailyWeatherRecord(
                        LocalDate.parse(daily.time().get(index), DateTimeFormatter.ISO_LOCAL_DATE),
                        configuration.locationName(),
                        configuration.latitude(),
                        configuration.longitude(),
                        daily.minimumTemperatures().get(index),
                        daily.maximumTemperatures().get(index),
                        daily.meanTemperatures().get(index),
                        LocalDateTime.parse(
                                daily.sunrise().get(index),
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalTime(),
                        LocalDateTime.parse(
                                daily.sunset().get(index),
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalTime(),
                        daylightMinutes,
                        weatherCode,
                        WmoWeatherCode.description(weatherCode)));
            }
            return List.copyOf(results);
        } catch (DateTimeParseException | NullPointerException | IllegalArgumentException exception) {
            throw new OpenMeteoException(
                    "Open-Meteo daily data could not be transformed",
                    exception);
        }
    }
}
