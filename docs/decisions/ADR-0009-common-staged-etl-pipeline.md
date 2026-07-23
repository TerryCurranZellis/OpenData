# ADR-0009: Use a common staged ETL pipeline

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Download, archive, parse, validate, transform and load recur.

## Decision

Implement ordered reusable stages configured/extended by plugins.

## Consequences

Cross-cutting behaviour is centralised; context/step APIs still need completion.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
