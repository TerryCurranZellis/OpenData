# ADR-0029: Use shared HTML discovery and standards-based tabular parsers

- **Status:** Accepted
- **Date:** 2026-07-23
- **Accepted:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

Open-data publishers frequently expose changing Excel or CSV links from landing
pages. Robust CSV supports quoted delimiters and multiline fields, and workbook
parsing must support both XLS and XLSX.

## Decision

Use a shared HTML discovery package implemented with Jsoup. Plugins supply
filtering and preference terms while the framework resolves and selects links.
Use Apache Commons CSV for CSV and Apache POI for Excel. Stream downloads to a
partial file with a size limit and atomically move completed files where
supported. Equal best-scoring links are an error rather than an arbitrary choice.

## Consequences

### Positive

- publisher landing-page changes can usually be handled in configuration;
- standards-compliant CSV and Excel become shared inputs;
- partial downloads do not replace valid files;
- plugin code remains focused on dataset meaning.

### Negative or limiting

- Jsoup and Apache POI increase dependencies;
- workbook-specific mappings remain plugin responsibilities;
- JavaScript-only pages require a later source strategy.

## Alternatives considered

Regular expressions over HTML, plugin-specific scrapers and `String.split` CSV
parsing were rejected as unreliable or duplicative.

## Implementation notes

Implemented by the Phase 2 web-ingestion overlay and used as the source boundary
for the Ofgem workbook flow.
