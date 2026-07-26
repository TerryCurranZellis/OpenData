# ADR-0033: Add thread and plugin context to java.util.logging

**Status:** Superseded by [ADR-0041](ADR-0041-contextual-jul-for-concurrent-plugins.md)  
**Date:** 24 July 2026

## Context

Concurrent plugins interleave messages in console and file output. The project requires `java.util.logging` rather than SLF4J or Logback.

## Decision

Keep one shared JUL console handler and one rotating file handler. Add a `ThreadLocal` context containing plugin id and run id, opened by the coordinator and removed when the task ends. The formatter also includes the worker thread name.

## Consequences

- Interleaved output remains attributable to one plugin task.
- No per-plugin handler or log file is required.
- Context cleanup is essential because executor threads are reused.
- Sensitive configuration values are not included in context or startup logs.

## Alternatives

- Separate logger/file per plugin: rejected because handler lifecycle and rotation become complex.
- Third-party logging facade: rejected by project logging policy.
- Thread name only: rejected because a worker can execute different tasks over its lifetime.
