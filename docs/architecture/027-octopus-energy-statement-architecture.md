# Octopus Energy Statement Architecture

**Document ID:** ARCH-027
**Version:** 2.0
**Status:** Write path implemented; dry-run defect and live acceptance pending
**Baseline date:** 3 August 2026

---

## Scope

The Octopus plugin is the reference for local document ingestion and processed
source-file idempotency. It reads statements already downloaded to a local
folder. Email, IMAP and direct account/API acquisition are outside the current
runtime.

## Processing flow

1. validate the input directory;
2. select files matching `octopus-energy-statement-YYYY-MM-DD.pdf`;
3. calculate file size and SHA-256;
4. read completed `(filename, hash)` keys from `octopus.statement_file`;
5. extract PDF text for new or changed candidates;
6. parse typed electricity and gas records;
7. insert/update all business rows and mark files completed in one transaction;
8. move successfully committed PDFs to the archive directory.

## Transaction and archive boundary

The business records and file ledger commit atomically. The file move occurs
after commit and cannot participate in the SQL transaction. A move failure is
logged and requires operational recovery; it does not make the database batch
partial.

## Dry-run variance

The framework's dry-run design deliberately denies plugin database access.
`OctopusExtract` currently reads the processed-file ledger regardless of the
dry-run flag, so Octopus fails before transformation. The load and archive stages
are side-effect free, but the complete plugin is not dry-run compliant until the
extract logic is corrected.

::: {.landscape}
![Octopus statement processing](../diagrams/generated/octopus-statement-processing.svg){width=22.5cm}

![Octopus data model](../diagrams/generated/octopus-data-model.svg){width=22.5cm}
:::
