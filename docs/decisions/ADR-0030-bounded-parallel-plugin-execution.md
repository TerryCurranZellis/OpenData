# ADR-0030: Use bounded parallel plugin execution

**Status:** Superseded by [ADR-0038](ADR-0038-bounded-parallel-plugin-execution.md)  
**Date:** 24 July 2026

## Context

The framework must support several selected plugins and `--plugin all`. Plugins are mainly network and database workloads, but unrestricted thread creation could exhaust memory, remote services or the SQL Server connection pool. Java 17 remains the minimum version.

## Decision

Create one independent task per selected plugin and execute tasks on a fixed-size `ExecutorService`. The worker count is configurable and capped by the number of selected plugins. A fresh plugin instance and unique run id are created for each task. Failures are isolated and aggregated after all tasks finish.

## Consequences

- Independent plugins can overlap downloads and database transactions.
- Concurrency is predictable and compatible with Java 17.
- More selected plugins than workers wait in the executor queue.
- Plugin implementations must be thread-confined and interruption-aware.
- `--plugin all` means all enabled plugins are submitted, not that an unlimited thread is created for each.

## Alternatives

- Sequential execution: rejected because unrelated plugins would block each other.
- Cached/unbounded executor: rejected because it provides no resource ceiling.
- Virtual threads: deferred because they require raising the Java baseline above 17.
