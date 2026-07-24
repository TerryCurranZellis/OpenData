# Ofgem Price-Cap Architecture

**Document ID:** ARCH-021  
**Version:** 1.0  
**Status:** Components implemented; runtime orchestration pending  
**Baseline date:** 24 July 2026

---

## Scope

The initial Ofgem implementation imports the primary levelised default-tariff-cap
output from the Annex 9 workbook worksheet `1a Levelised DTC`. It does not yet
import every component or historical worksheet.

## Target flow

The Phase 2 and Phase 3 components support the following flow. Final wiring of
these steps into the registered Ofgem CLI plugin remains an integration task.

1. discover the preferred workbook link on the Ofgem landing page;
2. download to a controlled staging path and calculate SHA-256;
3. create or update audit records;
4. open the workbook with Apache POI;
5. locate the period, source-column reference and payment sections by labels;
6. map region rows and output columns into typed immutable records;
7. upsert the period;
8. replace all facts for that period in one transaction;
9. complete the ingestion run with counts and status.

## Domain model

`OfgemPriceCapPeriod` identifies the effective date range, display name, source
column and current flag. `OfgemPriceCapLevel` represents one annual amount by:

- charge-restriction region or GB average;
- payment method;
- tariff/fuel/metering arrangement;
- nil or benchmark consumption basis;
- VAT inclusion flag;
- source worksheet and source cell.

`OfgemPriceCapWorkbookData` is the immutable extraction boundary and
`OfgemImportResult` reports the persisted period and row count.

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

## Outstanding integration

The current package does not add the complete plugin orchestration that creates
the audit run, performs discovery/download, invokes the extractor/service,
completes the run and maps the final result into the application run-status enum.
That wiring should be the next implementation phase and should reuse these
components without moving SQL into the plugin class.

## Deferred scope

- detailed component-value extraction;
- historical workbook backfill;
- explicit standing-charge and unit-rate datasets;
- automated reconciliation with Ofgem published headline figures;
- handling of dynamically rendered download pages.
