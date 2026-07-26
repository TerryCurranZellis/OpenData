# Monitoring and Diagnostics

**Document ID:** OPS-MONITOR-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

## Primary signals

| Signal | Source | Investigate when |
|---|---|---|
| Final application status | JUL final message | Not `SUCCESS` |
| Plugin terminal status | `core.PluginRun` and summary log | `FAILED`, `CANCELLED` or stale `RUNNING` |
| Domain ingestion status | `core.ingestion_run` for Ofgem | Stale `STARTED` or non-success |
| Row metrics | Plugin summary/audit tables | Unexpected zero, spike or mismatch |
| Pool usage/waits | Pool snapshot and SQL Server | Sustained exhaustion or long waits |
| Source identity | URI, size and SHA-256 | Unexpected publisher/file change |

## Correlation

Use the UUID printed as `[run=<uuid>]` to correlate concurrent log records with
`core.PluginRun`. The current Ofgem domain ingestion identity is separate, so
also correlate by plugin, time, source URI and workbook.

## First diagnostic checks

1. locate the first `SEVERE` entry for the affected run UUID;
2. confirm whether the failure occurred before or after persistence;
3. inspect the terminal audit row and plugin metrics;
4. check SQL Server connectivity, locks and permissions;
5. verify external HTTP status and publisher layout;
6. reproduce with a dry run when the suspected failure is before persistence.

Dry-run success does not prove SQL, transactions, audit completion or
least-privilege grants.
