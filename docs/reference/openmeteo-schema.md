# OpenMeteo Schema and Persistence Reference

**Document ID:** REF-OPENMETEO-SCHEMA-001
**Version:** 3.0.0  
**Baseline date:** 15 August 2026  

## Business tables

`openmeteo.Location` stores a stable location key, display name, coordinates and
timezone. `openmeteo.DailyWeather` stores one daily observation per location and
date, including temperatures, sunrise/sunset, daylight, weather code,
description and last run identity.

## Configurable identifiers

Schema and table property values are passed through `SqlIdentifiers` before
being formatted into SQL. Only safe single identifiers are accepted. Qualified
table text is produced as bracketed SQL Server identifiers.

Values remain prepared-statement parameters; the identifier utility is not a
replacement for parameterisation.

## Staging table

`#OpenMeteoDaily` exists only on the borrowed SQL Server session. Records are
inserted through `JdbcBatchExecutor` using `database.batch-size`. The repository
fails if the affected staging count differs from the input count.

## Set-based reconciliation

- update joins stage to target for the same location/date and changes only rows
  whose persisted values differ;
- insert selects staged dates not present for the location; and
- unchanged records are calculated as input minus inserted minus updated.

## Pooled-session safety

The transaction cleanup callback drops the temporary table and turns
`XACT_ABORT` off. This is required because connection-scoped state can remain on
a pooled physical SQL Server session after logical close.
