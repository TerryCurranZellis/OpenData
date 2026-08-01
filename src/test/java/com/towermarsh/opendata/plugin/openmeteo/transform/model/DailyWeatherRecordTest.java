/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.transform.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.*;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 1.0.0
 */
class DailyWeatherRecordTest {
    private DailyWeatherRecord valid() {
        return new DailyWeatherRecord(LocalDate.of(2026,7,24), "Home", 51.6207, -1.1098,
                10.2, 22.5, 16.3, LocalTime.of(5,12), LocalTime.of(21,3), 951, 1, "Mainly clear");
    }
    @Test void acceptsValidObservation() { assertEquals("Home", valid().locationName()); }
    @Test void rejectsBlankLocation() { assertThrows(IllegalArgumentException.class, () -> new DailyWeatherRecord(LocalDate.now(), " ", 0,0,0,0,0,LocalTime.NOON,LocalTime.NOON,0,0,"Clear")); }
    @Test void rejectsInvalidLatitude() { assertThrows(IllegalArgumentException.class, () -> new DailyWeatherRecord(LocalDate.now(), "X", 91,0,0,0,0,LocalTime.NOON,LocalTime.NOON,0,0,"Clear")); }
    @Test void rejectsInvalidLongitude() { assertThrows(IllegalArgumentException.class, () -> new DailyWeatherRecord(LocalDate.now(), "X", 0,181,0,0,0,LocalTime.NOON,LocalTime.NOON,0,0,"Clear")); }
    @Test void rejectsNegativeDaylight() { assertThrows(IllegalArgumentException.class, () -> new DailyWeatherRecord(LocalDate.now(), "X", 0,0,0,0,0,LocalTime.NOON,LocalTime.NOON,-1,0,"Clear")); }
}
