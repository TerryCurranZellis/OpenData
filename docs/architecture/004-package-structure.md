# Package Structure

**Document ID:** ARCH-004  
**Version:** 4.0.0  
**Status:** 4.1.0 dependency-boundary source layout  
**Baseline date:** 10 November 2026  
**Minimum Java version:** 24

---

## Canonical ownership

| Module | Package / area | Responsibility |
|---|---|---|
| `opendata-api` | `config.model` | Immutable endpoint, property and configuration contracts |
| `opendata-api` | `database` | JDBC resource contract, database exceptions and `SqlBatchParser` |
| `opendata-api` | `exception` | Shared checked application, plugin and validation exceptions |
| `opendata-api` | `foundation` | Validation rules, duration formatting, cause messages and discovery normalisation |
| `opendata-api` | `plugin` | `OpenDataPlugin`, `PluginRegistrar`, descriptor, metrics, context and log scope |
| `opendata-common` | `common.database`, `common.database.jdbc` | Reusable provider database helpers and compatibility adapters |
| `opendata-common` | `download.strategy`, `util`, `validation`, `common.logging` | Provider-oriented parsing, download, validation and compatibility helpers |
| `opendata-core` | `app`, `cli`, root entry point | Application entry and CLI policy |
| `opendata-core` | `config`, `core.database`, `core.plugin` | Runtime configuration, SQL Server and execution infrastructure |
| `opendata-core` | `setup`, `gui` | First-run provisioning and JavaFX presentation |
| `opendata-core` | `download`, `discovery`, `parser`, `etl`, `core.logging` | Provider-neutral processing and runtime logging |
| `opendata-plugins/opendata-plugin-<id>` | `plugin.<provider>` | Provider five-stage pipeline and `RegisterPlugin` |
| `opendata-plugin-electricity-common` | `plugin.neso` | Shared NESO CKAN/CSV helpers |
| `opendata-plugin-octopus-common` | `plugin.octopus` | Shared statement extraction, models and parsing |
| `opendata-launcher` | `pom.xml`, integration test packages | Provider classpath assembly; no core implementation code duplication |

## Package documentation

Every Java production package, including provider implementation and sample
packages, has a `package-info.java` describing its compiled classes,
interfaces, enums and records with Javadoc `{@link Type}` references.
A Maven regression test checks author/version tags and package file coverage.

Do not relocate a class across a module boundary without updating its Javadoc,
POM and module descriptor, package inventory, and corresponding tests.
