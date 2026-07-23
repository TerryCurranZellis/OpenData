# ADR-0026: Persist Octopus bill record lists as one transaction

- Status: Pending
- Date: 2026-07-23
- Decision owners: OpenData maintainers

## Context

One Octopus bill PDF can produce three collections of records:

- gas records;
- electricity records;
- adjustment records.

These collections represent one logical source document. Saving only some collections would leave the database inconsistent and could incorrectly cause the source email to be marked as processed.

## Proposed decision

Persist all records produced from one PDF inside a single database transaction.

The persistence boundary should accept a complete `OctopusImportResult` or equivalent object containing all three lists.

The SQL Server implementation will:

1. open a transaction;
2. insert or update gas records;
3. insert or update electricity records;
4. insert or update adjustment records;
5. record source-document/import metadata;
6. commit only when every operation succeeds;
7. roll back the complete transaction after any failure.

The source email must not be marked as processed until the transaction has committed successfully.

## Consequences

### Positive

- A bill is imported completely or not at all.
- Email state accurately reflects database state.
- Recovery and retry behaviour are simpler.
- Import auditing can be associated with one logical document.
- Partial gas, electricity, or adjustment imports are avoided.

### Negative or limiting

- Large bills hold a transaction open for longer.
- Retry logic must account for transaction rollback.
- Existing repository methods may require refactoring.
- Database constraints and duplicate handling must be designed before implementation.
- A failure in one record category prevents all categories from being saved.

## Alternatives considered

### Save each list independently

Rejected because partial imports could occur.

### Save every record in its own transaction

Rejected because it weakens document-level consistency and complicates retries.

### Mark the email as processed immediately after parsing

Rejected because successful parsing does not prove that persistence succeeded.

## Transaction boundary

The transaction begins immediately before persistence and ends after all record lists and import metadata have been stored.

Filesystem archiving and IMAP message movement cannot participate directly in the SQL transaction. They must occur in a controlled sequence after commit, with recoverable status information.

## Implementation status

Pending.

This ADR records intended future work only. Table design, keys, update rules, and repository methods will be finalised after the current code processes are complete.
