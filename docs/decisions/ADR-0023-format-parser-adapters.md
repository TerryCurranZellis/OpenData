# ADR-0023: Provide common CSV and JSON parser adapters

- Status: Accepted and implemented
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

Public datasets commonly use CSV or JSON. Reimplementing low-level format parsing in every dataset plugin would duplicate error handling and library configuration.

## Decision

Define a common `DataParser` abstraction and provide framework adapters:

- `CsvDataParser` using Apache Commons CSV;
- `JsonDataParser` using Jackson Databind.

Dataset-specific interpretation and transformation remain above the format parser.

## Consequences

### Positive

- Common format handling is reusable.
- Plugins focus on dataset semantics.
- Parser libraries are isolated behind framework contracts.
- Parsing can be tested independently.

### Negative or limiting

- Generic parsed structures may require a later mapping step.
- Library upgrades can alter parsing behaviour.
- Very large datasets may later require streaming-specific APIs.

## Alternatives considered

### JDK-only custom parsers

Rejected because robust CSV and JSON parsing is complex and unnecessary.

### Dataset-specific parser classes only

Rejected because it would repeat common format concerns.

## Implementation notes

Streaming or schema-aware parser variants may be added later without changing plugin-facing orchestration, provided the `DataParser` contract remains stable.
