# ADR-0028: Introduce the OpenMeteo historical weather plugin

- **Status:** Accepted
- **Date:** 2026-07-23
- **Decision owners:** OpenData maintainers

## Context

Existing Java code downloads historical daily weather data from the Open-Meteo
archive API. It must follow OpenData configuration, HTTP, JSON, logging and
immutable-model conventions.

## Decision

Integrate plugin identifier `openmeteo` using JDK HTTP, Jackson Databind,
`java.util.logging` and immutable records. Move endpoint, coordinates, timezone,
date range and timeout into plugin properties. No API credential is required for
the public archive endpoint.

## Consequences

### Positive

- weather acquisition becomes a second reference plugin for API/JSON sources;
- date ranges and locations become configurable;
- no additional JSON library is introduced.

### Negative or limiting

- large ranges may require batching;
- upstream historical data can be revised;
- large date ranges can put pressure on the remote service and transaction size.

## Implementation notes

Implemented by the registered `OpenMeteoPlugin`, provider-local
`OpenMeteoDownloader`, response extractor/validator/transformer,
`load.OpenMeteoRepository`, `openmeteo.Location` and
`openmeteo.DailyWeather`.
Production acceptance still requires a live write and idempotency test.
