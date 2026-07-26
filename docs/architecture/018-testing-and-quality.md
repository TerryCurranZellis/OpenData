# Testing and Quality

**Document ID:** ARCH-018  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

## Current automated coverage

The JUnit Jupiter suite covers CLI parsing, configuration values and overrides,
plugin-definition loading, registry and selection behaviour, execution
coordination, download/discovery components, parser adapters, Ofgem workbook
extraction and persistence SQL interactions, and OpenMeteo configuration,
weather-code and repository behaviour.

`HttpDataDownloaderTest` uses a local JDK HTTP server. CSV tests cover quoted
commas and multiline fields. Excel tests cover named sheets, formula evaluation,
blank headings and duplicate headings. Repository tests use mocked JDBC
objects; they are not SQL Server integration tests.

## Coverage still required

- live SQL Server schema, permission, pooling, idempotency and rollback tests;
- end-to-end Ofgem and OpenMeteo dry and write runs;
- retained acceptance evidence for publisher fixtures;
- automated package dependency enforcement;
- documented boundary cases not represented by current parser fixtures.

## Build gates

Compile with Java release 17, pass the JUnit suite, validate registered plugin
definitions, validate documentation links, render PlantUML, inspect DOCX/PDF
layout and scan for tracked secrets. Production acceptance additionally
requires the live checks above.
