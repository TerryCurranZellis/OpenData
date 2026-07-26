# CSV Parsing Reference

**Document ID:** REF-CSV-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


`CsvDataParser` uses Apache Commons CSV with UTF-8, comma delimiter,
first-record headings, trimmed fields and ignored empty lines by default.
`CsvParserOptions` can change the character set, delimiter, trimming and
empty-line behaviour.

Quoted delimiters, embedded line breaks and escaped quotes are handled by
Commons CSV. The parser returns `List<Map<String,String>>`; it does not perform
schema inference or typed conversion. `String.split` is not used for general
CSV.
