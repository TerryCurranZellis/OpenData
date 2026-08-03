# Dependency Management

**Document ID:** DEV-DEPENDENCY-001  
**Version:** 2.0  
**Status:** Version 2.0.0 implementation baseline  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## Policy

Prefer Java 17 APIs when they provide the required behaviour. Add a library when
it materially reduces protocol, parser, database, security or test risk. Record
a durable technology choice in an ADR and update third-party notices when the
dependency set changes.

## Current runtime dependencies

| Dependency | Purpose |
|---|---|
| Apache Commons CLI | Command-line parsing |
| Jackson Databind | Generic JSON parsing and OpenMeteo response handling |
| Apache Commons CSV | Standards-compliant CSV parsing |
| Jsoup | Static HTML link discovery |
| Apache POI OOXML | XLS and XLSX workbook parsing |
| Apache Commons DBCP | JDBC connection pooling |
| Microsoft JDBC Driver | SQL Server access |
| Apache PDFBox | Octopus Energy PDF text extraction |
| Log4j-to-JUL bridge | Route dependency Log4j API calls into JUL |

JUnit Jupiter, Mockito and Mockito's JUnit integration are test-scoped.

## Version ownership

Dependency and plugin versions are declared in `pom.xml`. Do not duplicate an
operational dependency version in documentation unless a release record needs an
immutable historical value. The POM is the authoritative current version source.

## Update procedure

1. read the library release notes and Java-version requirements;
2. review licence compatibility and known security advisories;
3. update one related dependency family at a time;
4. run `mvn clean verify` and inspect advisory quality reports;
5. run affected parser, plugin and SQL Server acceptance tests;
6. update the POM, notices, documentation and ADRs together; and
7. record the change in `CHANGELOG.md`.

## Release concerns

The current SQL Server JDBC dependency is a preview build. A production release
must either replace it with a stable compatible driver or explicitly accept the
risk in release evidence and an ADR.

Application code uses `java.util.logging`. Adding a library must not silently
introduce another application logging API. When a dependency uses the Log4j API,
the existing bridge routes those messages into JUL.

## Dependency analysis caveat

Maven dependency analysis is bound to `verify`, but warnings only fail when
`quality.failOnViolation=true`. Review ordinary build output even when the build
returns success.
