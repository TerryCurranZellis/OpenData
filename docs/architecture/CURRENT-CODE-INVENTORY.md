# Current Code Inventory

**Document ID:** ARCH-INVENTORY-001  
**Version:** 1.3  
**Status:** Current baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

Reviewed against commit `c2adae5` plus the current documentation/refactoring
branch and its Java, resource and SQL files.

| Area | Current classes/contracts | Status |
|---|---|---|
| Bootstrap | `Main`, `OpenDataApplication`, `ExecutionStatus` | Implemented |
| CLI | `CommandLineArguments`, `CommandLineArgumentsProcessor`, `PluginSelectionResolver` | Implemented, including multi-plugin selection and dry run |
| Configuration | Runtime, override and plugin-definition loaders/records | Implemented |
| Download and discovery | JDK HTTP, Jsoup discovery and strategies | Implemented |
| Parsing | Shared CSV/JSON/Excel adapters plus plugin-local extractors | Implemented |
| Validation | Shared contracts plus Ofgem/OpenMeteo provider validators | Implemented |
| ETL | `ExtractService`, `TransformService`, `LoadService` | Reusable contracts; plugins currently own their concrete flow |
| Execution | `PluginExecutionCoordinator`, fixed executor, aggregate summary | Implemented |
| Persistence | DBCP resource manager, JDBC repositories and SQL scripts | Implemented for SQL Server |
| Models | Dataset, source, file, and import result models | Present |
| Logging | `LoggingManager`, formatter and `PluginLogContext` | Implemented for concurrent tasks |
| Exceptions | Framework exception hierarchy | Present |
| Plugin registry | Classpath index, descriptors and reflection factory | Implemented |
| Ofgem execution | Plugin-local config/download/extract/transform/load pipeline | Implemented; live write acceptance outstanding |
| OpenMeteo execution | Plugin-local config/download/extract/transform/load pipeline | Implemented; live write acceptance outstanding |
| Audit | `core.PluginRun` plus Ofgem `core.ingestion_run` provenance | Implemented but duplicated |
| Scheduling | External scheduling recommended | Internal scheduler deferred |

## Important consistency notes

- Maven compilation uses `release=17`.
- Maven project version is `2.0.0`.
- `--version` prints the JAR implementation version when a manifest supplies it;
  otherwise it prints `development`.
- The POM does not yet create an executable JAR or bundle runtime dependencies.
- `Main` deliberately does not call `System.exit`; the logged `ExecutionStatus`
  description is authoritative inside the current process, but the
  operating-system exit code is not yet mapped.
- the CLI record under `cli` and `ExecutionStatus` are the only current
  command-line/status models; their unused `app` predecessors were removed;
- provider-specific classes are owned entirely by `plugin.ofgem` or
  `plugin.openmeteo`; Octopus follows the same boundary but still has placeholder
  extract/load/finalise steps; the root provider class is the workflow facade;
- `ApplicationRuntimeConfiguration` reads
  `src/main/resources/config/application.properties`.
- The active database path uses `DatabasePoolConfiguration`,
  `DatabaseResourceManager` and plugin persistence repositories. Older
  `DatabasePoolConfig`, `DatabaseConnectionManager` and generic repository
  classes remain and should not be mistaken for the runtime composition.
- Ofgem creates both a generic `core.PluginRun` row and its own
  `core.ingestion_run` row. OpenMeteo uses `core.PluginRun` as its lineage key.
- Production readiness must not be claimed until the verification gates in the
  [roadmap](../roadmap/README.md) are complete.
