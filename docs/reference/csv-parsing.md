# CSV Parsing Reference

**Document ID:** REF-CSV-001  
**Version:** 2.0  
**Status:** Implemented  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 24

---

`CsvDataParser` uses Apache Commons CSV and implements `DataParser`.

## Defaults

| Option | Default |
|---|---|
| Character set | UTF-8 |
| Delimiter | comma |
| Header | first record |
| Trim | enabled |
| Ignore empty lines | enabled |

The result is an immutable `List<Map<String,String>>` preserving parser header
order. Missing mapped values are returned as an empty string.

Quoted delimiters, escaped quotes and multiline fields are handled by Commons
CSV. The parser does not infer types, validate a provider schema, preserve a
source row number, archive the file or write to a database.

`CsvParserOptions` rejects newline, carriage-return and NUL delimiters.
Parsing I/O and format failures are wrapped in `ImportException`.
