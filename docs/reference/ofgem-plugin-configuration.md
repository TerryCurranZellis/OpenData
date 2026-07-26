# Ofgem Plugin Configuration

**Document ID:** REF-OF-GEM-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

## Source Strategy

Ofgem updates the energy price cap quarterly. The direct workbook URL therefore
changes over time.

The plugin uses the stable official energy-price-cap publication page
and discovers the link whose text identifies the final levelised cap-rates
model and whose target is an XLSX workbook.

## Processing Sequence

```text
Download official publication page
  -> parse HTML
  -> locate matching XLSX anchor
  -> resolve relative URL
  -> download workbook
  -> extract worksheet 1a Levelised DTC
  -> validate period and annual cap levels
  -> archive original workbook (write mode)
  -> replace the SQL Server period transactionally (write mode)
```

## Authentication

The current official publication is public and does not require an API key.

The plugin definition model can represent credential references, but production
secret resolution is not implemented.

## Current property groups

- `property.download.*` controls the output name and HTTP timeouts;
- `property.download.working-directory` controls temporary storage;
- `property.archive.original-file` and `property.archive.directory` control
  write-mode archiving;
- `property.excel.evaluate-formulas` controls workbook formula evaluation.

`property.excel.sheet-selection` and the generic database target properties are
present in the definition for compatibility, but the current extractor and
repository own the concrete worksheet and normalised table mapping.

## Implementation boundary

`HtmlLinkDiscoveryStrategy` and `HtmlLinkResolver` use JSoup and resolve relative
links against the landing-page URI. `OfgemPriceCapWorkbookExtractor` uses Apache
POI and structural labels rather than a fixed cell range.
