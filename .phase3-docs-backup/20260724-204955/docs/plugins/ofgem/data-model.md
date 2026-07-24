# Ofgem Price-Cap Data Model

**Document ID:** PLUGIN-OFGEM-MODEL-001  
**Version:** 1.0  
**Baseline date:** 24 July 2026

## Java records

| Record | Purpose |
|---|---|
| `OfgemPriceCapPeriod` | Effective period and source-column metadata |
| `OfgemPriceCapLevel` | One dimensional annual amount and lineage |
| `OfgemPriceCapWorkbookData` | Complete immutable extraction result |
| `OfgemImportResult` | Persisted period and row-count summary |

## Repository boundary

`OfgemPriceCapRepository` exposes period upsert and fact replacement.
`SqlServerOfgemPriceCapRepository` implements those operations with prepared JDBC
statements and explicit transactions.

## Keys

The fact key is:

```text
period + region + payment method + tariff type + consumption basis + VAT flag
```

The key prevents duplicate values for the same published interpretation while
allowing VAT-inclusive and VAT-exclusive outputs to coexist where the workbook
provides both.

## Extension rule

Do not add standing-charge or unit-rate columns to `price_cap_level`. Create a new
fact with its own unit, dimensions and source semantics. Component values use the
reserved component tables after their extraction rules are implemented.
