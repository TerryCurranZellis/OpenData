# Testing and Quality

**Document ID:** ARCH-018  
**Version:** 4.0.0  
**Status:** Strict Java 24 quality verification, 4.1.0 review branch  
**Baseline date:** 10 November 2026  
**Minimum Java version:** 24

---

## Automated Maven verification

The Maven reactor compiles with `release=24` and verifies API, Common,
provider-support projects, six provider plugins, Core and the launcher.

Unit and local integration tests reside with their owner module. Tests requiring
the **combined packaged provider classpath** live in `opendata-launcher`,
not Core. `ModuleBoundariesTest` protects the Maven, Java import and JPMS
boundaries. `JavadocInventoryTest` checks author/version Javadocs and the
presence of package inventories across compiled, test and example source.

## Quality gates

| Check | Implementation | Build outcome |
|---|---|---|
| Java compilation | Maven Compiler Plugin (`release=24`) | Fail on errors |
| Unit and integration tests | JUnit/Surefire | Fail on test errors |
| Checkstyle | Repository `config/quality/checkstyle.xml` | Fail on violations |
| SpotBugs | Maximum effort, medium threshold | Fail on reported bugs |
| Maven dependency analysis | Dependency Plugin | Fail on unresolved warnings |
| JaCoCo | Coverage reports | Report only |
| Javadoc | Maven Javadoc Plugin | Generate per-module API docs as a separate CI step |

The branch sets `quality.failOnViolation=true` by default. GitHub Actions
also explicitly requires it, installs reactor artifacts and generates per-module Javadocs after Maven
verification, and uploads module reports.

```powershell
mvn -Dquality.failOnViolation=true clean verify
```

GitHub Actions exercises JDK 24 and JavaFX 26.0.2. Desktop UI, live SQL
Server provisioning, provider API connectivity and document screenshot
captures still require environment-specific validation.

## Plugin execution and audit regression coverage

Core tests verify bounded concurrent execution, result ordering, failure isolation
and interruption propagation to active worker threads. The caller's interrupted
status must be preserved, and non-positive executor shutdown timeouts are rejected.

Further tests exercise the `PluginRunAudit` lifecycle: a live run is recorded,
a dry run never writes audit rows, and an audit-completion failure does not
produce a successful result. JDBC audit tests verify one-row INSERT/UPDATE
expectations, UTC timestamps, persisted metrics, error-length limits and
missing-row failure handling. These are isolated mock-JDBC tests; real SQL
Server behaviour still needs integration verification.
