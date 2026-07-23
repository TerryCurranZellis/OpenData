# Architecture Decision Record Register

Statuses reflect the current codebase and documented future direction as of 22 July 2026.

| ADR | Decision | Status | Implementation state |
|---|---|---|---|
| 0001 | Plugin architecture | Accepted | Partially implemented; runtime registry pending |
| 0002 | Apache Commons CLI | Accepted | Implemented |
| 0003 | `java.util.logging` | Accepted | Implemented |
| 0004 | Parameter-file layering | Accepted | Implemented in configuration foundation |
| 0005 | SQL Server persistence | Accepted | Foundation implemented |
| 0006 | Package-info documentation | Accepted | Implemented |
| 0007 | Modular monolith | Accepted | Implemented |
| 0008 | Java 17 records and immutable models | Accepted | Implemented |
| 0009 | Common staged ETL pipeline | Accepted | Contracts present; orchestration pending |
| 0010 | Constructor injection without DI framework | Accepted | Implemented |
| 0011 | JDK HTTP client downloads | Accepted | Implemented |
| 0012 | Plugin properties before database configuration | Accepted | Implemented; database settings shelved |
| 0013 | Plugin manifest and registry | Accepted | Planned/partial |
| 0014 | Framework metadata and plugin business tables | Accepted | Deferred |
| 0015 | Database abstraction, SQL Server first | Accepted | Implemented for SQL Server foundation |
| 0016 | Exception hierarchy and boundary translation | Accepted | Implemented |
| 0017 | Documentation as code | Accepted | Implemented |
| 0018 | Minimal dependencies, standard Java first | Accepted | Implemented with justified adapters |
| 0019 | Ofgem reference plugin | Accepted | In progress |
| 0020 | Internal scheduling deferred | Deferred | Use external scheduling |
| 0021 | Configuration resolution and validation | Accepted | Implemented |
| 0022 | CLI control commands and exit codes | Accepted | Implemented |
| 0023 | CSV and JSON parser adapters | Accepted | Implemented |
| 0024 | Use IMAP as a reusable email attachment source | Pending | Future work |
| 0025 | Introduce the Octopus email bill plugin | Pending | Existing parser available; integration deferred |
| 0026 | Persist Octopus gas, electricity, and adjustment records as one transaction | Pending | Future work |
| 0027 | Make email attachment processing idempotent | Pending | Future work |
## Status vocabulary

- **Accepted** — the decision governs current development.
- **Proposed** — awaiting approval.
- **Deferred** — intentionally postponed.
- **Shelved** — retained for possible future work but not currently planned.
- **Superseded** — replaced by another ADR.
- **Rejected** — considered and not adopted.

## Current review notes

- ADR-0013 should not be read as proof that dynamic discovery is complete.
- ADR-0014 remains a target-state decision.
- ADR-0019 is the selected reference implementation but is not yet wired end to end from `Main`.
- ADR-0020 remains deferred.
