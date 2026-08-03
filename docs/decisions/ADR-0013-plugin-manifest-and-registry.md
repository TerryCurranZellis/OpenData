# ADR-0013: Use an explicit classpath plugin index

**Status:** Accepted as the packaged registration catalogue; runtime-registry role superseded by ADR-0048  
**Date:** 23 July 2026

## Context

Hard-coded listing hid OpenMeteo and JAR directory scanning is unreliable.

## Decision

Use `config/plugins/index.properties` and a classpath catalogue that reads each packaged definition available for registration.

## Consequences

Adding a packaged plugin requires updating the index. ADR-0048 introduced `core.plugin_registry` as the authoritative installed/enabled registry; the classpath index remains the source catalogue used by `--register` when no external file is supplied.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
