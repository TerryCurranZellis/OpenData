# Running and Testing

**Document ID:** GUIDE-TEST-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


## Build verification

Run `java -version`, `mvn --version`, `mvn clean test` and `mvn package`. Maven
must compile with release 17. The current package is not an executable fat JAR.

## Runtime verification

From NetBeans or another classpath-aware launcher, run:

```text
opendata --list-plugins
opendata --plugin ofgem --dry-run
opendata --plugin openmeteo --dry-run
opendata --plugin all --dry-run --parallelism 2
```

Dry runs verify acquisition and parsing without database or audit writes.

## Database verification

Unit tests for JDBC repositories use mocks; they do not replace:

- clean and repeat SQL script execution;
- write, idempotency and rollback tests;
- application-principal permission checks;
- pool exhaustion and shutdown checks.

## Documentation verification

Run the documentation test, render all PlantUML sources, search for secrets and
stale implementation claims, and confirm that every unique ADR number appears in
the register.
