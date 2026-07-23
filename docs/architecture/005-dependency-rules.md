# Dependency Rules

**Document ID:** ARCH-005  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## Permitted direction

`app` may coordinate CLI, configuration, plugins and ETL. Source plugin packages
may use framework contracts and parsers. ETL may call downloader, parser,
validation and repository contracts. Infrastructure implementations depend on
framework values, not the reverse.

## Prohibited dependencies

- `Main` must not depend on Ofgem or OpenMeteo implementation classes.
- parsers must not depend on plugin packages;
- repositories must not initiate downloads;
- records must not open network/file/database resources;
- plugins must not parse CLI options or log secrets;
- validation must not silently mutate source rows.

## Approved specialist libraries

Commons CLI, Commons CSV, Jackson, JSoup, Apache POI and Microsoft JDBC.

A future ArchUnit suite should enforce package rules.
