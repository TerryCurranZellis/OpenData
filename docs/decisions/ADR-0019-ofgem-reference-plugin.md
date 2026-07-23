# ADR-0019: Use Ofgem as HTML-to-Excel reference

**Status:** Accepted  
**Date:** 23 July 2026

## Context

Ofgem uses stable pages with changing quarterly workbook URLs.

## Decision

Discover the final levelised cap rates XLSX from HTML and parse with Apache POI.

## Consequences

Exercises HTML, URL resolution, archive and Excel workflows.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)
