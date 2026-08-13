# Excel Parsing Reference

**Document ID:** REF-EXCEL-001  
**Version:** 2.0  
**Status:** Implemented  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 24

---

`ExcelDataParser` uses Apache POI `WorkbookFactory` and supports XLS and XLSX.

## `ExcelParserOptions`

| Setting | Default |
|---|---|
| Sheet name | blank |
| Sheet index | `0` |
| Header row | `0` |
| First data row | `1` |
| Evaluate formulas | `true` |
| Skip completely blank rows | `true` |

A non-blank sheet name takes precedence. Row and sheet indexes are zero-based,
and the first data row must be after the header row.

Cells are returned as UK-locale display text. Blank headings become
`COLUMN_<n>`. Repeated heading text receives `_2`, `_3` and later suffixes.
Rows become `Map<String,String>` values.

The generic parser does not interpret provider units, dates or identifiers,
filter hidden rows, join sheets, preserve formula expressions, archive files or
perform database work. Complex publisher workbooks require a provider-specific
extractor.
