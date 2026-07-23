# ADR-0022: Keep CLI control commands in the application bootstrap

- Status: Accepted and implemented
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

Help, version, plugin listing, invocation errors, and configuration failures must be handled before dataset processing begins. Scripts also require stable process exit codes.

## Decision

Represent parsed arguments with an immutable command model and process them with an Apache Commons CLI adapter.

Handle help, version, and plugin-list requests in `Main` before configuration-driven execution. Map command-line errors, configuration errors, and unexpected failures to distinct exit codes.

## Consequences

### Positive

- Control commands do not initialise the processing pipeline.
- Shell scripts can distinguish invalid arguments from invalid configuration.
- The application boundary remains small and explicit.

### Negative or limiting

- The version string and plugin list must not remain hard-coded long term.
- Calling `System.exit` makes direct unit testing of `Main` more difficult.

## Alternatives considered

### Treat all failures as exit code 1

Rejected because it provides insufficient operational information.

### Put help and version handling inside plugins

Rejected because these are application-level concerns.

## Implementation notes

Current exit codes are 1 for unexpected failure, 2 for command-line error, and 3 for configuration error. Normal control commands return successfully.

The current hard-coded version banner and plugin list should later be supplied by build metadata and the plugin registry.
