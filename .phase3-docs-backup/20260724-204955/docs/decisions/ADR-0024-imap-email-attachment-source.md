# ADR-0024: Use IMAP as a reusable email attachment source

- **Status:** Shelved
- **Date:** 2026-07-23
- **Shelved:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

Some future datasets, beginning with Octopus Energy bill PDFs, may arrive as
email attachments. Mailbox access is a reusable framework concern and must not
depend on Outlook desktop automation.

## Decision

When email ingestion is resumed, introduce a reusable email-source abstraction
with an IMAP implementation. It will search configured folders, filter messages,
save attachments into controlled staging, expose message/attachment metadata and
support explicit post-processing states.

## Consequences

### Positive

- processing can run without Outlook or an interactive desktop;
- mailbox access can be reused by multiple plugins;
- supplier-specific parsing remains outside the email infrastructure.

### Negative or limiting

- provider authentication and folder rules vary;
- OAuth or application passwords may be required;
- attachments require size, path, signature and malware controls;
- message-state recovery and duplicate handling need durable metadata.

## Alternatives considered

Outlook automation and manual attachment saving were rejected for unattended
processing. A watched folder remains possible as a simpler future source adapter.

## Implementation notes

Shelved until the current HTTP/API plugins and database orchestration are fully
integrated. No IMAP dependency should be added during Phase 3.
