# ADR-0016: Translate exceptions at boundaries

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Raw library exceptions leak implementation detail.

## Decision

Use focused framework exceptions, preserve causes and map to run status only at the entry boundary.

## Consequences

Diagnostics are consistent; catches must remain narrow.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
