# CSV Parsing Reference

**Document ID:** REF-CSV-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


Commons CSV defaults to UTF-8, comma, double-quote, first-record headings,
duplicate headings disallowed and empty lines ignored. Quoted delimiters,
embedded line breaks, escaped quotes and trailing blanks are supported.
`String.split` is not used for general CSV.
