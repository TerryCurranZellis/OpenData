# ADR-0032: Store OpenMeteo data in dedicated relational tables

**Status:** Superseded by [ADR-0040](ADR-0040-openmeteo-relational-storage.md)  
**Date:** 24 July 2026

## Context

The OpenMeteo plugin previously returned Java records but did not persist them. The project decision is that each plugin owns its target schema/tables while shared operational metadata remains in `core`.

## Decision

Create schema `openmeteo` with `Location` and `DailyWeather`. Use a stable `LocationKey` separate from the display name. Key daily data by `(LocationId, ObservationDate)` and link inserted or changed rows to `core.PluginRun.LastRunId`.

## Consequences

- Repeated loads are naturally idempotent.
- Location display names and coordinates can be maintained without changing daily keys.
- Row lineage is available through `LastRunId`.
- The model can add more locations without altering the daily table shape.
- Historical weather remains separate from Ofgem and other plugin data.

## Alternatives

- One wide generic dataset table: rejected because it loses domain constraints and types.
- JSON-only storage: deferred as a possible raw-response archive, not the analytical model.
- Store location values on every daily row: rejected because it duplicates mutable metadata.
