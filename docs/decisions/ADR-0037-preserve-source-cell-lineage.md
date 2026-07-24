# ADR-0037: Preserve workbook worksheet and cell lineage

- **Status:** Accepted
- **Date:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

Spreadsheet imports are difficult to verify when a database value cannot be
located in the source workbook, particularly after layout changes or mapping
corrections.

## Decision

Store `source_sheet` and `source_cell` on Ofgem fact rows and retain the source
file metadata and hash. Typed extracted records carry those values from parser to
repository without reconstructing them later.

## Consequences

### Positive

- representative values can be traced directly to the workbook;
- mapping regressions are easier to diagnose;
- audit evidence survives after the workbook is archived.

### Negative or limiting

- cell references are source-format-specific;
- workbook reformatting changes lineage even when the value is unchanged;
- retaining metadata does not replace validation of the source file itself.

## Alternatives considered

Keeping lineage only in logs was rejected. Storing complete workbook formulas in
the fact table was rejected as excessive for the first implementation.
