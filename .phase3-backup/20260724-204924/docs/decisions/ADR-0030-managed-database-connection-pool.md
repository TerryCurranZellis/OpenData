# ADR-0030: Use a managed Apache DBCP connection pool

- **Status:** Accepted
- **Date:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

Future plugin execution may perform concurrent database work and may require
multiple independently configured resources. Registering a named pooling driver
globally makes lifecycle, tests and multiple pools harder to control.

## Decision

Use Apache Commons DBCP `BasicDataSource`, owned by `SQLServerResource` and
exposed through `DatabaseResourceManager`. Configuration is external. The pool
has an explicit closeable lifecycle and is not a process-global singleton.

## Consequences

### Positive

- physical connections are reused;
- borrowing is bounded and observable;
- separate pools can be created later;
- shutdown and tests control the resource lifecycle.

### Negative or limiting

- the application must close the pool;
- pool sizing becomes an operational responsibility;
- multiple application processes multiply total possible connections.

## Alternatives considered

A raw connection per operation was rejected for inefficient connection churn.
A globally registered `PoolingDriver` singleton was rejected because it creates
name clashes and hidden process state.

## Implementation notes

Implemented by `SQLServerResource`, `DatabasePoolConfig`,
`DatabasePoolSnapshot` and `DatabaseConnectionManager`.
