# Ofgem Plugin Documentation

**Document ID:** PLUGIN-OFGEM-INDEX-001  
**Version:** 1.2  
**Status:** Runtime flow implemented; live write acceptance pending  
**Baseline date:** 26 July 2026

The Ofgem plugin is the reference implementation for a dataset discovered from a
public HTML page, downloaded as Excel, interpreted using dataset-specific rules
and persisted into a normalised SQL Server schema.

## Documents

- [Workbook mapping](workbook-mapping.md)
- [Data model](data-model.md)
- [Architecture](../../architecture/021-ofgem-price-cap-architecture.md)
- [Data dictionary](../../reference/ofgem-price-cap-data-dictionary.md)
- [Run guide](../../guides/run-ofgem-price-cap-import.md)

## Current boundary

Implemented: registry selection, HTML discovery, workbook download, primary
annual levelised cap extraction from worksheet `1a Levelised DTC`, optional
archive, typed records, transactionally replaced SQL Server facts and metrics.
Dry run stops before archive, database and audit writes.

All provider-specific code is owned below
`com.towermarsh.opendata.plugin.ofgem`: `config`, `download`, `extract`,
`transform.model`, `transform.validate` and `load`. The root `OfgemPlugin`
contains the stage workflow only. The superseded top-level `opendata.ofgem`
import service and repository generation has been removed.

Outstanding: live SQL Server write/rollback acceptance and unification of the
generic `core.PluginRun` row with the Ofgem `core.ingestion_run` provenance row.

Deferred: full component tables, historical backfill and separate standing-charge
or unit-rate facts.
