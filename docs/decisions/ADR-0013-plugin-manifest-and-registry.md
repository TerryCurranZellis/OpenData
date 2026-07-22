# ADR-0013: Describe plugins with manifests and resolve them through a registry

- Status: Accepted
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

The command line identifies a plugin by name. The framework therefore requires a stable mapping from user-facing plugin identifiers to plugin implementations and metadata without embedding dataset-specific conditional statements in application bootstrap.

## Decision

Each plugin provides a manifest containing its identifier, display name, version, description, implementation information, and default configuration resource. A central plugin registry resolves identifiers and supplies plugin factories or instances to the application.

## Consequences

### Positive

- Supports plugin discovery and `--list-plugins` output.
- Keeps plugin metadata separate from command parsing.
- Avoids `if` or `switch` chains in the application entry point.
- Creates a future path to service-based discovery.

### Negative or limiting

- Manifest and registry contracts need versioning.
- Duplicate plugin identifiers must be detected.
- The initial registry may still require explicit registration in code.

## Alternatives considered

### Hard-coded conditionals in the main class

Rejected because they couple application startup to every dataset.

### Independent external plugin JAR discovery immediately

Deferred because separately deployed plugin binaries are outside the initial modular-monolith scope.

## Implementation notes

Plugin identifiers should be lower-case and stable. Registry construction should fail fast on duplicates. External discovery through `ServiceLoader` may be considered later without changing the CLI contract.
