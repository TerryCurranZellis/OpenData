# Octopus Energy Statement Architecture

**Document ID:** ARCH-027  
**Version:** 2.0  
**Status:** Runtime and dry-run implemented; live acceptance pending  
**Baseline date:** 3 August 2026

---

The Octopus plugin is the reference for local document ingestion and processed
source-file idempotency. It reads statements already downloaded to a local
folder; email, IMAP and direct account/API acquisition are outside scope.

## Write-mode flow

1. validate the input directory and select supported filenames;
2. calculate size and SHA-256;
3. read completed `(filename, hash)` keys;
4. extract text for new/changed candidates;
5. parse electricity and gas records;
6. persist business rows and completion ledger in one transaction;
7. archive successfully committed PDFs.

## Dry-run flow

Dry run performs directory validation, discovery, hashing, text extraction,
parsing and metrics. It deliberately skips the processed-file repository, SQL
load, generic audit and archive. Consequently every matching input PDF is parsed,
including files that a later write run may recognise as completed.

## Transaction/archive boundary

Business records and the file ledger commit atomically. File movement occurs
after commit and requires operational reconciliation if it fails.

::: {.landscape}
![Octopus statement processing](../diagrams/generated/octopus-statement-processing.svg){width=22.5cm}

![Octopus data model](../diagrams/generated/octopus-data-model.svg){width=22.5cm}
:::
