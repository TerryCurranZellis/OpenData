# ADR-0010: Use constructor injection without a DI framework

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Dependencies must be testable without a heavy container.

## Decision

Inject through constructors and compose in the application layer.

## Consequences

Dependencies stay explicit; composition remains manual.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
