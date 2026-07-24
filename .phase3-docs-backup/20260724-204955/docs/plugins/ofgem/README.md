# Ofgem Plugin Documentation

**Document ID:** PLUGIN-OFGEM-INDEX-001  
**Version:** 1.0  
**Status:** Extractor and persistence foundation implemented  
**Baseline date:** 24 July 2026

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

Implemented: primary annual levelised cap extraction from worksheet
`1a Levelised DTC`, typed records, SQL Server schema and repository persistence.

Outstanding: wire discovery, download, audit, extraction and persistence into the
registered Ofgem CLI plugin and final application run-status handling.

Deferred: full component tables, historical backfill and separate standing-charge
or unit-rate facts.
