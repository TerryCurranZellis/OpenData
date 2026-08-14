# ADR-0056: Stream scoped JUL execution output into JavaFX without replacing application logging

**Status:** Accepted
**Date:** 2026-08-14
**Decision owners:** OpenData maintainers
**Version:** 3.1.0

---

## Context

Batch 6 connects the JavaFX **Execute** and **Dry-run** commands to the existing
plugin execution framework. Plugin work can run concurrently, performs network
and database I/O, and already reports progress through `java.util.logging`
(JUL). The GUI must display that output live while preserving the existing
console/file log and keeping the JavaFX application thread responsive.

`LoggingManager.configure(...)` replaces handlers on the JUL root logger when
runtime logging settings are applied. A live GUI handler attached to the root
logger could therefore be removed in the middle of a run. All OpenData loggers,
however, are descendants of `com.towermarsh.opendata`.

Normal execution and dry-run also have different database guarantees. Normal
execution writes provider data and generic run-audit rows. Dry-run must continue
to use an unavailable execution database and no-op audit implementation.

## Decision

1. Snapshot checked plugin identifiers before confirmation and before any
   background work starts.
2. Run GUI Execute and Dry-run through `PluginExecutionGateway` on a JavaFX
   `Task`; the controller does not perform JDBC, configuration or ETL work.
3. Reuse `PluginSelectionResolver`, `PropertiesPluginDefinitionLoader`,
   `PluginExecutionCoordinator`, `ReflectionPluginFactory` and the established
   runtime configuration types rather than defining a second execution model.
4. Preserve the CLI two-phase SQL Server lifecycle: open the bootstrap database
   to resolve registry/configuration, close it, then open the runtime execution
   pool when a write run requires one.
5. Use the configured `execution.max-parallel-plugins` value for GUI execution.
6. Keep dry-run side-effect isolation by using
   `UnavailableDatabaseResourceManager` and `NoOpPluginRunAudit`.
7. Attach a temporary `JavaFxLogHandler` to the
   `com.towermarsh.opendata` application logger, not to the root logger. This
   handler therefore survives `LoggingManager.configure(...)` while normal
   root console/file handlers continue to receive the same records.
8. Batch records from plugin threads into a thread-safe queue and marshal the
   batches to JavaFX with `Platform.runLater()` rather than issuing one UI
   callback for every JUL record.
9. Display execution output in a modal, scrollable JavaFX window. The window
   close decoration is blocked and the centred **Close** button is disabled
   until the background task has completed or failed.
10. Refresh the main plugin table after processing so normal execution audit
    status becomes visible. Dry-run does not create generic audit rows, so its
    last-run columns remain unchanged.

## Consequences

### Positive

- Execute and Dry-run use the same plugin execution primitives and side-effect
  rules as the CLI.
- Concurrent plugin logs can be viewed as the run progresses without blocking
  JavaFX.
- Existing rotating file and console logging remains authoritative and is not
  replaced by the execution window.
- Runtime logging reconfiguration cannot accidentally detach the live GUI
  handler because that handler is scoped to the application logger.
- The user cannot dismiss the execution window before processing ends.

### Trade-offs

- The GUI currently uses configured parallelism and does not expose a separate
  per-run parallelism control.
- Batch 6 does not provide an execution Cancel command; the Close button is an
  after-completion review control, not a cancellation mechanism.
- Reconfiguring runtime logging during GUI execution may switch the active log
  directory, matching CLI behaviour.

## Alternatives considered

### Read the rotating log file repeatedly

Rejected because file polling adds latency, couples the live window to rotation
behaviour and cannot guarantee that buffered records have already reached disk.

### Attach the live handler to the JUL root logger

Rejected because `LoggingManager.configure(...)` intentionally removes and
recreates root handlers, which could remove the GUI handler during execution.

### Redirect `System.out` and `System.err` into JavaFX

Rejected because OpenData has an established JUL logging contract and plugin
context (plugin id and run id) is already carried by the JUL formatter.

---
