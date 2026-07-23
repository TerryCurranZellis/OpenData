# ADR-0007: Use a modular monolith

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Independent deployment and distributed transactions are not required.

## Decision

Build one JVM application with explicit package boundaries.

## Consequences

Deployment is simple; boundaries rely on review and future architecture tests.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
