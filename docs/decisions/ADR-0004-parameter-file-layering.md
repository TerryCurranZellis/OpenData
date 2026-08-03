# ADR-0004: Layer bootstrap, plugin and runtime configuration

**Status:** Accepted for bootstrap and plugin separation; one-run override-file role superseded by ADR-0047 and ADR-0048  
**Date:** 23 July 2026

## Context

Startup and reusable plugin settings have different lifecycles. The original design also included one-run override files; the later persistent registration model removes that command-line role.

## Decision

Separate bootstrap properties from plugin properties and construct immutable configuration objects. Registered application/plugin values are read from SQL Server after bootstrap. External files are complete registration sources only, not invocation overrides.

## Consequences

Configuration remains explicit. ADR-0047 and ADR-0048 supersede invocation-file layering and define database-backed registration plus the persistent plugin registry.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
