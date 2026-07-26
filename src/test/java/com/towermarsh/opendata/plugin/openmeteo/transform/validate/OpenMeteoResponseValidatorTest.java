package com.towermarsh.opendata.plugin.openmeteo.transform.validate;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.towermarsh.opendata.plugin.openmeteo.extract.OpenMeteoResponse;
import com.towermarsh.opendata.plugin.openmeteo.exception.OpenMeteoException;
import java.util.List;
import org.junit.jupiter.api.Test;

class OpenMeteoResponseValidatorTest {
    private final OpenMeteoResponseValidator validator = new OpenMeteoResponseValidator();

    @Test
    void returnsResponseWhenDailyArraysHaveMatchingLengths() throws OpenMeteoException {
        final var response = response(List.of(1));
        assertSame(response, validator.validate(response));
    }

    @Test
    void rejectsMismatchedDailyArrayLengths() {
        final var response = response(List.of());
        assertThrows(OpenMeteoException.class, () -> validator.validate(response));
    }

    private static OpenMeteoResponse response(final List<Integer> weatherCodes) {
        return new OpenMeteoResponse(
                51.5,
                -0.1,
                "Europe/London",
                new OpenMeteoResponse.Daily(
                        List.of("2026-01-01"),
                        List.of(5.0),
                        List.of(1.0),
                        List.of(3.0),
                        List.of("2026-01-01T08:00"),
                        List.of("2026-01-01T16:00"),
                        List.of(28_800.0),
                        weatherCodes));
    }
}
