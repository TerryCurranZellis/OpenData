# ADR-0022: Keep CLI control commands in the application bootstrap

- Status: Accepted and implemented
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

Help, About, plugin listing, lifecycle operations, invocation errors, and configuration failures must be handled before dataset processing begins. Scripts also require stable process exit codes.

## Decision

Represent parsed arguments with an immutable command model and process them with an Apache Commons CLI adapter.

Handle help and About before database access; handle plugin listing and lifecycle administration at the application boundary before provider execution. Map failures to distinct `ExecutionStatus` values.

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

The implementation logs `ExecutionStatus` but deliberately does not call `System.exit`, so numeric process exit codes are not currently propagated. Plugin listing is supplied by the persistent SQL registry. ADR-0048 defines the current lifecycle command model and short-option resolution.
