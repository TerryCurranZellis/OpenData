# Ofgem Price-Cap Architecture

**Document ID:** ARCH-021
**Version:** 2.0
**Status:** Runtime flow implemented; live write acceptance pending
**Baseline date:** 3 August 2026

---

## Scope

The current Ofgem implementation imports the primary annual levelised
default-tariff-cap output from worksheet `1a Levelised DTC`. It does not import
every component, unit-rate, standing-charge or historical worksheet.

## Active flow

1. `OfgemPlugin` delegates to `initialise.OfgemInitialise`.
2. `extract.OfgemWorkbookDownloader` discovers and downloads the preferred XLSX.
3. `transform.OfgemPriceCapWorkbookExtractor` maps period and dimensional values.
4. `transform.validate.OfgemWorkbookDataValidator` rejects invalid, duplicate or
   lineage-free values.
5. `load.OfgemLoad` stops before persistence in dry-run mode.
6. `load.OfgemPersistenceRepository` performs provenance and period replacement
   in one transaction during a write run.
7. `finalise.OfgemFinalise` archives the successful workbook after loading.

## Domain and persistence

`OfgemPriceCapPeriod` identifies the effective range and source column.
`OfgemPriceCapLevel` stores one annual GBP amount by region, payment method,
tariff type, consumption basis and VAT flag, with worksheet/cell lineage.

Stable dimension codes are seeded in SQL Server. The fact uses a composite
business key and `decimal(19,6)`/`BigDecimal` values. Provider ingestion/source
provenance remains separate from the coordinator's `core.PluginRun` identity.

## Change tolerance

The extractor searches for structural labels rather than relying only on fixed
rows. A publisher layout change can still invalidate the mapping. New workbook
fixtures and expected source-cell assertions are required for each mapping
change.

## Acceptance and deferred work

Live write, rollback, repeat-import and permission tests remain outstanding.
Detailed component facts, historical backfill, rate datasets and automated
headline reconciliation remain deferred.

::: {.landscape}
![Ofgem price-cap import](../diagrams/generated/ofgem-price-cap-import-sequence.svg){width=22.5cm}
:::
