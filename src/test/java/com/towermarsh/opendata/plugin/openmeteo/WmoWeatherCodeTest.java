package com.towermarsh.opendata.plugin.openmeteo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

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
