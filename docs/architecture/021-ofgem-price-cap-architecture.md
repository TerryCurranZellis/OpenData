# Ofgem Price-Cap Architecture

**Document ID:** ARCH-021  
**Version:** 1.2  
**Status:** Runtime flow implemented; live write acceptance pending  
**Baseline date:** 26 July 2026

---

## Scope

The initial Ofgem implementation imports the primary levelised default-tariff-cap
output from the Annex 9 workbook worksheet `1a Levelised DTC`. It does not yet
import every component or historical worksheet.

## Implemented flow

The registered `OfgemPlugin` implements the following flow:

1. `download.OfgemWorkbookDownloader` discovers the preferred workbook link and
   downloads it to a controlled working path;
2. `extract.OfgemPriceCapWorkbookExtractor` opens and extracts the workbook with
   Apache POI;
3. `transform.validate.OfgemWorkbookDataValidator` rejects duplicate source
   cells and duplicate business keys;
4. return metrics immediately in a dry run;
5. optionally archive the workbook;
6. `load.OfgemPersistenceRepository` resolves the seeded dataset and creates
   ingestion/source-file provenance,
   including SHA-256;
7. upsert the period and replace all facts for it in one transaction;
8. complete domain ingestion and plugin-run audit records.

## Domain model

`OfgemPriceCapPeriod` identifies the effective date range, display name, source
column and current flag. `OfgemPriceCapLevel` represents one annual amount by:

- charge-restriction region or GB average;
- payment method;
- tariff/fuel/metering arrangement;
- nil or benchmark consumption basis;
- VAT inclusion flag;
- source worksheet and source cell.

`OfgemPriceCapWorkbookData` is the immutable extraction boundary.
`OfgemPersistenceResult` reports inserted, updated and skipped counts to the
plugin facade.

## Persistence model

Dimensions use stable codes rather than workbook labels as foreign keys. The fact
value is `decimal(19,6)` / `BigDecimal` and is named `amount_gbp` because the
primary output is an annual cap level in pounds. It must not be presented as a
unit rate or daily standing charge.

## Workbook change tolerance

The extractor searches for structural labels instead of depending solely on one
fixed row number. Nevertheless, a publisher layout change can still invalidate
mappings. Tests should use a representative workbook fixture and assert expected
sections, regions, values and source cells.

## Current integration limits

The CLI/runtime flow is present and dry-run execution has been demonstrated.
Before production acceptance:

- complete a live SQL Server write and rollback test;
- unify the coordinator `core.PluginRun` row with the Ofgem
  `core.ingestion_run`/`core.source_file` provenance chain;
- verify workbook extraction against each newly published layout;
- configure executable packaging and operational exit codes.

## Deferred scope

- detailed component-value extraction;
- historical workbook backfill;
- explicit standing-charge and unit-rate datasets;
- automated reconciliation with Ofgem published headline figures;
- handling of dynamically rendered download pages.
