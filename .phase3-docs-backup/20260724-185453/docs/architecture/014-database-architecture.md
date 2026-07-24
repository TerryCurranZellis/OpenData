# Database Architecture

**Document ID:** ARCH-014  
**Version:** 1.0  
**Status:** Partial  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


SQL Server is the first target. `DatabaseConnectionManager`, repository contracts,
`SqlServerRepository` and `LoadService` isolate JDBC and transaction behaviour.

Framework metadata (runs, artefacts, checksums, validation summaries, counts and
timings) is separated from plugin business tables.

A logical load is transactional. Batch inserts must not leave an apparently
successful partial run.

Database-neutral contracts do not imply portable SQL. Additional databases use
new implementations.

A future normalised plugin-definition database returning JSON is shelved.

See [database-architecture.puml](../diagrams/database-architecture.puml).
