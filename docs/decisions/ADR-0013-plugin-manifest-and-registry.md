# ADR-0013: Use an explicit classpath plugin index

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Hard-coded listing hid OpenMeteo and JAR directory scanning is unreliable.

## Decision

Use config/plugins/index.properties and a registry that reads each definition.

## Consequences

Adding a plugin requires updating the index; future database registry can replace it.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
