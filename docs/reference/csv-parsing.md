# CSV Parsing Reference

`CsvDataParser` uses Apache Commons CSV.

The default parser:

- reads UTF-8;
- uses comma delimiters;
- uses double-quote quoting;
- obtains headers from the first record;
- rejects duplicate and missing header names;
- ignores empty lines;
- preserves embedded line breaks and quoted delimiters.

Custom behaviour is supplied through `CsvParserOptions`.

The current return type remains `List<Map<String,String>>` for compatibility
with the existing ETL code.
