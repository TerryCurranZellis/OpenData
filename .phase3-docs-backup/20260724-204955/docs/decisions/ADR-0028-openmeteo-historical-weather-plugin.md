# ADR-0028: Introduce the OpenMeteo historical weather plugin

- **Status:** Proposed
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
- the weather persistence schema and complete runtime orchestration remain open.

## Implementation notes

Code preparation exists, but final integration and persistence verification are
outstanding. Retain Proposed until the plugin executes through the common
pipeline and writes an agreed SQL Server schema.
