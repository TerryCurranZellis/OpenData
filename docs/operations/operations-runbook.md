# Operations Runbook

**Document ID:** OPS-RUNBOOK-001  
**Version:** 1.0  
**Status:** Pre-production baseline  
**Baseline date:** 26 July 2026

---

## Pre-run

1. confirm Java, application and schema versions;
2. verify the override file exists and is access-restricted;
3. verify SQL Server reachability for a write run;
4. confirm working, archive and log directories have space;
5. check that no conflicting same-dataset maintenance is in progress.

## Safe first run

Run plugin listing, then each plugin in dry-run mode. A dry run performs network
and parsing work but does not create audit rows, archive files or initialise the
database pool.

## Write run

Start with one plugin. Confirm its terminal `core.PluginRun` row and business
row counts before enabling parallel runs. For Ofgem, also confirm the separate
`core.ingestion_run` and source-file provenance row.

## Multi-plugin run

Use scoped override keys and choose parallelism no greater than the available
database connections or operationally safe remote request count. One plugin
failure does not cancel another; inspect every summary row.

## Stop and escalation conditions

Stop further write runs after:

- a non-terminal audit row remains without an active process;
- a rollback or permission test fails;
- publisher layout validation fails;
- source/file hashes or row counts are unexpectedly different;
- the pool is exhausted or SQL blocking persists;
- credentials appear in logs.

## Post-run

Retain run identifiers, final status, metrics and the archived Ofgem source when
enabled. Do not infer success from the shell exit code until exit-code mapping is
implemented.
