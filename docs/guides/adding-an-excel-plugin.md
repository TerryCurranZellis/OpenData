# Adding an Excel Plugin

**Document ID:** GUIDE-EXCEL-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


Set resolved format `xls`/`xlsx`, inspect sheets/heading rows, configure
`ExcelParserOptions`, add transformations for unusual layouts, test formulas,
dates, percentages, blank rows and missing/duplicate headings.

The shared parser selects one sheet and does not filter hidden rows. A
publisher-specific extractor is appropriate for structurally complex workbooks.
If retention is required, archive through the plugin rather than assuming the
parser performs it.
