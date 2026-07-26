# ADR-0042: Keep dry runs free of persistent side effects

- **Status:** Accepted
- **Date:** 26 July 2026
- **Decision owners:** OpenData maintainers

## Context

Operators need to verify external access, discovery, download, parsing and
validation before supplying database credentials or changing persistent state.
A dry run that creates audit rows, archives files or partially writes plugin
tables is difficult to reason about and unsafe for first-time checks.

## Decision

`--dry-run` may perform outbound requests and write replaceable working files,
but it must not initialise the database pool, create audit rows, archive source
files or write domain tables. The application supplies a no-op audit and an
unavailable database resource so accidental database access fails.

## Consequences

### Positive

- dry runs need no database password;
- operators can validate external formats without changing the database;
- persistence code is excluded from the verification path;
- accidental database use is detected.

### Negative or limiting

- dry runs do not verify SQL, permissions, transactions or audit code;
- downloaded working files may still be replaced;
- no durable audit record proves that a dry run occurred.

## Alternatives considered

- Persisting a `DRY_RUN` audit row was rejected for the current implementation
  because it would require database access.
- Rolling back a full write transaction was rejected because it still exercises
  locks, permissions and side effects outside the transaction.

## Implementation notes

Implemented by `OpenDataApplication`, `NoOpPluginRunAudit`,
`UnavailableDatabaseResourceManager`, `OfgemPlugin` and `OpenMeteoPlugin`.
