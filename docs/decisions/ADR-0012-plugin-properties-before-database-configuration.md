# ADR-0012: Use properties-based plugin definitions in Phase 1

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Database administration is premature before the model stabilises.

## Decision

Use one properties file per plugin, parsed into storage-neutral records.

## Consequences

Definitions are reviewable in Git; central management is postponed.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
