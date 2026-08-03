# Monitoring and Diagnostics

**Document ID:** OPS-MONITOR-001  
**Version:** 2.0  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 3 August 2026

---

| Signal | Source | Investigate when |
|---|---|---|
| Registry/status | `core.plugin_registry`, `--list-plugins` | unexpected absence/status/change |
| Configuration freshness | property-table `updated_at` | unexpected registration/update |
| Final application status | final JUL message | not `Successful` |
| Plugin status | summary and `core.PluginRun` | failure or stale `RUNNING` |
| Row metrics | logs/audit/provider tables | unexpected zero/spike/mismatch |
| Pool state | pool snapshot/SQL Server | sustained exhaustion or waits |
| Source identity | URI/name/size/SHA-256 | unexplained change |
| Octopus archive | ledger and directories | committed file remains unarchived |

Dry runs have log run UUIDs but no generic audit row. They still require SQL
registry/configuration reads and are useful only for failures before persistence.
All three current plugins support dry run.

Diagnostic sequence: locate first warning/error, identify pre/post-persistence
boundary, inspect status/metrics, check SQL/grants/locks/pool, verify source
shape/scope, reconcile hashes/archive, then reproduce safely.
