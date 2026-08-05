# Ofgem Energy Price Cap Plugin

**Document ID:** PLUGIN-OFGEM-INDEX-001
**Version:** 2.1
**Status:** Runtime and shared processing integration implemented; live acceptance pending
**Baseline date:** 4 August 2026

---

**Plugin id:** `ofgem`
**Implementation:** `com.towermarsh.opendata.plugin.ofgem.OfgemPlugin`
**Dataset id:** `ofgem-energy-price-cap`

## Purpose

The Ofgem plugin discovers the current official workbook from an HTML
publication page, downloads Excel, maps the primary annual levelised cap output,
validates source lineage and transactionally replaces one price-cap period.

## Pipeline

| Stage | Responsibility |
|---|---|
| Initialise | Build `OfgemConfiguration` and orchestrate stages |
| Extract | Discover and download the preferred XLSX |
| Transform | Map period and `OfgemPriceCapLevel` records |
| Validate | Enforce dimensions, uniqueness, values and source-cell lineage |
| Load | Dry-run metrics or transactional period replacement |
| Finalise | Archive the successful workbook when configured |

## Configuration processing

`OfgemConfiguration` uses shared `PluginPropertyValues` and `ValidationRules`.
Provider defaults remain:

- output filename `ofgem-final-levelised-cap-rates.xlsx`;
- connect timeout `PT30S`;
- request timeout `PT2M`;
- archive enabled;
- working directory `work/ofgem`; and
- archive directory `archive/ofgem`.

Private provider-local parsing helpers have been removed.

## Persistence processing

`OfgemPersistenceRepository` uses `JdbcTransactionTemplate` and inserts level
rows through `JdbcBatchExecutor` in batches of 500. Ofgem still owns all SQL,
provenance, generated keys, period lookup, current-period state and replacement
semantics.

Re-importing an existing period deletes and replaces its level rows inside the
same transaction. No partial provenance or fact set is committed on failure.

## Dry run

```text
opendata --plugin ofgem --dry-run
```

Dry run performs discovery, download, parsing and validation, but writes no
audit/business rows and does not archive the workbook.

## Documents

- [Architecture](../../architecture/021-ofgem-price-cap-architecture.md)
- [Workbook mapping](workbook-mapping.md)
- [Data model](data-model.md)
- [Configuration reference](../../reference/ofgem-plugin-configuration.md)
- [API reference](../../reference/ofgem-plugin.md)
- [Data dictionary](../../reference/ofgem-price-cap-data-dictionary.md)

## Acceptance still required

Run `mvn clean verify`, then complete live write, induced rollback, repeat import,
least-privilege and newly published workbook reconciliation tests.
