# ADR-0018: Prefer standard Java but approve specialist libraries

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Avoiding all dependencies would reimplement complex formats incorrectly.

## Decision

Use JDK where strong; approve focused CLI/CSV/JSON/HTML/Excel/JDBC libraries.

## Consequences

Correctness improves; dependency versions/security require maintenance.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
