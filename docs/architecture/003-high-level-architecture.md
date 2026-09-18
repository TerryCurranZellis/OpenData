# High-Level Architecture

**Document ID:** ARCH-003  
**Version:** 3.1.0  
**Status:** Version 3.1.0 modular build baseline  
**Baseline date:** 18 September 2026  
**Minimum Java version:** 24

---

OpenData remains a modular monolith: one Maven reactor build, one JVM process,
and explicit module and package ownership.

## Reactor modules

| Module | Responsibility |
|---|---|
| `opendata-api` | Shared plugin contracts, immutable configuration records, and database resource interfaces |
| `opendata-common` | Shared validation, JDBC helpers, shared exceptions, utility helpers, logging context, and reusable support strategies |
| `opendata-plugins` | Bundled plugin registry infrastructure, packaged plugin definitions, and provider implementations |
| `opendata-core` | Application entry points, runtime orchestration, SQL Server resources, CLI, and JavaFX GUI |

## Principal components

| Component | Responsibility |
|---|---|
| `OpenData` | Entry point, top-level error translation, About/splash and final status logging |
| JavaFX GUI | Desktop presentation, plugin administration, information dialogs and live execution logging |
| CLI | Immutable command model, aliases, help and validation |
| Bootstrap/configuration | Bootstrap loading, password decryption and classpath/JDBC property sources |
| `ClasspathPluginRegistry` | Packaged registration catalogue |
| `JdbcPluginRegistry` | Authoritative registered metadata and enabled status |
| Execution coordinator | Bounded concurrency, run context, audit and summaries |
| Plugin pipelines | Provider-specific five-stage workflows |
| Database infrastructure | DBCP pooling, registry/configuration/audit/provider repositories |

## Deployment boundary

The Maven split does not create separate runtime processes or release trains.
`opendata-core` assembles the executable application and depends on the shared
contracts and helpers supplied by `opendata-api`, `opendata-common`, and the
bundled provider implementations supplied by `opendata-plugins`.
