# ADR-0026: Persist Octopus bill records as one transaction

- **Status:** Shelved
- **Date:** 2026-07-23
- **Shelved:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

One bill can produce gas, electricity and adjustment records. Persisting only
some of them would make a source document appear partly processed.

## Decision

When the Octopus plugin is implemented, persist all records and source-document
metadata for one PDF inside one database transaction. Mark or move the email only
after database commit. Any persistence error rolls back the entire bill.

## Consequences

### Positive

- one bill is complete or absent;
- retry and mailbox-state rules are simpler;
- audit records align with the source document.

### Negative or limiting

- one category failure prevents all categories being saved;
- filesystem and IMAP actions cannot join the SQL transaction;
- recovery status is needed for failures after database commit.

## Implementation notes

Shelved with the Octopus plugin. ADR-0035 applies the same atomic replacement
principle to the implemented Ofgem period load.
