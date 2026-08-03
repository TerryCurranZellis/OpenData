# Operations Runbook

**Document ID:** OPS-RUNBOOK-001  
**Version:** 2.0  
**Status:** Version 2.0.0 pre-production baseline  
**Baseline date:** 3 August 2026

---

![OpenData operational lifecycle](../diagrams/generated/operational-lifecycle.svg)

## Installation or change preflight

1. record the application, schema and documentation versions;
2. confirm the repository root is the launcher working directory;
3. verify the SQL scripts have been applied in order;
4. replace and protect the bootstrap credential and certificate pair;
5. verify log, input, working and archive directory permissions and capacity;
6. back up SQL Server and protected configuration; and
7. confirm no conflicting dataset maintenance is in progress.

## Registration

Run registration only after the configuration tables and grants exist:

```text
opendata --register --file C:\OpenData\bootstrap.properties
```

Verify both `core.application_property`/`core.plugin_property` and the rewritten
`src/main/resources/config/application.properties`. Registration is not atomic
across SQL Server and the local file.

## Safe acceptance

1. run `--list-plugins`;
2. dry-run Ofgem;
3. dry-run OpenMeteo with a small explicit date range;
4. run one controlled Ofgem write and reconcile audit/domain rows;
5. run one controlled OpenMeteo write and verify idempotent replay; and
6. run Octopus only against disposable PDF copies, an isolated database and an
   explicit archive directory.

Do not dry-run Octopus or `all` in the current baseline.

## Routine write run

Before each run:

- confirm SQL Server and external source availability;
- verify no stale `RUNNING` rows indicate an unresolved prior interruption;
- verify expected file/date scope; and
- start with parallelism appropriate to the pool and workload.

After each run, retain the final application line, every plugin summary, run
UUIDs, row metrics and relevant source/statement hashes.

## Stop and escalation conditions

Stop further writes after:

- a transaction or permission test fails;
- publisher/API/PDF structure changes unexpectedly;
- source hashes or row counts differ without explanation;
- the pool is exhausted or SQL blocking persists;
- a credential or statement content appears in logs;
- Octopus reports an archive warning; or
- a non-terminal audit row remains without an active process.

## Scheduler warning

The Java process currently does not propagate `ExecutionStatus.statusCode()` to
the operating system. A zero shell code does not prove success. Until corrected,
use a tested wrapper that inspects the final status and plugin-summary logs, or
run interactively under operator control.
