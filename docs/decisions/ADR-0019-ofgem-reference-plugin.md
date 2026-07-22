# ADR-0019: Use Ofgem as the first reference plugin

- Status: Accepted
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

A concrete dataset is required to prove the plugin, configuration, download, parsing, validation, and SQL Server persistence contracts. The Ofgem energy price cap data is the first target agreed for Stage 1.

## Decision

Implement the Ofgem dataset as the first production-oriented plugin and use it as the reference implementation for framework contracts, examples, configuration files, SQL scripts, and integration tests.

## Consequences

### Positive

- Validates the architecture against a real public dataset.
- Provides a repeatable example for later plugins.
- Focuses Stage 1 scope on an agreed deliverable.
- Exposes missing framework abstractions early.

### Negative or limiting

- Framework design may initially reflect Ofgem needs too strongly.
- Changes in Ofgem publication formats may require parser maintenance.
- A single plugin cannot prove every future extension requirement.

## Alternatives considered

### Build several plugins in parallel

Rejected for Stage 1 because it would dilute effort before the shared framework is stable.

### Build only abstract framework code

Rejected because abstractions need validation against real data.

## Implementation notes

Ofgem-specific URLs, worksheet names, parsing rules, validation tolerances, and destination tables must remain inside the plugin and its configuration. Core packages must not reference Ofgem classes.
