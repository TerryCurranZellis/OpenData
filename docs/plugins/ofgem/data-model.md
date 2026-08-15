# Ofgem Price-Cap Data Model

**Document ID:** PLUGIN-OFGEM-MODEL-001
**Version:** 3.0.0  
**Status:** Implemented primary annual-cap model
**Baseline date:** 15 August 2026  

---

## Java records

| Record | Purpose |
|---|---|
| `OfgemPriceCapPeriod` | Effective period, display text, current flag and source-column metadata |
| `OfgemPriceCapLevel` | One dimensional annual GBP amount with worksheet/cell lineage |
| `OfgemPriceCapWorkbookData` | Immutable complete extraction result |
| `OfgemPersistenceResult` | Inserted, updated and skipped load counts |

## Fact key

```text
period
+ region
+ payment method
+ tariff type
+ consumption basis
+ VAT inclusion flag
```

The composite key permits VAT-inclusive and VAT-exclusive interpretations where
published while preventing duplicate values for the same dimensional meaning.

## Persistence ownership

`plugin.ofgem.load.OfgemPersistenceRepository` owns all provider SQL and the
explicit transaction. It also creates the domain ingestion/source-file
provenance used by the Ofgem schema. The coordinator's `core.PluginRun` remains a
separate framework audit identity.

## Extension rule

Do not add standing-charge or unit-rate columns to `price_cap_level`. Those are
different measures and require separate facts with their own units and mapping
rules. The reserved component tables may be used only after component extraction
semantics are implemented and documented.
