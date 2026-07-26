# ADR-0031: Coordinate concurrent loads in SQL Server transactions

**Status:** Superseded by [ADR-0039](ADR-0039-database-concurrency-owned-by-transactions.md)  
**Date:** 24 July 2026

## Context

A shared Apache DBCP pool permits concurrent connections. Java synchronization would coordinate only one JVM and would serialize unrelated plugins. Duplicate or overlapping loads can also originate from separate processes.

## Decision

Each repository owns one pooled connection and one transaction. Logical uniqueness is enforced with database keys. Update/insert decisions use `UPDLOCK, HOLDLOCK`. OpenMeteo obtains a transaction-owned `sp_getapplock` scoped to its stable location key. The load stages source rows, updates changed rows, and inserts missing rows without `MERGE`.

## Consequences

- No JDBC connection is shared between threads.
- Independent plugins and different OpenMeteo locations can run concurrently.
- The same location is serialized across JVMs while its transaction is active.
- Commit or rollback releases the application lock automatically.
- Transactions must remain short enough to avoid unnecessary contention.
- Repositories must remove connection-scoped temporary objects and restore session settings before returning pooled sessions.

## Alternatives

- `synchronized` around repositories: rejected as process-local and overly broad.
- One global SQL application lock: rejected because it blocks unrelated data.
- SQL Server `MERGE`: rejected for this path in favour of explicit update and insert statements.
