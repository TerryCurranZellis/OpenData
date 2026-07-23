# ADR-0001: Use a plugin architecture

**Status:** Accepted  
**Date:** 23 July 2026

## Context

The framework must support unrelated datasets without embedding each source in the entry point.

## Decision

Represent dataset families as plugins that provide identity, configuration validation and source-specific transformation while reusing framework infrastructure.

## Consequences

New datasets can be added independently; a stable contract and registry are required.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
