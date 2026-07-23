package com.towermarsh.opendata.plugin.openmeteo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OpenMeteoConfigurationTest {

    @Test
    void resolvesYesterdayWhenCurrentDateIsExcluded() {
        var configuration = new OpenMeteoConfiguration(
                URI.create("https://archive-api.open-meteo.com/v1/archive"),
                "Home",
                51.674304,
                -0.785602,
                ZoneId.of("Europe/London"),
                Duration.ofSeconds(30),
                Duration.ofSeconds(60),
                Optional.empty(),
                Optional.empty(),
                30,
                false);

        var range = configuration.resolveDateRange(
                LocalDate.of(2026, 7, 23));

        assertEquals(
                LocalDate.of(2026, 6, 22),
                range.startDate());
        assertEquals(
                LocalDate.of(2026, 7, 22),
                range.endDate());
    }
}
