# ADR-0038: Use bounded parallel plugin execution

- **Status:** Accepted
- **Date:** 24 July 2026
- **Decision owners:** OpenData maintainers

## Context

The CLI supports several selected plugins and `--plugin all`. Plugins are mainly
network and database workloads, but unrestricted thread creation could exhaust
memory, remote-service capacity or the SQL Server connection pool. Java 17 is
the minimum version.

## Decision

Create one independent task per selected plugin and execute the tasks on a
fixed-size `ExecutorService`. The worker count is configurable, limited to
1–64, and capped by the number of selected plugins. Create a fresh plugin
instance and UUID for each task. Isolate failures and aggregate ordered results
after all tasks finish.

## Consequences

### Positive

- independent plugins can overlap downloads and database transactions;
- resource use is bounded and compatible with Java 17;
- one plugin failure does not prevent another task from completing;
- result order remains deterministic.

### Negative or limiting

- queued plugins wait when selection size exceeds worker count;
- plugins must be thread-confined and interruption-aware;
- executor size and database-pool size require coordinated tuning.

## Alternatives considered

- Sequential execution was rejected because unrelated plugins would block one
  another.
- A cached or unbounded executor was rejected because it provides no resource
  ceiling.
- Virtual threads were deferred because they require a Java baseline above 17.

## Implementation notes

Implemented by `PluginExecutionCoordinator`, `PluginThreadFactory`,
`PluginSelectionResolver` and `execution.max-parallel-plugins`.

This is the canonical uniquely numbered record for the decision originally
stored as `ADR-0030-bounded-parallel-plugin-execution.md`.
