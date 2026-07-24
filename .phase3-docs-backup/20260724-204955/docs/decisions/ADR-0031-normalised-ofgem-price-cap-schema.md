# ADR-0031: Store Ofgem price-cap output as a dimensional annual-level fact

- **Status:** Accepted
- **Date:** 2026-07-24

## Context

The Ofgem Annex 9 primary output presents annual default-tariff-cap levels by
period, region, payment method, tariff/metering arrangement and consumption
basis. The values are not themselves daily standing charges or pence-per-kWh
unit rates.

## Decision

Create normalised reference tables for regions, payment methods, tariff types
and consumption bases. Store one `amount_gbp` row in
`ofgem.price_cap_level` for each dimensional combination, retaining the source
worksheet and cell. Keep VAT inclusion explicit. Define a separate component
fact table for later extraction of historical/component outputs.

## Consequences

- The database accurately represents the source semantics.
- New periods append naturally without adding columns.
- Reporting can compare dimensions and periods using stable codes.
- A later rates dataset will require a separate fact table rather than
  overloading `price_cap_level`.
