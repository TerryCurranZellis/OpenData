# ADR-0004: Use plugin defaults plus optional override files

- Status: Accepted
- Date: 2026-07-21

## Context

Each dataset needs its own settings. A normal plugin run should work with packaged defaults, while a user may explicitly provide a machine- or run-specific override.

## Decision

Each plugin supplies a default properties resource. The CLI accepts an optional `--file` path. Values in that file override matching defaults. No override file is searched for implicitly.

## Consequences

- Behaviour is deterministic.
- Defaults are version controlled.
- Local settings can remain outside source control.
- Configuration requires schema validation and secret redaction.
