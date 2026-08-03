# Ofgem Plugin Reference

**Document ID:** REF-PLUGIN-OFGEM-001
**Version:** 2.0
**Status:** Version 2.0.0 implementation reference
**Baseline date:** 3 August 2026

---

| Item | Value |
|---|---|
| Plugin id | `ofgem` |
| Implementation class | `com.towermarsh.opendata.plugin.ofgem.OfgemPlugin` |
| Dataset id | `ofgem-energy-price-cap` |
| Endpoint | `price-cap-publication` |
| Source | Official Ofgem publication page and discovered XLSX workbook |
| Primary worksheet | `1a Levelised DTC` |
| Write model | Transactional period/fact replacement |

## Active class flow

```text
OfgemPlugin
 -> initialise.OfgemInitialise
 -> extract.OfgemWorkbookDownloader
 -> transform.OfgemPriceCapWorkbookExtractor
 -> transform.validate.OfgemWorkbookDataValidator
 -> load.OfgemLoad / OfgemPersistenceRepository
 -> finalise.OfgemFinalise
```

## Metrics

`read` is the number of extracted `OfgemPriceCapLevel` values. A dry run reports
all read rows as skipped. A write run returns repository insert/update/skip
counts.

## Failure conditions

- no matching workbook link;
- HTTP or file-write failure;
- missing worksheet or structural labels;
- invalid/duplicate period or business keys;
- missing source-cell lineage;
- SQL/provenance/transaction failure;
- archive failure after successful processing.

See the [configuration reference](ofgem-plugin-configuration.md),
[data dictionary](ofgem-price-cap-data-dictionary.md) and
[plugin documentation](../plugins/ofgem/README.md).
