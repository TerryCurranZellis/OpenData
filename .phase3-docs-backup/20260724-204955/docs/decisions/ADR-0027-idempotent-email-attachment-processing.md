# ADR-0027: Make email attachment processing idempotent

- **Status:** Shelved
- **Date:** 2026-07-23
- **Shelved:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

Email messages may be rediscovered after restarts, folder failures, resends or
mail-client activity. Read/unread state and filenames are not sufficient duplicate
identifiers.

## Decision

When email ingestion is resumed, track mailbox identifiers and attachment
metadata together with SHA-256 and explicit processing states. Completed
attachments are skipped; failed or partially completed attachments resume under a
documented retry policy.

## Consequences

### Positive

- scheduled polling and restarts can be safe;
- duplicate and partial processing are visible;
- post-commit mailbox failures can be recovered.

### Negative or limiting

- additional metadata and retention rules are required;
- identical content may occasionally represent distinct legitimate documents;
- folder-specific IMAP identifiers can change after moves.

## Implementation notes

Shelved until ADR-0024 and ADR-0025 are resumed. The Phase 3 source-file hash and
ingestion audit schema provide useful patterns but are not a complete email state
model.
