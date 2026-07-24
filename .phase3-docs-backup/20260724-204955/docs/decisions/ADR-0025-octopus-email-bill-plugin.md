# ADR-0025: Introduce the Octopus email bill plugin

- **Status:** Shelved
- **Date:** 2026-07-23
- **Shelved:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

Existing code can parse Octopus Energy bill PDFs into gas, electricity and
adjustment records. Integration depends on the reusable email source and a stable
transactional persistence model.

## Decision

When resumed, create plugin `octopus` to coordinate attachment acquisition,
existing PDF parser adaptation, validation, persistence and post-processing. IMAP
logic remains framework infrastructure rather than part of the supplier plugin.

## Consequences

### Positive

- existing parsing can be reused;
- supplier rules remain isolated;
- email acquisition and PDF interpretation are independently testable.

### Negative or limiting

- bill layouts and sender details can change;
- document-level duplicate and recovery rules are required;
- final persistence keys and schemas remain to be designed.

## Implementation notes

Shelved until the core Ofgem/OpenMeteo runtime flows and operational audit model
are complete and verified.
