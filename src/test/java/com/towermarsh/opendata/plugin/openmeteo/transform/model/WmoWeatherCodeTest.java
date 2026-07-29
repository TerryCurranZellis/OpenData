/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.transform.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
class WmoWeatherCodeTest {

    @Test
    void mapsKnownWeatherCodes() {
        assertEquals("Clear sky", WmoWeatherCode.description(0));
        assertEquals("Heavy freezing rain", WmoWeatherCode.description(67));
        assertEquals(
                "Thunderstorm with heavy hail",
                WmoWeatherCode.description(99));
    }

    @Test
    void mapsUnknownWeatherCode() {
        assertEquals("Unknown", WmoWeatherCode.description(1234));
    }
}
