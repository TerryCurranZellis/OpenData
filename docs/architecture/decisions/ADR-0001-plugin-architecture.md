# ADR-0001: Use a dataset plugin architecture

- Status: Accepted
- Date: 2026-07-21

## Context

OpenData will support more than one public dataset. Dataset formats, validation rules, retrieval methods, and database tables differ, while command-line processing, logging, configuration, file handling, and run reporting are common.

## Decision

Use a plugin architecture. The framework exposes a small `DatasetPlugin` contract. Concrete plugins, beginning with `ofgem`, implement dataset-specific acquisition, parsing, validation, transformation, and persistence.

## Consequences

### Positive

- New datasets can be added without placing dataset conditions in the core.
- Common infrastructure is reusable.
- Plugin contract tests can enforce consistent behaviour.
- Dataset documentation can remain grouped with its implementation.

### Negative or limiting

- Interfaces and execution context require careful versioning.
- Some apparent duplication between plugins may remain deliberately isolated.
- Independently deployed plugin JARs are not part of the initial design.

## Alternatives considered

### One application per dataset

Rejected because shared CLI, configuration, logging, and persistence support would be duplicated.

### Large central ETL pipeline

Rejected because source-specific behaviour would accumulate in central conditional logic.
