# Dependency Management

**Document ID:** DEV-DEPENDENCY-001  
**Version:** 3.1.0  
**Status:** Version 3.1.0 modular build baseline  
**Baseline date:** 18 September 2026  
**Minimum Java version:** 24

---

## Policy

Prefer standard Java 24 APIs when they provide the required behaviour. Add a
library when it materially reduces protocol, parser, database, security or test
risk. Record a durable technology choice in an ADR and update third-party
notices when the dependency set changes.

## Module dependency rules

- `opendata-api` must not depend on sibling OpenData modules.
- `opendata-common` may depend on `opendata-api` but not on `opendata-core`.
- `opendata-core` may depend on both `opendata-api` and `opendata-common`.
- Bundled provider plugins remain inside `opendata-core` until they are ready to
  become separate projects.

Keep shared contracts in `opendata-api`, reusable implementation helpers in
`opendata-common`, and application/runtime orchestration in `opendata-core`.

## Current runtime dependencies

| Dependency | Primary module | Purpose |
|---|---|---|
| Apache Commons CLI | `opendata-core` | Command-line parsing |
| Jackson Databind | `opendata-core` | Generic JSON parsing and OpenMeteo response handling |
| Apache Commons CSV | `opendata-core` | Standards-compliant CSV parsing |
| Jsoup | `opendata-common` | Static HTML link discovery support |
| Apache POI OOXML | `opendata-core` | XLS and XLSX workbook parsing |
| Apache Commons DBCP | `opendata-core` | JDBC connection pooling |
| Microsoft JDBC Driver | `opendata-core` | SQL Server access |
| Apache PDFBox | `opendata-core` | Octopus Energy PDF text extraction |
| Log4j-to-JUL bridge | `opendata-core` | Route dependency Log4j API calls into JUL |

JUnit Jupiter and Mockito are test-scoped module dependencies where needed.

## Version ownership

The root `pom.xml` owns the reactor module list and shared build-version
properties. Each module `pom.xml` owns its module-specific runtime dependencies
and declares the common verification plugins needed to validate that module.

## Update procedure

1. read the library release notes and Java-version requirements;
2. review licence compatibility and known security advisories;
3. update one related dependency family at a time;
4. run `mvn clean verify` from the repository root and inspect advisory quality reports;
5. run affected parser, plugin and SQL Server acceptance tests;
6. update the POMs, notices, documentation and ADRs together; and
7. record the change in `CHANGELOG.md`.
