# ADR-0008: Use Java 17 records and immutable configuration models

- Status: Accepted
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

OpenData is being developed on Java 17. Configuration, command-line selections, plugin metadata, dataset descriptors, and processing results are primarily data carriers. Conventional mutable JavaBeans would add boilerplate and make it easier for configuration to change during a run.

## Decision

Use Java 17 language features throughout the project. Use records by default for immutable data carriers, especially configuration values, plugin metadata, pipeline results, validation results, and database row models. Use ordinary final classes when identity, lifecycle, inheritance, framework integration, or substantial behaviour makes a record unsuitable.

## Consequences

### Positive

- Reduces boilerplate for value-oriented models.
- Makes configuration and execution inputs immutable after construction.
- Provides generated accessors, equality, hashing, and readable string representations.
- Makes data contracts explicit and concise.

### Negative or limiting

- Records are not suitable for every domain object.
- Changing a record component is a source- and binary-compatibility change.
- Some libraries may require mutable beans or no-argument constructors.

## Alternatives considered

### Mutable JavaBeans

Rejected as the default because they permit accidental mutation and require repetitive code.

### Lombok-generated value classes

Not selected because the project prefers standard Java features and minimal build-time tooling.

## Implementation notes

The project must compile for Java 17. Record constructors should validate mandatory invariants. Sensitive configuration values must not be exposed through generated `toString()` output; use a class or a redacted wrapper where necessary.
