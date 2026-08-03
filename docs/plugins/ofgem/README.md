# Ofgem Energy Price Cap Plugin

**Document ID:** PLUGIN-OFGEM-INDEX-001
**Version:** 2.0
**Status:** Runtime flow implemented; live write acceptance pending
**Baseline date:** 3 August 2026

---

**Plugin id:** `ofgem`
**Implementation:** `com.towermarsh.opendata.plugin.ofgem.OfgemPlugin`
**Dataset id:** `ofgem-energy-price-cap`

## Purpose

The Ofgem plugin is the reference implementation for a public dataset whose
current workbook is discovered from a stable HTML publication page. It downloads
Excel, applies source-specific mapping and validation, preserves source lineage,
and transactionally replaces the facts for one price-cap period.

## Active pipeline

1. `OfgemPlugin` builds `initialise.OfgemConfiguration` from the resolved plugin
   definition.
2. `initialise.OfgemInitialise` controls the workflow.
3. `extract.OfgemWorkbookDownloader` discovers the preferred workbook link and
   downloads it to the configured working file.
4. `transform.OfgemPriceCapWorkbookExtractor` reads the workbook with Apache POI.
5. `transform.validate.OfgemWorkbookDataValidator` validates the period, source
   cells, business keys and extracted values.
6. `load.OfgemLoad` returns read/skipped metrics in dry-run mode or calls
   `load.OfgemPersistenceRepository` in write mode.
7. `finalise.OfgemFinalise` archives the successfully processed workbook when
   archiving is enabled.

The similarly named classes under older `plugin.ofgem.config`,
`plugin.ofgem.download` and `plugin.ofgem.extract` paths are not the classes
wired by the current initialise pipeline.

## Source discovery

The endpoint is the official Ofgem Energy Price Cap publication page. The
configured HTML-link discovery rules select an enabled XLSX link whose text
matches the final levelised cap-rates model. A publisher layout or link-text
change can break discovery and must not be silently guessed around.

## Extraction boundary

The implemented mapping reads the primary annual levelised default-tariff-cap
output from worksheet `1a Levelised DTC`. It produces:

- one `OfgemPriceCapPeriod`;
- immutable `OfgemPriceCapLevel` records;
- source worksheet and cell addresses for each value;
- stable dimension codes for region, payment method, tariff type, consumption
  basis and VAT inclusion.

The amount is an annual cap level in GBP. It is not a unit rate or daily standing
charge.

## Persistence and idempotency

One repository transaction resolves the dataset/provenance records, upserts the
period, updates current-period state and replaces the fact rows for that period.
Failure rolls back the transaction. Re-importing a period replaces its current
fact set rather than appending duplicates.

The plugin currently participates in both `core.PluginRun` and the separate
Ofgem ingestion/source-file provenance chain. That duplicated run identity is
transitional.

## Dry run

```text
opendata --plugin ofgem --dry-run
```

A dry run still performs HTML discovery, workbook download, parsing and
validation. It does not write audit/business rows and does not archive the
workbook.

## Documents

- [Workbook mapping](workbook-mapping.md)
- [Data model](data-model.md)
- [Architecture](../../architecture/021-ofgem-price-cap-architecture.md)
- [Configuration reference](../../reference/ofgem-plugin-configuration.md)
- [Data dictionary](../../reference/ofgem-price-cap-data-dictionary.md)
- [Run guide](../../guides/run-ofgem-price-cap-import.md)

## Remaining acceptance work

- live SQL Server write and induced rollback;
- repeat-import verification;
- least-privilege permission test;
- reconciliation against each newly published workbook layout;
- run/provenance identity consolidation;
- installed launcher and process exit-code mapping.
