/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.transform;

import com.towermarsh.opendata.plugin.openmeteo.initialise.OpenMeteoConfiguration;
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
* @version 1.0.0
*/
public class OpenMeteoTransformer {

    /**
     *
     * @param response
     * @param configuration
     * @return
     * @throws IllegalArgumentException
     */
    public List<DailyWeatherRecord> transform(
            OpenMeteoResponse response,
            OpenMeteoConfiguration configuration) throws IllegalArgumentException {
        Objects.requireNonNull(response, "response");
        Objects.requireNonNull(configuration, "configuration");
        var daily = requireValue(response.daily(), "response.daily");
        var dates = requireValue(daily.time(), "daily.time");
        var weatherCodes = requireValue(daily.weatherCodes(), "daily.weatherCodes");
        var daylightDurations = requireValue(
                daily.daylightDurationSeconds(),
                "daily.daylightDurationSeconds");
        var minimumTemperatures = requireValue(
                daily.minimumTemperatures(),
                "daily.minimumTemperatures");
        var maximumTemperatures = requireValue(
                daily.maximumTemperatures(),
                "daily.maximumTemperatures");
        var meanTemperatures = requireValue(
                daily.meanTemperatures(),
                "daily.meanTemperatures");
        var sunriseTimes = requireValue(daily.sunrise(), "daily.sunrise");
        var sunsetTimes = requireValue(daily.sunset(), "daily.sunset");
        var results = new ArrayList<DailyWeatherRecord>(dates.size());
        try {
            for (var index = 0; index < dates.size(); index++) {
                 int weatherCode = requireValue(
                        weatherCodes.get(index),
                        "daily.weatherCodes[%d]".formatted(index));
                long daylightMinutes =
                        Math.round(requireValue(
                                daylightDurations.get(index),
                                "daily.daylightDurationSeconds[%d]".formatted(index)) / 60.0);
                results.add(new DailyWeatherRecord(
                        LocalDate.parse(
                                requireValue(dates.get(index), "daily.time[%d]".formatted(index)),
                                DateTimeFormatter.ISO_LOCAL_DATE),
                        configuration.locationName(),
                        configuration.latitude(),
                        configuration.longitude(),
                        requireValue(
                                minimumTemperatures.get(index),
                                "daily.minimumTemperatures[%d]".formatted(index)),
                        requireValue(
                                maximumTemperatures.get(index),
                                "daily.maximumTemperatures[%d]".formatted(index)),
                        requireValue(
                                meanTemperatures.get(index),
                                "daily.meanTemperatures[%d]".formatted(index)),
                        LocalDateTime.parse(
                                requireValue(
                                        sunriseTimes.get(index),
                                        "daily.sunrise[%d]".formatted(index)),
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalTime(),
                        LocalDateTime.parse(
                                requireValue(
                                        sunsetTimes.get(index),
                                        "daily.sunset[%d]".formatted(index)),
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalTime(),
                        daylightMinutes,
                        weatherCode,
                        WmoWeatherCode.description(weatherCode)));
            }
            return List.copyOf(results);
        } catch (DateTimeParseException | IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Open-Meteo daily data could not be transformed",
                    exception);
        }
    }

    /**
     * Validates value.
     *
     * @param value value of value
     * @param description input description
     * @return result of require value
     */
    private static <T> T requireValue(T value, String description) {
        if (value == null) {
            throw new IllegalArgumentException(description + " must not be null");
        }
        return value;
    }
}
