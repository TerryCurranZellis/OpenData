# ADR-0016: Use a framework exception hierarchy and translate errors at boundaries

- Status: Accepted
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

Download, parsing, validation, configuration, plugin, and database failures need consistent handling by the command-line application. Throwing raw library exceptions across layers would expose implementation details and make exit-code and run-status decisions inconsistent.

## Decision

Define a small checked or runtime framework exception hierarchy rooted in an OpenData exception type. Translate external-library and infrastructure exceptions at package boundaries while preserving the original cause and adding safe operational context.

## Consequences

### Positive

- Produces consistent CLI error handling and exit codes.
- Keeps library-specific exceptions out of higher layers.
- Improves logging and run-failure classification.
- Preserves causes for diagnostics.

### Negative or limiting

- Poor translation can duplicate messages or hide important detail.
- The hierarchy must remain small enough to be useful.
- Sensitive values must be excluded from exception messages.

## Alternatives considered

### Propagate all raw exceptions

Rejected because callers would depend on every implementation library.

### One generic exception with no categories

Rejected because configuration, validation, transient download, and database failures need different handling.

## Implementation notes

Likely categories include configuration, plugin, download, parsing, validation, and persistence failures. Do not catch an exception merely to log and rethrow it at every layer; log once at the responsible application boundary.
