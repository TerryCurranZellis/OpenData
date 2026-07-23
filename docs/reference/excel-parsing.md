# Excel Parsing Reference

`ExcelDataParser` uses Apache POI `WorkbookFactory`, allowing the same parser to
open legacy XLS and modern XLSX files.

Defaults:

- first visible worksheet;
- row 0 as headings;
- row 1 as first data row;
- hidden rows skipped;
- formulas evaluated;
- blank rows ignored;
- formatted values trimmed.

A named worksheet and alternative row positions can be supplied through
`ExcelParserOptions`.

Raw source workbooks should be archived before parsing.
