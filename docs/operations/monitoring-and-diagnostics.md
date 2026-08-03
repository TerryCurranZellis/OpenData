# Monitoring and Diagnostics

**Document ID:** OPS-MONITOR-001  
**Version:** 2.0  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 3 August 2026

---

## Primary signals

| Signal | Source | Investigate when |
|---|---|---|
| Final application status | final JUL message | not `Successful` |
| Plugin terminal status | summary log and `core.PluginRun` | `FAILED`, `CANCELLED` or stale `RUNNING` |
| Ofgem ingestion status | `core.ingestion_run` | stale `STARTED` or non-success |
| Row metrics | summary/audit/domain tables | unexpected zero, spike or mismatch |
| Pool state | pool snapshot and SQL Server | sustained exhaustion or long waits |
| Source identity | URI/file name, size and SHA-256 | unexpected change |
| Octopus archive reconciliation | input/archive directories and `octopus.statement_file` | committed file remains unarchived |
| Configuration freshness | configuration tables `updated_at` | unexpected registration/update |

## Correlation

Use `[run=<uuid>]` to correlate plugin logs with `core.PluginRun`. Dry runs have a
UUID in logs but no persisted generic audit row. Ofgem has a separate numeric
ingestion id; correlate by time, source URI and plugin context.

## Diagnostic sequence

1. locate the first `SEVERE` or `WARNING` for the affected run;
2. determine whether failure occurred before or after persistence;
3. inspect plugin summary metrics and terminal audit state;
4. check SQL connectivity, permissions, locks and pool availability;
5. verify remote response, workbook/API/PDF structure and configured scope;
6. reconcile source hashes and archive movement; and
7. reproduce with a supported dry run only when the suspected failure is before
   persistence.

A successful Ofgem/OpenMeteo dry run does not prove SQL, transaction, grants or
audit completion. Octopus has no usable dry-run path in this baseline.

## Useful stale-run query

```sql
SELECT RunId, PluginId, Status, StartedAt, ThreadName, HostName
FROM core.PluginRun
WHERE Status = 'RUNNING'
  AND StartedAt < DATEADD(hour, -2, SYSUTCDATETIME())
ORDER BY StartedAt;
```

Do not update stale rows to success. Retain them as incident evidence.
