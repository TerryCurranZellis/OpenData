# OpenData 4.1.0 module-boundary review

**Documentation baseline:** OpenData 4.0.0  
**Refactoring target:** OpenData 4.1.0  
**Status:** Maven and Java module boundaries refactored on the review branch; final CI and desktop verification pending.

## Architectural rules

Only the following compile-time (and declared Maven) dependencies are permitted between major modules:

| Consumer | Allowed OpenData dependencies | Forbidden OpenData dependencies |
|---|---|---|
| `opendata-api` | None | Core, Common, provider plugins |
| `opendata-common` | API | Core, provider plugins |
| `opendata-core` | API | Common, provider plugins |
| Provider plugins | API, Common, provider-specific support libraries | Core |

Plugin-specific shared implementation modules may depend on API and Common, but never Core. A composition/launcher module may depend on Core, Common and plugin implementation JARs, because it has no reverse dependencies into them.

## Violations in the original 4.0.0 baseline

The original `opendata-core/pom.xml` declared direct compile dependency on `opendata-common` and runtime dependencies on all six provider plugins. `module-info.java` also required `com.towermarsh.opendata.common`.

That is **not compliant** merely because the six plugin dependencies have Maven `runtime` scope. A runtime-scoped dependency is still a declared Core dependency.

Original Core source files also directly imported Common database exception types, `UnavailableDatabaseResourceManager`, `SqlScriptExecutor` and `PluginLogContext`. Removing the Maven dependency without relocating these contracts and implementations breaks the build.

## Migration implemented on the 4.1.0 review branch

1. The shared `com.towermarsh.opendata.exception` hierarchy was moved unchanged by fully-qualified name from Common to API, preserving existing callers in Core and provider plugins.
2. `DatabaseException`, `DatabaseAccessException`, `SqlBatchParser`, validation and formatting helpers, and `PluginLogContext` are now available in API-owned packages.
3. Core now owns its non-writing `UnavailableDatabaseResourceManager`; deprecated Common compatibility classes remain available for plugin migrations.
4. `opendata-core` now declares an internal dependency on API only, and its module descriptor no longer requires Common.
5. New `opendata-launcher` depends on Core, Common and all six provider implementations. Packaged-provider integration tests were relocated from Core into the launcher.
6. The launcher tests module dependencies and forbidden source imports with `ModuleBoundariesTest`. Strict Checkstyle and SpotBugs verification is enabled in CI.

The wiring must also be verified in a GUI desktop session; a passing CI build alone does not prove JavaFX startup or installed SQL Server behaviour.  The `ClasspathPluginRegistry`, `PluginRegistrarLocator` and `ReflectionPluginFactory` currently load provider metadata/classes using class-loader resources or reflection; the new launcher must preserve this behaviour.

## Acceptance criteria

The 4.1.0 refactor is complete only when:

- `mvn -Dquality.failOnViolation=true clean verify` passes with JDK 24 or a compatible later JDK, including Checkstyle, SpotBugs and tests.
- No forbidden dependency exists in any POM, module descriptor or source import.
- Installation starts the first-run wizard with `init=true`, stores the AES key path through the normal properties loader, and registers plugins disabled.
- A normal application launch finds all six provider plugins; execution and dry-run continue to function.
- Javadoc includes the author and changed-code `@version 4.1.0`; prior version tags are kept as `@since` when appropriate.
- Current manuals, PlantUML and release documentation refer to the as-shipped module layout.

## Build toolchain compatibility discovered during review

The 4.0.0 Core POM declared JavaFX 27 while the full reactor targets Java 24. JavaFX 27 requires JDK 25 or later. The review branch corrects the JavaFX dependency to **26.0.2** for Java 24 compatibility. GitHub Actions uses the JDK 24 toolchain for compilation and quality verification.

## Documentation and screenshot assets

- [Updated GUI screenshot plan](../development/gui-screenshot-plan.md): 20 mandatory actual-application screenshots, including the first-run setup workflow, and one optional dry-run screenshot.
- [First-run user guide](../guides/first-run-initialisation.md).
- [First-run sequence source](../diagrams/source/first-run-initialisation-sequence.puml).

Older release notes and accepted ADRs are historical records, not current instructions. Preserve such material until references and provenance have been checked; do not simply relabel historical 3.x implementation notes as 4.0.0.
