# Database Schema Reference

**Document ID:** REF-DB-SCHEMA-001
**Version:** 2.0
**Status:** Baseline
**Baseline date:** 3 August 2026
**Minimum Java version:** 24

---

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
| `core.PluginRun` | UUID runtime task status and read/write metrics |
| `core.schema_version` | Installed logical migration versions |
| `core.application_property` | Database-backed runtime application properties and encryption marker |
| `core.plugin_registry` | Registered plugin metadata, implementation class, configuration version and enabled status |
| `core.plugin_property` | Complete database-backed plugin definition values by plugin id |
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

## `openmeteo` schema

| Table | Primary purpose |
|---|---|
| `openmeteo.Location` | Stable location key, name, coordinates and timezone |
| `openmeteo.DailyWeather` | Daily temperature, daylight and weather-code facts |

## `octopus` schema

| Table | Primary purpose |
|---|---|
| `octopus.statement_file` | Completed source-file ledger keyed uniquely by filename and SHA-256 |
| `octopus.electric_data` | Electricity bill line facts keyed by statement date, tariff period and meter identifiers |
| `octopus.gas_data` | Gas bill line facts keyed by statement date, tariff period and meter identifiers |

## Natural and surrogate keys

- datasets use a stable dataset code;
- price-cap periods use a surrogate key with a unique effective date range;
- dimension tables use stable short codes;
- price-cap facts use a composite dimensional primary key;
- `core.PluginRun` uses a UUID; ingestion provenance uses identity keys;
- Octopus billing facts use composite natural keys and retain the last plugin run identifier;
- source files carry a SHA-256 value for provenance and duplicate analysis.

See the [Ofgem data dictionary](ofgem-price-cap-data-dictionary.md).

![OpenData database schemas](../diagrams/generated/opendata-database.svg){width=16cm}

The coexistence of `core.PluginRun` and `core.ingestion_run` is transitional and
must not be interpreted as the permanent target model.
