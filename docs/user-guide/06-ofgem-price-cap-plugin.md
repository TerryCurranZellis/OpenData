# 6. Ofgem Price-Cap Plugin

**Document ID:** USER-006  
**Version:** 2.1  
**Status:** Runtime implemented; acceptance required  
**Baseline date:** 7 August 2026

---

The Ofgem plugin opens the official Energy Price Cap page, discovers the current
“Final levelised cap rates model” workbook, downloads the XLSX file and extracts
annual levelised default-tariff-cap values.

Register and test:

```text
opendata --plugin ofgem --register
opendata --plugin ofgem --Execute --dry-run
opendata --plugin ofgem --Execute
```

Normal and dry-run execution both require `--Execute` or `-x`.

To change configuration, copy the complete packaged
`config/plugins/ofgem.properties` definition, amend it and re-register it with
`--plugin ofgem --register --file <filename>`. A run does not accept `--file`.

Dry run proves discovery, download, parsing and validation only. A write run can
archive the workbook, record source URI/size/SHA-256, upsert the effective period
and transactionally replace the period's facts.

After a write run verify the plugin summary, `core.PluginRun`,
`core.ingestion_run`, `core.source_file`, `ofgem.price_cap_period` and
`ofgem.price_cap_level`.

The generic `core.PluginRun` UUID and Ofgem's numeric `core.ingestion_run`
identity are separate; correlate by plugin, time, source URI and log context.
