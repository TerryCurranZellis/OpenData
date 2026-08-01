# ADR-0040: Store OpenMeteo data in dedicated relational tables

- **Status:** Accepted
- **Date:** 24 July 2026
- **Decision owners:** OpenData maintainers

## Context

The OpenMeteo plugin produces typed daily records and requires idempotent,
queryable persistence. Shared operational metadata belongs in `core`, while
plugin business data belongs in a plugin-owned schema.

## Decision

Create `openmeteo.Location` and `openmeteo.DailyWeather`. Use an immutable
`LocationKey` separate from the display name. Key daily data by
`(LocationId, ObservationDate)` and link inserted or changed rows to
`core.PluginRun.RunId`.

## Consequences

### Positive

- repeated identical loads make no data changes;
- display names and coordinates can be maintained without changing daily keys;
- multiple locations use the same table design;
- changed rows retain the UUID of the last modifying run.

### Negative or limiting

- changing a location key creates a different logical location;
- raw JSON is not retained;
- the schema currently depends on the generic plugin-run audit model.

## Alternatives considered

- A generic wide dataset table was rejected because it loses domain types and
  constraints.
- JSON-only storage was deferred as a possible raw-response archive.
- Repeating location values on each daily row was rejected as duplication.

## Implementation notes

Implemented by `sql/007-create-openmeteo-schema.sql`, `load.OpenMeteoRepository` and
`config.OpenMeteoConfiguration`.

This is the canonical uniquely numbered record for the decision originally
stored as `ADR-0032-openmeteo-relational-storage.md`.
