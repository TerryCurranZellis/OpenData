# ADR-0010: Use constructor injection without a dependency-injection framework

- Status: Accepted
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

The framework needs testable components with explicit dependencies, but the initial application is a modular Java command-line program. A full dependency-injection container would increase startup, configuration, and dependency complexity.

## Decision

Use constructor injection for mandatory dependencies and assemble the object graph in application bootstrap/factory classes. Do not introduce Spring, CDI, Guice, or another dependency-injection container in the initial architecture.

## Consequences

### Positive

- Dependencies are visible and enforceable at construction time.
- Components are straightforward to unit test.
- Avoids reflection-heavy framework behaviour and hidden wiring.
- Keeps the runtime and dependency set small.

### Negative or limiting

- Bootstrap code must create and connect components explicitly.
- Large object graphs may eventually require additional factories or modules.
- Cross-cutting lifecycle features must be implemented deliberately.

## Alternatives considered

### Spring Boot

Rejected for the initial command-line framework because it is larger than required and would shape the architecture around the framework.

### Service locator or static globals

Rejected because they hide dependencies and make testing difficult.

## Implementation notes

Constructors should reject null mandatory dependencies. Optional behaviour should be represented through explicit strategy interfaces or optional configuration, not mutable setter injection.
