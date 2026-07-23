# Phase 2: Web File Ingestion

## Purpose

Many public-data publishers do not expose a permanent dataset URL. Instead,
they publish a landing page whose links change when a new workbook or CSV file
is released. The framework therefore needs a reusable discovery stage before
the existing download and parse stages.

## Pipeline

```text
Landing page
    ↓
JsoupHtmlLinkDiscoverer
    ↓
LinkDiscoveryRequest filtering
    ↓
HighestScoringLinkSelector
    ↓
HttpDataDownloader
    ↓
DataParserFactory
    ├── CsvDataParser (Apache Commons CSV)
    ├── ExcelDataParser (Apache POI)
    └── JsonDataParser (existing)
```

## Safety decisions

1. Relative links are resolved against the landing-page URI.
2. Duplicate target URIs are removed while retaining document order.
3. Plugins specify allowed extensions, required terms and excluded terms.
4. Equal best selection scores fail rather than choosing arbitrarily.
5. Downloads are streamed rather than loaded wholly into memory.
6. A configurable maximum byte count guards against unexpectedly large files.
7. Files are written to a `.part` sibling and moved into place only when the
   transfer succeeds.
8. CSV parsing uses a standards-aware parser instead of `String.split()`.
9. Excel parsing supports both `.xls` and `.xlsx`, named sheets, displaced
   header rows and formula evaluation.

## Plugin configuration planned for the next batch

The Ofgem plugin should expose properties similar to:

```properties
source.page-uri=https://www.ofgem.gov.uk/...
source.allowed-extensions=xlsx,xls,csv
source.required-terms=price cap
source.excluded-terms=archive,methodology
source.preferred-terms=annex,levelisation
parser.sheet-name=
parser.sheet-index=0
parser.header-row=0
parser.first-data-row=1
```

The exact values must be based on the current Ofgem publication page and the
selected workbook structure, rather than hard-coded in the shared framework.
