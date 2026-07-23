package com.towermarsh.opendata.plugin.openmeteo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OpenMeteoApiClientTest {

    @Test
    void parsesDailyResponse() throws Exception {
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

        var client = new OpenMeteoApiClient(
                configuration,
                HttpClient.newHttpClient(),
                new ObjectMapper());

        var json = """
                {
                  "latitude": 51.67,
                  "longitude": -0.78,
                  "timezone": "Europe/London",
                  "daily": {
                    "time": ["2024-01-01"],
                    "temperature_2m_max": [9.4],
                    "temperature_2m_min": [3.1],
                    "temperature_2m_mean": [6.2],
                    "sunrise": ["2024-01-01T08:08"],
                    "sunset": ["2024-01-01T16:04"],
                    "daylight_duration": [28560.0],
                    "weather_code": [61]
                  }
                }
                """;

        var records = client.parseResponse(json);

        assertEquals(1, records.size());
        assertEquals(
                LocalDate.of(2024, 1, 1),
                records.get(0).observationDate());
        assertEquals(476, records.get(0).daylightMinutes());
        assertEquals("Slight rain", records.get(0).weatherDescription());
    }
}
