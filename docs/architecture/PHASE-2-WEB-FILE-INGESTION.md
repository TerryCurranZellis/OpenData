# Phase 2: Web File Ingestion

**Document ID:** ARCH-PHASE-2-001  
**Version:** 1.1  
**Status:** Historical phase design; discovery and parsing subsequently implemented  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 24

---

## Purpose

Many public-data publishers do not expose a permanent dataset URL. Instead,
they publish a landing page whose links change when a new workbook or CSV file
is released. This historical phase introduced the reusable discovery stage that
now precedes download and parsing for Ofgem.

## Historical target pipeline

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
    CsvDataParser (Apache Commons CSV)
    ExcelDataParser (Apache POI)
    JsonDataParser (Jackson)
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

## Subsequent implementation

The implemented Ofgem definition is
`src/main/resources/config/plugins/ofgem.properties`. It uses the stable Ofgem
publication page, `html-link-discovery`, an XLSX href pattern and the link text
for the final levelised cap rates model. Download timeouts, working/archive
directories and formula evaluation are typed plugin properties.

Workbook extraction is plugin-specific: `OfgemPriceCapWorkbookExtractor`
locates the `1a Levelised DTC` worksheet and interprets its structural labels.
Shared discovery and parser classes remain source-agnostic.

The current Ofgem runtime uses `HtmlLinkDiscoveryStrategy` and
`HtmlLinkResolver`, applying the configured CSS selector and regular
expressions before choosing the first or last match. The separate
`JsoupHtmlLinkDiscoverer` and `HighestScoringLinkSelector` remain reusable
contracts but are not the Ofgem execution path.
