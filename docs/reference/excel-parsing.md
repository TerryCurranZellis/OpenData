# Excel Parsing Reference

**Document ID:** REF-EXCEL-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


Apache POI `WorkbookFactory` handles XLS/XLSX. `ExcelParserOptions` selects a
worksheet by name or zero-based index, the header and first data rows, formula
evaluation and whether completely blank rows are omitted.

`DataFormatter` with the UK locale supplies display strings. Blank headings are
named `COLUMN_<n>` and duplicate headings receive a numeric suffix. The generic
parser does not archive files; the Ofgem plugin archives its downloaded workbook
in write mode when configured to do so.
