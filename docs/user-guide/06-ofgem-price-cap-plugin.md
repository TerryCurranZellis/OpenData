# 6. Ofgem Price-Cap Plugin

**Document ID:** USER-006  
**Version:** 1.0  
**Status:** Runtime implemented; acceptance pending  
**Baseline date:** 26 July 2026

---

The Ofgem plugin opens the official price-cap page, finds the current “Final
levelised cap rates model” XLSX link, downloads the workbook and extracts annual
levelised default-tariff-cap values.

## Dry run

```text
opendata --plugin ofgem --dry-run
```

This downloads and extracts the workbook but does not archive it or write the
database.

## Write run

```text
opendata --plugin ofgem --file C:\OpenData\ofgem.properties
```

The write run optionally archives the workbook, records source URI/size/SHA-256,
upserts the effective period and transactionally replaces that period’s facts.

Verify the plugin summary, `core.PluginRun`, `core.ingestion_run`,
`core.source_file`, `ofgem.price_cap_period` and `ofgem.price_cap_level`.
The two run records are currently separate and are a known gap.

The initial extractor covers annual cap levels from `1a Levelised DTC`; detailed
component values and historical backfill are deferred.
