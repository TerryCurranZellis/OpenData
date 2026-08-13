# High-Level Architecture

**Document ID:** ARCH-003  
**Version:** 2.0  
**Status:** Version 2.0.0 implementation baseline  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 24

---

OpenData is a modular monolith: one Maven build and one JVM process with explicit
package ownership and constructor-based composition.

## Principal components

| Component | Responsibility |
|---|---|
| `OpenData` | Entry point, top-level error translation, About/splash and final status logging |
| CLI | Immutable command model, aliases, help and validation |
| Bootstrap/configuration | Bootstrap loading, password decryption and classpath/JDBC property sources |
| `ClasspathPluginRegistry` | Packaged registration catalogue |
| `JdbcPluginRegistry` | Authoritative registered metadata and enabled status |
| Execution coordinator | Bounded concurrency, run context, audit and summaries |
| Plugin pipelines | Provider-specific five-stage workflows |
| Database infrastructure | DBCP pooling, registry/configuration/audit/provider repositories |

## Administration flow

```text
CLI -> bootstrap -> SQLServerResource -> JdbcPluginRegistry
    -> register / list / enable / disable / unregister
```

Registration without `--file` resolves definitions from the packaged catalogue.
Registration with `--file` resolves exactly one complete external definition.
Both forms persist metadata in `core.plugin_registry` and definition properties
in `core.plugin_property`.

## Execution flow

```text
CLI -> bootstrap -> SQL registry/configuration reads
    -> enabled plugin selection -> typed definitions
    -> PluginExecutionCoordinator -> fresh plugin instances
    -> extract/transform/load/finalise -> SQL Server and/or archive
```

::: {.landscape}
![OpenData component architecture](../diagrams/generated/component-architecture.svg){width=22.5cm}
:::
