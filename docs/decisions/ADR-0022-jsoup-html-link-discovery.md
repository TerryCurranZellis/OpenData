# ADR-0022: Use JSoup for static HTML discovery

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Publishers often expose changing file URLs through stable pages.

## Decision

Use CSS selectors and configured href/text regex; resolve relative links.

## Consequences

Static discovery is testable; publisher markup can require updates.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
