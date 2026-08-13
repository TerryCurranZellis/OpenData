# Ofgem Price-Cap Data Dictionary

**Document ID:** REF-OFGEM-DATA-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 24

---

## `ofgem.price_cap_period`

| Column | Type | Meaning |
|---|---|---|
| `price_cap_period_id` | `bigint` | Surrogate period identifier |
| `period_name` | `nvarchar(100)` | Human-readable workbook period |
| `effective_from` | `date` | First effective date |
| `effective_to` | `date` | Last effective date |
| `source_column_reference` | `int` | Workbook's current-column reference where available |
| `source_file_id` | `bigint` | Source workbook provenance |
| `is_current` | `bit` | Exactly one current period where populated |

## `ofgem.price_cap_level`

| Column | Type | Meaning |
|---|---|---|
| `price_cap_period_id` | `bigint` | Effective period |
| `region_code` | `varchar(40)` | Charge-restriction region/GB average |
| `payment_method_code` | `varchar(30)` | Other, standard credit or PPM code |
| `tariff_type_code` | `varchar(40)` | Electricity, gas or derived dual-fuel arrangement |
| `consumption_basis_code` | `varchar(20)` | Nil or benchmark consumption |
| `amount_gbp` | `decimal(19,6)` | Annual cap level in pounds |
| `vat_included` | `bit` | Whether the source value includes VAT |
| `source_sheet` | `nvarchar(128)` | Workbook worksheet |
| `source_cell` | `varchar(20)` | Workbook cell address |
| `ingestion_run_id` | `bigint` | Run that wrote the row |
| `loaded_at` | `datetime2(3)` | UTC load timestamp |

## Semantic warnings

- `amount_gbp` is not a unit rate;
- `amount_gbp` is not a daily standing charge;
- dual-fuel values may be derived outputs represented as their own tariff code;
- GB-average rows are explicitly flagged in the region dimension;
- VAT inclusion must be used when comparing figures.
