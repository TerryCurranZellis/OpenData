# ADR-0004: Layer bootstrap, plugin and runtime configuration

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Startup, reusable plugin settings and one-run overrides have different lifecycles.

## Decision

Separate bootstrap properties and plugin properties, then apply invocation overrides and construct immutable ApplicationConfig.

## Consequences

Configuration is explicit; earlier flat loaders are transitional.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
