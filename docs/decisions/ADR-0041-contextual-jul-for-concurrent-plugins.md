# ADR-0041: Add task context to java.util.logging

- **Status:** Accepted
- **Date:** 24 July 2026
- **Decision owners:** OpenData maintainers

## Context

Concurrent plugins interleave messages in shared console and file output. The
project standard requires `java.util.logging`.

## Decision

Use one shared JUL console handler and one rotating file handler. Store plugin id
and run UUID in a `ThreadLocal` scope opened by the coordinator and removed when
the task ends. Include the worker thread name in every formatted record.

## Consequences

### Positive

- interleaved output remains attributable to one plugin execution;
- handlers, rotation and retention remain centralised;
- log and `core.PluginRun` identifiers can be correlated.

### Negative or limiting

- context cleanup is essential because executor threads are reused;
- child threads do not automatically inherit the context;
- sensitive values still require explicit redaction.

## Alternatives considered

- Per-plugin log files were rejected because handler lifecycle and rotation
  become complex.
- A third-party logging facade was rejected by the project logging policy.
- Thread name alone was rejected because one worker executes many tasks.

## Implementation notes

Implemented by `PluginLogContext`, `ContextualLogFormatter`,
`LoggingManager` and `PluginExecutionCoordinator`.

This is the canonical uniquely numbered record for the decision originally
stored as `ADR-0033-contextual-jul-for-concurrent-plugins.md`.
