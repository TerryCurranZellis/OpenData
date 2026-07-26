# Ingestion Audit Reference

**Document ID:** REF-AUDIT-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

## Plugin-run statuses

| Status | Terminal | Meaning |
|---|---|---|
| `RUNNING` | no | Coordinator task has begun |
| `SUCCESS` | yes | Plugin completed successfully |
| `DRY_RUN` | yes | Reserved status; dry runs currently do not persist audit rows |
| `FAILED` | yes | Plugin or audit completion failed |
| `CANCELLED` | yes | Task was interrupted or cancelled |

`core.PluginRun` records `RunId`, plugin, timestamps, thread/host, read,
inserted, updated and skipped counts, plus a truncated error message.

## Domain-ingestion statuses

| Status | Terminal | Meaning |
|---|---|---|
| `STARTED` | no | Run exists and processing has begun |
| `SUCCEEDED` | yes | All required work completed without rejected rows |
| `SUCCEEDED_WITH_REJECTIONS` | yes | Load completed but some extracted rows were rejected under policy |
| `FAILED` | yes | Required stage failed; success must not be inferred |
| `CANCELLED` | yes | Execution was deliberately stopped |

## Recommended stage names

`CONFIGURATION`, `DISCOVERY`, `DOWNLOAD`, `VALIDATION`, `PARSE`, `TRANSFORM`,
`LOAD`, `VERIFY`, `SHUTDOWN`.

## Implemented counters

| Column | Definition |
|---|---|
| `rows_extracted` | Typed records produced from the source before persistence |
| `rows_loaded` | Database fact rows confirmed written |
| `rows_rejected` | Extracted records excluded by an explicit rejection policy |

For a strict all-or-nothing Ofgem import, rejected normally remains zero and a
validation problem fails the run. More granular discovery/read/accepted counters
are possible future schema additions, not current columns.

## Duration

`duration_ms` is calculated from run start and finish timestamps in SQL Server.
The application should also log monotonic elapsed time for diagnostics, but the
database record remains the durable operational value.

## Transitional relationship

Ofgem writes a `core.PluginRun` row for the coordinator and a separate
`core.ingestion_run` row for workbook provenance. OpenMeteo rows link directly
to `core.PluginRun.RunId`. No current column links the two Ofgem audit records.
