# Parallel plugin runtime

**Status:** Implemented  
**Date:** 24 July 2026  
**Minimum Java:** 17

## Purpose

The runtime accepts one or more plugin ids. Each selected plugin becomes one independent `Callable<PluginRunResult>` and receives a fresh plugin instance, a unique run id, its own immutable definition and access to the shared connection pool.

## Selection rules

- `--plugin openmeteo` selects one plugin.
- Repeated options and comma-separated ids select several installed executable plugins. The uploaded baseline currently contains only one executable plugin class (`openmeteo`); the Ofgem descriptor points to a class that is not yet present.
- `--plugin all` selects every enabled descriptor in the classpath registry.
- Duplicate ids are rejected before execution.
- `all` cannot be combined with named ids.
- Disabled plugins are omitted from `all` and rejected when explicitly named.

## Thread model

`PluginExecutionCoordinator` creates a fixed-size executor. The effective worker count is the lower of selected plugins and configured/requested parallelism. There is one task per plugin, but not an unbounded permanent thread per plugin. This protects the JVM, database pool and remote services as the registry grows.

A failure in one task does not cancel unrelated plugins. Results are reported in selection order. The invocation succeeds only when all tasks have status `SUCCESS` or `DRY_RUN`.

## Isolation rules

1. Plugin implementations must be stateless or confined to one task.
2. `ReflectionPluginFactory` creates a new instance for each execution.
3. Plugins must not cache JDBC `Connection`, `Statement` or `ResultSet` objects in static or instance fields.
4. Each repository method owns one transaction and closes its connection with try-with-resources.
5. Shared immutable objects are permitted; mutable cross-plugin state is not.
6. Interruption is restored with `Thread.currentThread().interrupt()`.

## Why a bounded platform-thread pool

Java 17 is the project baseline, so virtual threads are not available. A fixed pool provides predictable concurrency and integrates with JDBC drivers and Apache DBCP without changing the minimum Java version.

## Shutdown

The coordinator calls `shutdown()`, waits for `execution.shutdown-timeout-seconds`, and then uses `shutdownNow()` only if workers do not finish. Plugin code must honour interruption during blocking or long-running work.

## Extension contract

Every executable implementation must implement:

```java
public interface OpenDataPlugin {
    PluginMetrics execute(PluginExecutionContext context) throws Exception;
}
```

The implementation should expose either a public constructor accepting `PluginDefinition` or a public no-argument constructor.

## References

- Java SE 17 `Executors`: https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Executors.html
