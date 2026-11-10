# OpenData 4.1.0 source review — verified findings and outstanding work

**Baseline:** `main`, commit `613767c811685905265d0574f9948789b60e175c` (8 November 2026).  
**Status:** Review in progress. This record is not evidence of a passing build or a completed refactor.

## Verified code-level findings

- `opendata-api/pom.xml` has no OpenData module dependency; `module-info.java` requires only the JDK SQL module.
- `opendata-common/pom.xml` depends on API, not Core or provider plugins.
- `opendata-core/pom.xml` depends on API and Common at compile scope, and lists the six provider plugin JARs at runtime scope. This violates the requested rule even though source imports of plugin implementation packages were not found.
- `opendata-core/src/main/java/module-info.java` explicitly requires Common.
- At least fourteen Core production Java files import Common classes, including `DatabaseException`, `DatabaseAccessException`, `UnavailableDatabaseResourceManager`, `SqlScriptExecutor` and `PluginLogContext`. Removing the POM dependency without relocating or abstracting these types would break compilation.
- Source search found no production provider-plugin imports of `com.towermarsh.opendata.core.*`. This should be verified with a full source and bytecode dependency scan before declaring the dependency rule fully met.
- The `docs/development/gui-screenshot-plan.md` document was based on GUI 3.0.0 and lacked first-run setup images. Its replacement includes 21 required 4.0.0 captures.
- `docs/diagrams/source/gui-application-flow.puml` described GUI 3.0.0 and omitted the first-run gate.

## Required design before code changes

1. Extract genuinely shared exceptions/context to API contracts, or define API abstractions implemented independently by Core and Common. Avoid duplicate unrelated `ThreadLocal` logging contexts.
2. Keep SQL provisioning helpers in Core when no plugin should use them; retain reusable plugin-specific SQL helpers in Common.
3. Move plugin-JAR runtime composition to a separate distribution/launcher module. The Core POM and `module-info` must have no Common or provider-plugin dependencies, at any scope.
4. Confirm plugin discovery, registration DDL, execution, logs, CLI and setup on a complete distribution, not just the Core JAR.
5. Review `final` declarations individually. Preserve `static final` constants and immutability guarantees. Only change inferred local declarations to `var` where the initializer communicates the type clearly and where it is legal in Java 24.
6. Preserve existing historical `@since` provenance when updating `@version 4.1.0` on changed Java types; add `@author Terry Curran` where absent. Do not mark active compatibility APIs deprecated without a usable replacement and a documented migration.
7. Review `package-info.java` type inventories against source packages. Retain historical release records and accepted ADRs as archival material; only remove truly redundant current material after checking incoming references.

## Validation gate

A completed implementation requires a green full-reactor `mvn clean verify` on JDK 24+, a generated-Javadoc check, explicit dependency checks, plugin-discovery integration tests, GUI smoke tests and first-run wizard verification against an expendable SQL Server. These checks **have not been executed** during this GitHub-connected review.
