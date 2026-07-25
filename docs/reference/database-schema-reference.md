# Database Schema Reference

**Document ID:** REF-DB-SCHEMA-001  
**Version:** 2.0  
**Status:** Two coexisting schema foundations  
**Baseline date:** 24 July 2026

## Database and principals

| Object | Name |
|---|---|
| Database | `OpenData` |
| SQL login | `OpenData` |
| Database user | `OpenData` |
| Application role | `opendata_app` in the `sql/sqlserver` script set |

## Current runtime audit schema

The current multi-plugin runtime uses the root ordered scripts:

| Table | Purpose |
|---|---|
| `core.PluginRun` | One row per non-dry-run plugin task, keyed by the UUID logging correlation id |
| `openmeteo.Location` | Stable configured location |
| `openmeteo.DailyWeather` | Daily weather values and `LastRunId` lineage |

Apply `sql/001-core-plugin-run.sql` before `sql/002-openmeteo.sql`, then apply `sql/003-permissions.sql`.

## Earlier Phase 3 ingestion foundation

The separate `sql/sqlserver` script set defines:

- `core.schema_version`, `core.dataset`, `core.ingestion_run`, `core.source_file` and `core.ingestion_error`;
- Ofgem dimensions, period, level and component tables.

Those tables are used by `database.audit` and `ofgem.database` classes, but they are not the audit mechanism used by `PluginExecutionCoordinator`, which writes `core.PluginRun` through `JdbcPluginRunAudit`. The two models must not be described as one unified production schema until a consolidation decision and migration are implemented.

## Keys and lineage

- `core.PluginRun.RunId` is the plugin task correlation UUID.
- `openmeteo.Location.LocationKey` is the stable natural key.
- `openmeteo.DailyWeather` uses `(LocationId, ObservationDate)` as its primary key.
- `openmeteo.DailyWeather.LastRunId` references the plugin run that last inserted or changed the row.
- The earlier Ofgem model uses source-file and ingestion-run lineage.

![OpenData database schemas](../diagrams/generated/opendata-database.svg)
