# ADR-0018: Prefer standard Java APIs and a minimal dependency set

- Status: Accepted
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

The project is intended to remain understandable and maintainable as a command-line ETL framework. Every external library increases upgrade, security, logging-bridge, licensing, and compatibility obligations.

## Decision

Prefer Java 17 standard APIs when they adequately meet requirements. Add an external dependency only when it supplies material capability or reliability that would be costly to reproduce. Keep dependencies centrally managed through Maven and document their purpose.

## Consequences

### Positive

- Reduces security and upgrade surface.
- Keeps the architecture understandable.
- Avoids unnecessary framework lock-in.
- Supports predictable logging and packaging.

### Negative or limiting

- Some standard APIs require more application code.
- Specialised formats such as Excel and HTML still require libraries.
- A strict interpretation could lead to reinventing mature functionality.

## Alternatives considered

### Adopt a broad application framework

Rejected because the initial requirements do not justify the additional runtime and architectural coupling.

### Avoid all third-party libraries

Rejected because libraries such as Apache Commons CLI, Apache POI, Jsoup, and the SQL Server driver provide necessary capability.

## Implementation notes

Third-party libraries must not force application logging away from `java.util.logging`; use bridges or adapters where needed. Dependency versions and licences should be reviewed through Maven.
