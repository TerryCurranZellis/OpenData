# ADR-0021: Separate configuration resolution from validation

- Status: Accepted and implemented
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

An invocation can combine framework defaults, plugin defaults, optional override files, and command-line values. Loading those values and deciding whether the final configuration is valid are separate responsibilities.

## Decision

Use a `ConfigurationLoader` to resolve an immutable `ApplicationConfig`, then use `ConfigurationService` to apply a list of `ConfigurationValidator` implementations.

The default composition uses `StandardConfigurationValidator`. Alternative loaders and validators can be supplied through constructors.

## Consequences

### Positive

- Resolution rules can be tested independently from validation rules.
- Additional validators can be added without changing the loader.
- Immutable resolved configuration is passed to later layers.
- Constructor injection avoids a dependency-injection framework.

### Negative or limiting

- More types are required than a single utility method.
- Validator ordering can become significant.
- Error messages must identify the failing layer clearly.

## Alternatives considered

### Validate while reading each property

Rejected because cross-property rules require the complete resolved configuration.

### Use a dependency-injection framework

Rejected because the project prefers standard Java and explicit construction.

## Implementation notes

`Main` calls `ConfigurationService.resolve(arguments)`. The service loads, validates, and returns the configuration for one invocation.
