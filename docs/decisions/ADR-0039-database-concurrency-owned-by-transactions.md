# ADR-0039: Coordinate database concurrency through transactions

- **Status:** Accepted
- **Date:** 24 July 2026
- **Decision owners:** OpenData maintainers

## Context

A shared DBCP pool permits concurrent connections. Java synchronisation would
coordinate only one JVM and could unnecessarily serialize unrelated datasets.
Conflicting loads may originate from separate processes.

## Decision

Each repository owns one borrowed connection and one transaction. Do not share a
JDBC connection between plugin tasks. Enforce logical uniqueness with database
keys and SQL Server locking. OpenMeteo uses a transaction-owned
`sp_getapplock` scoped to its stable location key and explicit update/insert
statements rather than `MERGE`.

## Consequences

### Positive

- independent plugins and different weather locations may proceed concurrently;
- conflicting same-location loads serialize across JVMs;
- commit or rollback releases transaction-owned locks;
- connection ownership is explicit.

### Negative or limiting

- long transactions can cause pool or lock contention;
- pooled sessions require temporary-table and `SET`-state cleanup;
- SQL Server-specific concurrency behaviour belongs in repository code.

## Alternatives considered

- `synchronized` repository methods were rejected as process-local and broad.
- One global application lock was rejected because it blocks unrelated work.
- SQL Server `MERGE` was rejected for this path in favour of explicit statements.

## Implementation notes

Implemented in the plugin-local OpenMeteo and Ofgem `load` repositories.

This is the canonical uniquely numbered record for the decision originally
stored as `ADR-0031-database-concurrency-owned-by-transactions.md`.
