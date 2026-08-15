# Download and Parsing Architecture

**Document ID:** ARCH-DOWNLOAD-001  
**Version:** 3.0.0  
**Status:** Implemented shared components; extension gaps identified  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Purpose

This document defines reusable download and parser components for API, file,
HTML landing-page, CSV and Excel sources.

## Processing Model

```text
PluginEndpointDefinition
    -> download strategy
    -> working resource
    -> parser selected from the file extension by DataParserFactory
    -> List<Map<String,String>> (current transitional model)
    -> validation and transformation
    -> optional plugin-owned archive/persistence
```

## Download Strategies

### Direct HTTP

`DirectHttpDownloadStrategy` streams a response to a temporary `.part` file.
Only a successful 2xx response is moved to the final destination.

### HTML Link Discovery

`HtmlLinkDiscoveryStrategy` downloads a landing page and delegates HTML matching
to `HtmlLinkResolver`.

Discovery rules are held in `LinkDiscoveryDefinition`:

- CSS selector;
- href regular expression;
- optional link-text regular expression;
- first- or last-match selection.

Relative URLs are resolved against the landing-page URI.

## Parser Implementations

### CSV

`CsvDataParser` uses Apache Commons CSV. Parser behaviour is represented by the
`CsvParserOptions` record.

### Excel

`ExcelDataParser` uses Apache POI and supports XLS and XLSX through
`WorkbookFactory`. Parser behaviour is represented by `ExcelParserOptions`.

### JSON

`JsonDataParser` uses Jackson. The OpenMeteo live path uses the provider-local
`OpenMeteoResponseExtractor`, `OpenMeteoResponseValidator` and
`OpenMeteoTransformer` because its parallel-array response needs typed,
cross-field validation.

## Transitional Result Model

The existing `DataParser` interface returns `List<Map<String,String>>`. This
overlay deliberately retains that contract to minimise integration risk.

A typed `DataRecord`/`DataTable` replacement remains an open extension decision.

## Error Handling

Network failures produce `DownloadException`.

CSV and workbook failures produce `ImportException`.

Interrupted HTTP requests restore the thread interrupt flag.

## Future Extensions

- API-key and OAuth authentication decorators;
- ZIP extraction;
- streaming JSON for very large responses;
- HTML table parsing;
- browser automation as a last resort;
- checksum validation;
- MIME-type verification;
- parser registry selected by `DatasetFormat`.
