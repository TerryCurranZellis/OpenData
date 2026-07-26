# OpenMeteo SQL Server schema reference

**Document ID:** REF-OPENMETEO-SCHEMA-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

## `core.PluginRun`

One row per plugin task. `RunId` is also the correlation id used in logging. A task starts as `RUNNING` and ends as `SUCCESS`, `FAILED` or `CANCELLED`. Dry runs do not create audit rows.

## `openmeteo.Location`

| Column | Type | Notes |
|---|---|---|
| `LocationId` | `bigint identity` | Primary key |
| `LocationKey` | `nvarchar(100)` | Unique stable key |
| `LocationName` | `nvarchar(200)` | Display name |
| `Latitude` | `decimal(9,6)` | -90 to 90 |
| `Longitude` | `decimal(9,6)` | -180 to 180 |
| `TimeZone` | `nvarchar(100)` | IANA zone id |
| `CreatedAt`, `UpdatedAt` | `datetime2(3)` | UTC audit times |

## `openmeteo.DailyWeather`

The clustered primary key is `(LocationId, ObservationDate)`. Temperatures are `decimal(6,2)`, times are local `time(0)`, and `LastRunId` references `core.PluginRun`.

A separate date-leading index supports cross-location date analysis. A `LastRunId` index supports lineage and operational investigation.
