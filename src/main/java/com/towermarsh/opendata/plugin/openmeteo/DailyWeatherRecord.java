/*
 *  Filename: DailyWeatherRecord.java
 * 
 *  (C) Copyright Terry Curran 2026. All rights reserved
 * 
 *  This software is provided 'as-is', without any express or implied
 *  warranty.  In no event will the author be held liable for any damages
 *  arising from the use of this software.
 * 
 *  Permission is granted to anyone to use this software for any purpose,
 *  including commercial applications, and to alter it and redistribute it
 *  freely, subject to the following restrictions:
 * 
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgement in the product documentation would be
 *     appreciated but is not required.
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *  3. This notice may not be removed or altered from any source distribution.
 * 
 *  The author may be contacted by email to the following address:
 * 
 *  terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.plugin.openmeteo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * One daily historical weather observation returned by Open-Meteo.
 *
 * @param observationDate observation date
 * @param locationName configured display name for the location
 * @param latitude configured latitude
 * @param longitude configured longitude
 * @param minimumTemperatureC minimum two-metre temperature in degrees Celsius
 * @param maximumTemperatureC maximum two-metre temperature in degrees Celsius
 * @param meanTemperatureC mean two-metre temperature in degrees Celsius
 * @param sunrise local sunrise time
 * @param sunset local sunset time
 * @param daylightMinutes daylight duration rounded to minutes
 * @param weatherCode WMO weather interpretation code
 * @param weatherDescription human-readable weather description
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public record DailyWeatherRecord(
        LocalDate observationDate,
        String locationName,
        double latitude,
        double longitude,
        double minimumTemperatureC,
        double maximumTemperatureC,
        double meanTemperatureC,
        LocalTime sunrise,
        LocalTime sunset,
        long daylightMinutes,
        int weatherCode,
        String weatherDescription) {

    public DailyWeatherRecord {
        Objects.requireNonNull(observationDate, "observationDate");
        Objects.requireNonNull(locationName, "locationName");
        Objects.requireNonNull(sunrise, "sunrise");
        Objects.requireNonNull(sunset, "sunset");
        Objects.requireNonNull(weatherDescription, "weatherDescription");

        if (locationName.isBlank()) {
            throw new IllegalArgumentException("locationName must not be blank");
        }
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("latitude must be between -90 and 90");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("longitude must be between -180 and 180");
        }
        if (daylightMinutes < 0) {
            throw new IllegalArgumentException("daylightMinutes must not be negative");
        }
    }
}
