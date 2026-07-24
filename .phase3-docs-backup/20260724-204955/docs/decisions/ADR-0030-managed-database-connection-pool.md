# ADR-0030: Use a managed Apache DBCP connection pool

- **Status:** Accepted
- **Date:** 2026-07-24

## Context

The initial repository opened one physical JDBC connection for each repository
operation. Future plugin execution may use concurrent database work and may
need more than one independently configured database resource. An earlier
prototype registered a named `PoolingDriver` globally with `DriverManager`.

## Decision

Use Apache Commons DBCP `BasicDataSource`, owned by `SQLServerResource` and
exposed through `DatabaseResourceManager`. Keep an explicit closeable lifecycle
and configure URL, user and password externally through `ApplicationConfig`.
Do not hard-code the application password and do not make the pool a process-
global singleton.

## Consequences

- Connections are reused and concurrent borrowing is bounded.
- Closing a borrowed connection returns it to the pool.
- The application must close the pool during shutdown.
- Multiple pools can be introduced later without global driver-name clashes.
- Pool limits and validation settings are testable configuration values.
