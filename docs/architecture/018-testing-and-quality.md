# Testing and Quality

**Document ID:** ARCH-018  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


Unit tests cover record validation, properties parsing, registry behaviour, HTML
matching, CSV edge cases, Excel cells and transformations. Integration tests use
a local HTTP server, packaged resources and a test database. Contract tests
verify every indexed plugin.

CSV tests include quoted commas, multiline fields, escaped quotes and trailing
blanks. Excel tests include formulas, dates, percentages, hidden/blank rows and
missing/duplicate headings. HTTP tests include redirects, non-2xx, interruption,
partial files and relative links.

Build gates: compile with release 17, tests pass, unique plugin ids, valid docs
links, rendered PlantUML and no tracked secrets. Consolidation on JUnit Jupiter
is recommended.
