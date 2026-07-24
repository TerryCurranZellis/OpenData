# Database Persistence and Connection Pooling

**Document ID:** ARCH-019  
**Version:** 1.0  
**Status:** Implemented  
**Baseline date:** 24 July 2026

---

## Purpose

The database layer supports the current command-line application and future
parallel plugin work without opening a new physical SQL Server connection for
every statement.

## Components

| Component | Responsibility |
|---|---|
| `DatabasePoolConfig` | Immutable validated pool limits and timeouts |
| `SQLServerResource` | Creates, configures and closes `BasicDataSource` |
| `DatabaseConnectionManager` | Stable facade supplied to repositories |
| `DatabasePoolSnapshot` | Reports active, idle and configured capacity |
| `DatabaseHealthCheck` | Executes a low-cost connectivity check |
| repository implementations | Own prepared SQL and transaction boundaries |

## Pool defaults

| Setting | Default | Purpose |
|---|---:|---|
| initial size | 1 | Establish one connection at startup/use |
| minimum idle | 1 | Keep one reusable connection available |
| maximum idle | 4 | Bound retained idle sessions |
| maximum total | 12 | Bound concurrent borrowed connections |
| maximum wait | 30 seconds | Fail rather than wait indefinitely |
| minimum evictable idle | 5 minutes | Reclaim long-idle physical connections |
| validation query | `SELECT 1` | Verify a borrowed SQL Server connection |
| validation timeout | 5 seconds | Bound health validation |

These are conservative application defaults, not universal production values.
Pool sizing must account for plugin concurrency, SQL Server capacity and the
number of application processes.

## Lifecycle

1. configuration is resolved and validated;
2. one `DatabaseConnectionManager` is created for the process;
3. repositories borrow logical connections;
4. try-with-resources returns each connection to the pool;
5. application shutdown closes the manager and pool once;
6. shutdown records the final run status and duration before resources disappear.

The pool is intentionally not a global singleton. Tests and later deployments
can create separate pools for different databases or workloads.

## Failure behaviour

- invalid pool values fail during configuration construction;
- exhaustion fails after `max-wait-millis` rather than blocking forever;
- validation failure prevents a bad connection being handed to a repository;
- repository SQL exceptions are translated at the service boundary;
- closing an individual borrowed connection must not close the pool;
- closing the pool makes later borrows fail clearly.

See [database-persistence-components.puml](../diagrams/database-persistence-components.puml).
