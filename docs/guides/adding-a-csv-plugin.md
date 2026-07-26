# Adding a CSV Plugin

**Document ID:** GUIDE-CSV-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


Set the resolved format to `csv`, configure the download strategy and construct
`CsvParserOptions` when defaults are insufficient. Validate required headings
and convert strings into plugin-specific types outside the shared parser.

Tests should cover quoted delimiters, multiline fields, escaped quotes, missing
headings, empty lines and the configured character set. If source retention is
required, make archiving an explicit plugin step; `CsvDataParser` does not
archive files.
