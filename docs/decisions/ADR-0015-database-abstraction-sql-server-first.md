# ADR-0015: Abstract database contracts, implement SQL Server first

**Status:** Accepted  
**Date:** 23 July 2026

## Context

SQL Server is first but SQL is not universally portable.

## Decision

Define operation-oriented repository contracts and named SQL Server implementations.

## Consequences

Future databases are added without conditionals throughout plugins.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
