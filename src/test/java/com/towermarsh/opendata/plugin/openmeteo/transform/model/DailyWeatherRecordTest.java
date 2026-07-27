/*
 * Filename: DailyWeatherRecordTest.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.plugin.openmeteo.transform.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.*;
import org.junit.jupiter.api.Test;

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
