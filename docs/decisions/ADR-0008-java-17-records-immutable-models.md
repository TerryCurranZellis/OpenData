# ADR-0008: Support Java 17 minimum and use records

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Records suit immutable configuration/results and a stable minimum is needed.

## Decision

Compile with --release 17 and use records for immutable values without mutable identity.

## Consequences

Later-JDK APIs are prohibited unless a new ADR changes the baseline.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
