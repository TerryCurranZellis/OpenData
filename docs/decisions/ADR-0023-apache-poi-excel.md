# ADR-0023: Use Apache POI for XLS and XLSX

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Public datasets commonly use formatted workbooks and formulas.

## Decision

Use WorkbookFactory, DataFormatter and optional formula evaluation.

## Consequences

Both formats work; unusual layouts require plugin transformation.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
