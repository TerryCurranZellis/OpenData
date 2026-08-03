# 6. Ofgem Price-Cap Plugin

**Document ID:** USER-006  
**Version:** 2.0  
**Status:** Runtime implemented; acceptance required  
**Baseline date:** 3 August 2026

---

The Ofgem plugin opens the official Energy Price Cap page, discovers the current
“Final levelised cap rates model” workbook, downloads the XLSX file and extracts
annual levelised default-tariff-cap values.

## Safe acceptance sequence

```text
opendata --plugin ofgem --dry-run
opendata --plugin ofgem --file C:\OpenData\ofgem.properties
```

Dry run proves discovery, download, parsing and validation only. A write run can
archive the workbook, record source URI/size/SHA-256, upsert the effective period
and transactionally replace the period's facts.

After a write run verify:

- the plugin summary log;
- `core.PluginRun`;
- `core.ingestion_run` and `core.source_file`;
- `ofgem.price_cap_period`; and
- `ofgem.price_cap_level`.

The generic `core.PluginRun` UUID and Ofgem's numeric `core.ingestion_run`
identity are currently separate and have no direct linking column. Correlate by
plugin, time, source URI and log context.

The initial extractor covers annual cap levels from worksheet
`1a Levelised DTC`; detailed component extraction and historical backfill remain
deferred.
