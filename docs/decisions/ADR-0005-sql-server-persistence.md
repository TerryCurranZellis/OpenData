# ADR-0005: Use SQL Server as the first persistence target

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Initial datasets are loaded into SQL Server.

## Decision

Hide JDBC behind connection/repository classes and keep source SQL out of plugins.

## Consequences

SQL Server features may be used deliberately; future databases need implementations.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
