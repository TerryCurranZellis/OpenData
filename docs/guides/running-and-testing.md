# Running and Testing

**Document ID:** GUIDE-TEST-001  
**Version:** 2.0  
**Status:** Current  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## Build verification

Run `java -version`, `mvn --version`, `mvn clean test` and `mvn package`. Maven
must compile with release 17. The current package is not a verified executable
fat JAR.

## CLI and registry verification

```text
opendata --help
opendata --plugin all --register
opendata --list-plugins
opendata --plugin octopus --disable
opendata --plugin octopus --enable
```

Use a disposable database to verify unregister/re-register behaviour and that
provider data remains intact.

## Runtime verification

```text
opendata --plugin ofgem --dry-run
opendata --plugin openmeteo --dry-run
opendata --plugin octopus --dry-run
opendata --plugin all --dry-run --parallelism 3
```

Complete controlled write, replay, rollback and permission tests for each plugin.
Mock JDBC tests do not replace SQL Server integration testing.

## Documentation verification

Validate manifests/links, render PlantUML, parse changed Markdown, scan for
secrets/stale claims and confirm ADR registration.
