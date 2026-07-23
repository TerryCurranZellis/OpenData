# ADR-0021: Use Apache Commons CSV

**Status:** Accepted  
**Date:** 23 July 2026

## Context

String.split cannot safely parse general CSV.

## Decision

Use Commons CSV with immutable parser options.

## Consequences

Quoted delimiters, multiline values and header policies are handled correctly.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
