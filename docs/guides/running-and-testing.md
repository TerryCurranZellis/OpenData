# Running and Testing

**Document ID:** GUIDE-TEST-001
**Version:** 2.0
**Status:** Current
**Baseline date:** 3 August 2026
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
opendata --plugin ofgem,openmeteo --dry-run --parallelism 2
```

These Ofgem/OpenMeteo dry runs verify acquisition and parsing without plugin
database or audit writes. Database-backed configuration startup may still need
the bootstrap credential and SQL Server before the dry-run execution boundary is
created.

Do not use Octopus or `all` dry run as the current acceptance test. Octopus
extract still reads the completed-file ledger and fails against the unavailable
dry-run database resource. Validate Octopus with disposable statements, a test
database and a controlled write run until that defect is fixed.

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
