# High-Level Architecture

**Document ID:** ARCH-003  
**Version:** 2.0  
**Status:** Version 2.0.0 implementation baseline  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## Architectural style

OpenData is a modular monolith: one Maven build and one JVM process with explicit
package ownership. The application composes services manually and uses
constructor injection rather than a dependency-injection framework.

## Principal components

| Component | Responsibility |
|---|---|
| `OpenData` | Process entry point, top-level error translation, final status and duration logging |
| CLI | Immutable command arguments, help and validation |
| Bootstrap/configuration | Bootstrap loading, RSA password decryption, classpath/JDBC property sources and overrides |
| Plugin registry/factory | Installed plugin descriptors and fresh plugin construction |
| Execution coordinator | Bounded concurrency, run context, audit, failure isolation and summary aggregation |
| Plugin pipelines | Provider-specific initialise, extract, transform, load and finalise work |
| Shared acquisition/parsers | HTTP, HTML discovery, CSV, JSON and Excel adapters |
| Database infrastructure | Apache DBCP pooling, JDBC resources, audit and plugin repositories |
| Logging | JUL configuration and plugin/run context |

## Registration control flow

```text
OpenData -> CLI -> OpenDataApplication
         -> bootstrap file + RSA decrypt
         -> SQLServerResource
         -> ConfigurationRegistrationService
         -> core.application_property + core.plugin_property
         -> rewrite bootstrap with encrypted password and database mode enabled
```

## Execution control flow

```text
OpenData -> CLI -> OpenDataApplication
         -> bootstrap file
         -> classpath or JDBC property source
         -> plugin selection and definitions
         -> PluginExecutionCoordinator
         -> ReflectionPluginFactory
         -> OpenDataPlugin
         -> plugin-owned extract/transform/load/finalise
         -> SQL Server and/or file archive
```

The application layer owns configuration-source selection, plugin selection,
parallelism, database lifecycle and aggregate status. Each plugin owns its
source-specific workflow. JDBC repositories own SQL and transaction boundaries.
The generic `ExtractService`, `TransformService` and `LoadService` interfaces are
reusable contracts; they are not assembled by a generic runtime pipeline engine.

![OpenData component architecture](../diagrams/generated/component-architecture.svg){width=16cm}
