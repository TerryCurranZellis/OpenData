# ADR-0026: Persist Octopus statement records as one transaction

- **Status:** Accepted and implemented
- **Date:** 2026-07-23
- **Implemented:** 2026-08-03
- **Decision owners:** OpenData maintainers

## Context

One source PDF can produce multiple electricity and gas records. Persisting only
part of the parsed statement would create an inconsistent source state.

## Decision

Persist the complete parsed batch and its processed-file ledger entries in one
SQL Server transaction. Mark a source file `COMPLETED` only inside that
transaction. Move the local PDF only after commit.

## Consequences

### Positive

- the database batch is complete or rolled back;
- a source is not marked complete without its business rows;
- repeated input has a deterministic ledger check;
- recovery from SQL failure is straightforward.

### Negative or limiting

- one invalid record prevents the complete batch from committing;
- filesystem archive cannot be atomic with SQL Server;
- post-commit archive failure needs operational recovery.

## Implementation

`OctopusPersistenceRepository.save` disables auto-commit, inserts/updates all
electricity and gas records, marks every batch statement completed, commits once
and rolls back on SQL or runtime failure. `OctopusFinalise` performs the archive
move after successful return from the load stage.
