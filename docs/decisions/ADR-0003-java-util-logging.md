# ADR-0003: Standardise on java.util.logging

- Status: Accepted
- Date: 2026-07-21

## Context

The project requires consistent logging and the user prefers the Java platform logging API.

## Decision

All OpenData source code will use `java.util.logging`. Required dependencies that use another framework must be configured, bridged, or have unnecessary implementations excluded.

## Consequences

- No project-level logging facade is required.
- Logging configuration uses `logging.properties` and JUL handlers.
- Care is required when integrating third-party libraries.
- Architecture tests should prevent accidental adoption of another logging API.
