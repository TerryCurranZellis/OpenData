# Ingestion Audit Reference

**Document ID:** REF-AUDIT-001  
**Version:** 3.0.0  
**Status:** Version 3.0.0 implementation reference  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 24

---

## Generic plugin-run audit

| Status | Terminal | Meaning |
|---|---|---|
| `RUNNING` | no | coordinator task started |
| `SUCCESS` | yes | write-mode plugin completed |
| `DRY_RUN` | yes | schema-supported value; current dry runs create no row |
| `FAILED` | yes | plugin failed or successful work could not complete audit |
| `CANCELLED` | yes | execution was interrupted |

`core.PluginRun` stores run UUID, plugin id, timestamps, thread, host, row metrics
and an error message truncated to 4,000 characters.

Dry runs use `NoOpPluginRunAudit`, so a dry-run UUID appears only in logs.

## Audit completion edge case

If plugin work succeeds but the terminal audit update fails, the coordinator
changes the in-memory/logged result to `FAILED`. Business rows may already have
committed. Investigate the domain tables before retrying.

## Plugin-specific provenance

- Ofgem also uses `core.ingestion_run`, `core.source_file` and
  `core.ingestion_error`; there is no direct key to `core.PluginRun`.
- OpenMeteo business rows store `LastRunId` linked to the generic UUID.
- Octopus business rows and `octopus.statement_file` store `last_run_id` linked
  to the generic UUID.

## Operational rules

Do not manually change failed, cancelled or stale-running rows to success. Keep
them as evidence and create a new run after diagnosis. Reconcile audit status,
metrics, source/statement identity and business rows together.
