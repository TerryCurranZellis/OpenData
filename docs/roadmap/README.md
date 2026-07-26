# OpenData Roadmap

**Document ID:** ROADMAP-001  
**Version:** 1.0  
**Status:** Current priorities  
**Baseline date:** 26 July 2026

---

## Current baseline

Ofgem and OpenMeteo execute through the common registry and bounded coordinator.
Dry runs, contextual logging, pooled SQL Server access and plugin metrics are
implemented.

## Priority order

| Priority | Outcome | Acceptance evidence |
|---|---|---|
| 1 | Unify plugin-run and ingestion provenance | One run identity links logs, source file and business rows |
| 2 | Remove packaged/legacy credentials | No password in tracked resources; external secret verified |
| 3 | Prove SQL Server operation | Clean/repeat install, both write runs, rollback and permissions |
| 4 | Produce executable package | Verified `java -jar`, dependencies, manifest version and exit codes |
| 5 | Harden documentation release | Technical/user builds and all PlantUML render successfully |
| 6 | Expand Ofgem coverage | Component values and historical backfill with fixtures/reconciliation |

## Deferred

Internal scheduling, database-backed plugin configuration, IMAP/Octopus work and
additional database engines remain deferred or shelved according to their ADRs.

## Release boundary

Do not label the application production-ready until priorities 1–5 are complete
or each remaining risk has a documented release waiver.
