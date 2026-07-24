# Database Schema Reference

**Document ID:** REF-DB-SCHEMA-001  
**Version:** 1.0  
**Baseline date:** 24 July 2026

## Database and principals

| Object | Name |
|---|---|
| Database | `OpenData` |
| SQL login | `OpenData` |
| Database user | `OpenData` |
| Application role | `opendata_app` |

## `core` schema

| Table | Primary purpose |
|---|---|
| `core.schema_version` | Installed logical migration versions |
| `core.dataset` | Dataset/plugin registration |
| `core.ingestion_run` | Run status, timing, counters and message |
| `core.source_file` | Source URI, filename, size, media type and SHA-256 |
| `core.ingestion_error` | Stage/row/field diagnostics |

## `ofgem` schema

| Table | Primary purpose |
|---|---|
| `ofgem.charge_restriction_region` | Region and GB-average dimension |
| `ofgem.payment_method` | Payment-method dimension |
| `ofgem.tariff_type` | Fuel/tariff/metering dimension |
| `ofgem.consumption_basis` | Nil or benchmark basis dimension |
| `ofgem.price_cap_period` | Effective period and source file |
| `ofgem.price_cap_level` | Annual cap-level fact |
| `ofgem.price_cap_component` | Component reference data |
| `ofgem.price_cap_component_value` | Reserved detailed component fact |

## Natural and surrogate keys

- datasets use a stable dataset code;
- price-cap periods use a surrogate key with a unique effective date range;
- dimension tables use stable short codes;
- price-cap facts use a composite dimensional primary key;
- audit records use identity keys;
- source files carry a SHA-256 value for provenance and duplicate analysis.

See the [Ofgem data dictionary](ofgem-price-cap-data-dictionary.md) and
[database ER diagram](../diagrams/database/opendata-database.puml).
