# ADR-0021: Separate configuration resolution from validation

- Status: Accepted; invocation-file layering superseded by ADR-0047 and ADR-0048
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

Configuration can combine framework defaults, stored application properties and stored plugin definitions. Loading those values and deciding whether the final configuration is valid are separate responsibilities. The original decision also allowed per-invocation override files; that part has been superseded by database-backed registration and the registration-only `--file` contract.

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

`OpenDataApplication` loads runtime configuration through `ApplicationRuntimeConfiguration` and validates each registered plugin through `PropertiesPluginDefinitionLoader`. `--file` is not an invocation override: ADR-0048 restricts it to registering one complete named plugin definition.
