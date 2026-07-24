# ADR-0029: Use shared HTML discovery and standards-based tabular parsers

- Status: Proposed
- Date: 2026-07-23
- Decision owners: OpenData maintainers

## Context

OpenData publishers frequently expose changing Excel or CSV links from landing
pages. The existing downloader requires a final file URI, and the existing CSV
parser splits lines and fields on commas, which does not support quoted commas,
escaped quotes or multiline values. Excel workbooks are not yet supported.

## Decision

Introduce a shared HTML discovery package implemented with Jsoup. Plugins supply
filtering and preference terms while the framework resolves and selects links.
Use Apache Commons CSV for CSV parsing and Apache POI for `.xls` and `.xlsx`
parsing. Keep all application logging on `java.util.logging`; route third-party
Log4j API output to JUL through `log4j-to-jul`.

Downloads will be streamed to a temporary file, limited by a configurable
maximum size, and atomically moved to the final destination where supported.
Ambiguous equal-scoring links will cause a discovery error.

## Consequences

### Positive

- Publisher landing-page changes can be handled through plugin properties.
- CSV quoting and multiline data are parsed correctly.
- Excel datasets become first-class inputs.
- Shared behaviour prevents each plugin from implementing its own scraper.
- Partial downloads do not replace a previously valid file.

### Negative or limiting

- Jsoup and Apache POI increase the dependency footprint.
- Workbook-specific sheet and header settings still belong in each plugin.
- Dynamic JavaScript-only download pages may require a later alternative.

## Alternatives considered

### Regular expressions over HTML

Rejected because HTML parsing and relative URL resolution are not reliable with
regular expressions.

### Plugin-specific HTML parsing

Rejected because it would duplicate networking, filtering and error handling.

### Continue using line splitting for CSV

Rejected because valid CSV can contain delimiters and newlines inside quoted
fields.

## Implementation notes

This ADR remains Proposed until the overlay is merged, the tests pass in the
repository build, and the Ofgem plugin uses the shared discovery pipeline.
