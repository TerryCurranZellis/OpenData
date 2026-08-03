# ADR-0044: Process local Octopus Energy statement PDFs

- **Status:** Accepted and implemented
- **Date:** 2026-08-02
- **Updated:** 2026-08-03
- **Decision owners:** OpenData maintainers

## Context

Octopus Energy statements are available as PDF files and contain electricity and
gas tariff/meter/reading-period records. The application needs repeatable local
processing without requiring mailbox or account credentials.

## Decision

Implement plugin id `octopus` as a local-folder pipeline:

- accept only `octopus-energy-statement-YYYY-MM-DD.pdf` candidates;
- calculate SHA-256 and use `(filename, hash)` against a completed-file ledger;
- extract PDF text and parse typed electricity/gas records;
- insert or update all records and mark source files completed in one SQL
  transaction;
- move source PDFs to an archive directory only after commit;
- keep email/IMAP and direct Octopus account/API acquisition outside this ADR.

## Consequences

### Positive

- no Octopus account or mailbox credential is required;
- duplicate completed files are skipped deterministically;
- changed content with a reused filename is processed again;
- business rows and ledger completion are atomic;
- source statements are retained through an explicit archive step.

### Negative or limiting

- PDF parsing is coupled to statement text/layout conventions;
- input and archive directories contain personal billing data;
- filesystem archive cannot join the SQL transaction;
- operators must arrange statement download separately;
- dry-run parsing intentionally ignores the completion ledger, so it may parse files that write mode later skips.

## Implementation evidence

- `plugin.octopus.extract.OctopusExtract` and `PdfTextExtractor`;
- `plugin.octopus.transform.OctopusStatementParser`;
- `plugin.octopus.load.OctopusPersistenceRepository`;
- `plugin.octopus.finalise.OctopusFinalise`;
- `sql/007a-create-octopus-schema.sql`.

## Follow-up

Maintain dry-run isolation and complete live SQL/rollback/idempotency tests,
and define operational recovery for post-commit archive failures. Separate ADRs
remain authoritative for any future IMAP or account/API source adapter.
