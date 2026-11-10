# OpenData 4.1.0 module-boundary review

**Documentation baseline:** OpenData 4.0.0  
**Refactoring target:** OpenData 4.1.0  
**Status:** Partial source clean-up committed; architectural migration **not complete**.

## Architectural rules

Only the following compile-time (and declared Maven) dependencies are permitted between major modules:

| Consumer | Allowed OpenData dependencies | Forbidden OpenData dependencies |
|---|---|---|
| `opendata-api` | None | Core, Common, provider plugins |
| `opendata-common` | API | Core, provider plugins |
| `opendata-core` | API | Common, provider plugins |
| Provider plugins | API, Common, provider-specific support libraries | Core |

Plugin-specific shared implementation modules may depend on API and Common, but never Core. A composition/launcher module may depend on Core, Common and plugin implementation JARs, because it has no reverse dependencies into them.

## Current 4.0.0 violations

The current `opendata-core/pom.xml` declares direct compile dependency on `opendata-common` and runtime dependencies on all six provider plugins. `module-info.java` also requires `com.towermarsh.opendata.common`.

That is **not compliant** merely because the six plugin dependencies have Maven `runtime` scope. A runtime-scoped dependency is still a declared Core dependency.

Core source files also directly import Common database exception types, `UnavailableDatabaseResourceManager`, `SqlScriptExecutor` and `PluginLogContext`. Removing the Maven dependency without relocating these contracts and implementations breaks the build.

## Proposed migration, subject to compilation and test verification

1. Move shared exception contracts required by Core and plugins into API, keeping backward-compatible adapters in Common until consumers can migrate.
2. Keep the dry-run unavailable database implementation within Core's execution infrastructure, not in the Common abstraction package.
3. Put Core-specific logging scope state and SQL Server initialisation batch handling in a layer reachable without an illegal Core -> Common dependency; preserve log context across plugin worker threads.
4. Remove provider runtime dependencies from Core's POM. Introduce a dedicated executable composition/launcher module that depends on Core and provider implementations and packages their classpath registration resources.
5. Verify the Java Platform Module System descriptor, Maven dependency graph, resource discovery, reflection-based factories, first-run plugin registration, CLI, GUI and dry-run behaviour.
6. Add automated dependency-boundary tests before enabling the new module layout as a release baseline.

Do not remove dependencies piecemeal without their replacement wiring. The `ClasspathPluginRegistry`, `PluginRegistrarLocator` and `ReflectionPluginFactory` currently load provider metadata/classes using class-loader resources or reflection; the new launcher must preserve this behaviour.

## Acceptance criteria

The 4.1.0 refactor is complete only when:

- `mvn -Dquality.failOnViolation=true clean verify` passes with JDK 24 or a compatible later JDK, including Checkstyle, SpotBugs and tests.
- No forbidden dependency exists in any POM, module descriptor or source import.
- Installation starts the first-run wizard with `init=true`, stores the AES key path through the normal properties loader, and registers plugins disabled.
- A normal application launch finds all six provider plugins; execution and dry-run continue to function.
- Javadoc includes the author and changed-code `@version 4.1.0`; prior version tags are kept as `@since` when appropriate.
- Current manuals, PlantUML and release documentation refer to the as-shipped module layout.

## Build toolchain compatibility discovered during review

The 4.0.0 Core POM declared JavaFX 27 while the full reactor targets Java 24. JavaFX 27 requires JDK 25 or later. The review branch corrects the JavaFX dependency to **26.0.2** for Java 24 compatibility. A GitHub Actions quality build verifies the updated configuration separately.

## Documentation and screenshot assets

- [Updated GUI screenshot plan](../development/gui-screenshot-plan.md): 20 mandatory actual-application screenshots, including the first-run setup workflow, and one optional dry-run screenshot.
- [First-run user guide](../guides/first-run-initialisation.md).
- [First-run sequence source](../diagrams/source/first-run-initialisation-sequence.puml).

Older release notes and accepted ADRs are historical records, not current instructions. Preserve such material until references and provenance have been checked; do not simply relabel historical 3.x implementation notes as 4.0.0.
