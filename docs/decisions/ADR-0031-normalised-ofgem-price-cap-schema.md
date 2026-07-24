# ADR-0031: Store Ofgem price-cap output as a dimensional annual-level fact

- **Status:** Accepted
- **Date:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

The Annex 9 primary output presents annual default-tariff-cap levels by period,
region, payment method, tariff/metering arrangement and consumption basis. The
values are not daily standing charges or pence-per-kWh unit rates.

## Decision

Create normalised dimensions for regions, payment methods, tariff types and
consumption bases. Store one `amount_gbp` fact per dimensional combination in
`ofgem.price_cap_level`, with explicit VAT status and source-cell lineage. Use
`decimal(19,6)` in SQL Server and `BigDecimal` in Java.

## Consequences

### Positive

- source semantics are represented accurately;
- new periods add rows rather than columns;
- stable codes support reporting and validation;
- monetary values avoid floating-point rounding.

### Negative or limiting

- queries require dimension joins;
- rates require a separate dataset/fact;
- workbook labels must be mapped to stable codes.

## Alternatives considered

A wide table per workbook was rejected as difficult to evolve. Storing all
figures as generic doubles was rejected because of semantics and precision.
