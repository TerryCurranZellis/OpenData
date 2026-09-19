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
- `opendata-plugins` may depend on `opendata-api` and `opendata-common` only,
  plus required Java/external libraries.
- `opendata-core` may depend on `opendata-api` and `opendata-common` only.

Keep shared contracts in `opendata-api`, reusable implementation helpers in
`opendata-common`, bundled provider code in `opendata-plugins`, and
application/runtime orchestration, registry, and execution infrastructure in
`opendata-core`.

## Current runtime dependencies

| Dependency | Primary module | Purpose |
|---|---|---|
| Apache Commons CLI | `opendata-core` | Command-line parsing |
| Jackson Databind | `opendata-plugins` | Generic JSON parsing and OpenMeteo response handling |
| Apache Commons CSV | `opendata-core` | Standards-compliant CSV parsing |
| Jsoup | `opendata-common` | Static HTML link discovery support |
| Apache POI OOXML | `opendata-plugins` | XLS and XLSX workbook parsing |
| Apache Commons DBCP | `opendata-core` | JDBC connection pooling |
| Microsoft JDBC Driver | `opendata-core` | SQL Server access |
| Apache PDFBox | `opendata-plugins` | Octopus Energy PDF text extraction |
| Log4j-to-JUL bridge | `opendata-core` | Route dependency Log4j API calls into JUL |

JUnit Jupiter and Mockito are test-scoped module dependencies where needed.

## Version ownership

The root `pom.xml` owns the reactor module list, shared dependency versions, and
shared plugin versions/configuration through `dependencyManagement` and
`pluginManagement`. Each module `pom.xml` owns only its module-specific
dependency list and the plugin declarations that activate the inherited
verification baseline for that module.

## Update procedure

1. read the library release notes and Java-version requirements;
2. review licence compatibility and known security advisories;
3. update one related dependency family at a time;
4. run `mvn clean verify` from the repository root and inspect advisory quality reports;
5. run affected parser, plugin and SQL Server acceptance tests;
6. update the POMs, notices, documentation and ADRs together; and
7. record the change in `CHANGELOG.md`.
