# Adding a CSV Plugin

**Document ID:** GUIDE-CSV-001  
**Version:** 2.0  
**Status:** Version 2.0.0 developer procedure  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 24

---

## Shared parser

`CsvDataParser` returns `List<Map<String,String>>`. Its defaults are:

- UTF-8;
- comma delimiter;
- first record as headings;
- trimming enabled; and
- empty physical lines ignored.

Use `CsvParserOptions` for a different character set, delimiter, trimming or
empty-line behaviour.

```java
var options = new CsvParserOptions(
        StandardCharsets.UTF_8,
        ';',
        true,
        true);
var rows = new CsvDataParser(options).parse(file);
```

## Plugin responsibilities

The shared parser does not infer types, validate required headings, normalise
publisher values, archive files or write SQL. Those are provider responsibilities
in `transform`, `transform.validate`, `finalise` and `load`.

Validate the complete heading set before processing rows. Preserve source row
numbers in provider records when they are needed for audit or error reporting.

## Tests

Cover quoted delimiters, embedded line breaks, escaped quotes, duplicate or
blank headings, missing required headings, empty datasets, malformed records,
the configured character set and large-file behaviour.

Do not use `String.split` as a substitute for a CSV parser.
