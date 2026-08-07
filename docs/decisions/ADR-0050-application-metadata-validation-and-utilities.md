# ADR-0050: Centralise application metadata, validation and focused utilities

- Status: Accepted
- Date: 2026-08-07
- Decision owners: OpenData maintainers

## Context

Application and bootstrap configuration repeated required-text, integer,
Boolean and duration parsing even though plugins already used shared typed
validation. Root-cause message traversal and `HH:mm:ss` duration formatting
were also duplicated. Application identity could differ between the splash
screen, properties and JAR metadata, and startup identity was not written to the
log.

## Decision

Use `ApplicationPropertyValues` for typed application and bootstrap property
access, alongside the existing `PluginPropertyValues` for plugin settings and
plugin-specific validators for source data. Remove the unused map-based
`Validator`, `DataQualityValidator` and `ValidationResult` API.

Use small, purpose-specific `ExceptionMessages` and `DurationFormatter`
utilities rather than a general utility class. Maven project version 2.1.0 is
filtered into `application-metadata.properties` and written to the JAR
manifest. `ApplicationInfo` reads the manifest first and packaged metadata as
the IDE fallback. Startup logging records product/version, runtime, operating
system, working directory and non-sensitive invocation flags.

## Consequences

### Positive

- Application and plugin configuration now have parallel typed-validation paths.
- Exception and duration presentation are consistent and independently tested.
- Splash/About and startup logs share one application identity source.
- Configuration parsing and startup diagnostics are easier to extend.

### Negative or limiting

- The bootstrap file retains an explicit compatibility version used for database registration.
- Source-tree execution relies on Maven resource processing for generated application metadata.
- Negative elapsed durations are rejected rather than formatted.

## Alternatives considered

### General-purpose utility class

Rejected because unrelated behaviour would become coupled and the class would
grow without a clear responsibility.

### Adapt plugins to the legacy map-based validator

Rejected because current plugins validate typed records and configuration;
converting them back to maps would discard type safety.

## Implementation notes

This decision is implemented in OpenData 2.1. All changed Java source and test
classes are marked `@version 2.1`.
