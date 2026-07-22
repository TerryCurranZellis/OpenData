# Ofgem Plugin Configuration

**Document ID:** REF-OF-GEM-001  
**Version:** 1.0  
**Status:** Draft  
**Last Updated:** 22 July 2026

## Source Strategy

Ofgem updates the energy price cap quarterly. The direct workbook URL therefore
changes over time.

The Phase 1 plugin uses the stable official energy-price-cap publication page
and discovers the link whose text identifies the final levelised cap-rates
model and whose target is an XLSX workbook.

## Processing Sequence

```text
Download official publication page
  -> parse HTML
  -> locate matching XLSX anchor
  -> resolve relative URL
  -> download workbook
  -> archive original workbook
  -> parse visible worksheets
  -> validate and transform
  -> load SQL Server
```

## Authentication

The current official publication is public and does not require an API key.

The plugin model nevertheless supports credential references for future Ofgem
or other dataset endpoints.

## Implementation Note

The HTML link-discovery implementation should use JSoup and must resolve
relative URLs against the landing-page URI.

The workbook parser should use Apache POI.
