# Ofgem Annex 9 Workbook Mapping

**Document ID:** PLUGIN-OFGEM-MAP-001  
**Version:** 3.0.0  
**Baseline date:** 15 August 2026  

## Source worksheet

`1a Levelised DTC`

## Structural values located by the extractor

- charge-restriction period text;
- source/current column reference;
- payment-method section headings;
- regional row labels and GB-average rows;
- output columns representing tariff and consumption combinations;
- explicit VAT-inclusive GB-average output where present.

## Mapping boundary

The extractor converts workbook cells into `OfgemPriceCapLevel` records. Every
record carries stable dimension codes, `BigDecimal amountGbp`, VAT status,
worksheet name and cell address.

## Validation expectations

- period start is not after period end;
- every mapped region code exists in reference data;
- every payment, tariff and consumption code exists;
- amounts are non-negative;
- source worksheet and cell are present;
- dimensional composite keys are unique within one extraction;
- extraction is non-empty and includes all expected payment sections.

## Layout change handling

A missing structural label is a parsing failure, not permission to guess. Update
mapping code and workbook fixtures together, record the change in documentation,
and decide whether a new ADR is needed only when the architectural strategy—not
just row positions—changes.
