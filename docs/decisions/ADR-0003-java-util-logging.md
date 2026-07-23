# ADR-0003: Use java.util.logging

**Status:** Accepted  
**Date:** 23 July 2026

## Context

The project prefers the JDK logging framework.

## Decision

Use JUL throughout application and plugin code; do not introduce another application logging API.

## Consequences

The dependency footprint is small; JUL conventions and third-party bridges must be managed.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
