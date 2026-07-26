# Dependency Management

**Document ID:** DEV-DEPENDENCY-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

## Policy

Prefer Java 17 APIs when they provide the required behaviour. Add a library when
it materially reduces parser, protocol, pooling or test risk. Record a durable
technology choice in an ADR.

## Current runtime dependencies

| Dependency | Purpose |
|---|---|
| Apache Commons CLI | Command-line parsing |
| Jackson Databind | JSON parsing |
| Apache Commons CSV | CSV parsing |
| Jsoup | Static HTML link discovery |
| Apache POI | XLS/XLSX parsing |
| Apache Commons DBCP | JDBC connection pooling |
| Microsoft JDBC Driver | SQL Server access |
| Log4j-to-JUL bridge | Route dependency Log4j API calls to JUL |

JUnit Jupiter and Mockito are test-scoped.

## Update procedure

1. read the release notes and Java-version requirements;
2. check licence compatibility and known security advisories;
3. update one related dependency family at a time;
4. run unit, dry-run and relevant SQL Server tests;
5. update `pom.xml`, documentation and ADRs together;
6. record the change in `docs/ChangeLog.md`.

Preview dependencies, including the current preview SQL Server JDBC driver, must
be replaced by stable releases before a production baseline unless a specific
ADR accepts the risk.

## Logging constraint

Application code uses `java.util.logging`. A dependency must not cause the
project to adopt another application logging API. Bridges must route dependency
messages into JUL.
