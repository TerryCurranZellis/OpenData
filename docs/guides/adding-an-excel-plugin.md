# Adding an Excel Plugin

**Document ID:** GUIDE-EXCEL-001  
**Version:** 3.0.0  
**Status:** Version 3.0.0 developer procedure  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Shared parser

`ExcelDataParser` supports XLS and XLSX through Apache POI. Default
`ExcelParserOptions` select sheet index `0`, header row `0`, first data row `1`,
formula evaluation enabled and completely blank rows omitted.

```java
var options = new ExcelParserOptions(
        "Data",
        0,
        4,
        5,
        true,
        true);
var rows = new ExcelDataParser(options).parse(file);
```

A non-blank sheet name takes precedence over sheet index. Cell values are
formatted as display text using the UK locale. Blank headings become
`COLUMN_<n>` and duplicate headings gain a numeric suffix.

## When to write a provider extractor

Use a provider-specific extractor for multi-row headings, merged cells, multiple
related sheets, repeated sections, hidden semantic columns, complex formulas or
workbooks whose layout changes by reporting period. The Ofgem workbook is an
example of source-specific extraction rather than a simple generic table.

## Tests

Cover sheet selection, missing sheets, heading/data row indexes, formulas,
dates, percentages, blank rows, duplicate headings, corrupt files, merged cells
when relevant and representative publisher workbooks.

The parser does not archive source files. Retention and archive behaviour belongs
to the plugin lifecycle.
