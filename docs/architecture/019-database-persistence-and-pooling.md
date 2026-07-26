# Database Persistence and Connection Pooling

**Document ID:** ARCH-019  
**Version:** 1.1  
**Status:** Implemented  
**Baseline date:** 26 July 2026

---

## Purpose

The database layer supports the current command-line application and future
parallel plugin work without opening a new physical SQL Server connection for
every statement.

## Components

| Component | Responsibility |
|---|---|
| `DatabasePoolConfiguration` | Runtime SQL Server and pool settings |
| `SQLServerResource` | Process singleton backed by DBCP `GenericObjectPool` and `PoolingDriver` |
| `DatabaseResourceManager` | Connection/pool facade supplied in plugin execution context |
| `DatabasePoolSnapshot` | Reports active, idle and configured capacity |
| repository implementations | Own prepared SQL and transaction boundaries |

## Pool defaults

| Setting | Default | Purpose |
|---|---:|---|
| minimum idle | 1 | Keep one reusable connection available |
| maximum idle | 8 | Bound retained idle sessions |
| maximum total | 8 | Bound concurrent borrowed connections |
| maximum wait | 30 seconds | Fail rather than wait indefinitely |
| validation query | `SELECT 1` | Verify a borrowed SQL Server connection |

These are conservative application defaults, not universal production values.
Pool sizing must account for plugin concurrency, SQL Server capacity and the
number of application processes.

## Lifecycle

1. configuration is resolved and validated;
2. `SQLServerResource.initialise` creates or returns the process singleton;
3. repositories borrow logical connections;
4. try-with-resources returns each connection to the pool;
5. application shutdown closes the resource, DBCP pool and singleton once;
6. shutdown records the final run status and duration before resources disappear.

`DatabasePoolConfig` and `DatabaseConnectionManager` remain from the earlier
persistence layer and are used by older repository classes/tests, not by the
current application runtime. Consolidating those parallel abstractions is an
open cleanup task.

## Failure behaviour

- invalid pool values fail during configuration construction;
- exhaustion fails after `database.pool.max-wait-seconds` rather than blocking
  forever;
- validation failure prevents a bad connection being handed to a repository;
- repository SQL exceptions are translated at the service boundary;
- closing an individual borrowed connection must not close the pool;
- closing the pool makes later borrows fail clearly.

::: {.landscape}
![Database persistence components](../diagrams/generated/database-persistence-components.svg){width=22.5cm}
:::
