# ADR-0002: Use Apache Commons CLI

- Status: Accepted
- Date: 2026-07-21

## Context

OpenData requires a predictable command-line interface. The user prefers Apache Commons CLI rather than environment-variable-driven configuration.

## Decision

Use Apache Commons CLI for parsing global options including `--plugin`, optional `--file`, help, version, plugin listing, and dry run.

## Consequences

- Command syntax is explicit.
- Help generation is centralised.
- Plugins do not parse raw arguments.
- Apache Commons CLI becomes a required dependency.

## Alternatives considered

### Manual argument parsing

Rejected because validation and help behaviour would be unnecessarily error-prone.

### Environment variables

Rejected as the primary configuration mechanism because explicit command and parameter-file selection is preferred.
