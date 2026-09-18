# Current Code Inventory

**Document ID:** ARCH-INVENTORY-001
**Version:** 3.1.0  
**Status:** Version 3.1.0 modular build baseline
**Baseline date:** 18 September 2026  
**Minimum Java version:** 24

This inventory reflects the current three-module reactor source tree.

## Module inventory

| Module | Current classes/contracts | Status |
|---|---|---|
| `opendata-api` | plugin contracts, plugin execution records, immutable plugin/configuration records, database resource interfaces | Implemented |
| `opendata-common` | shared validation, JDBC helpers, shared base exceptions, HTML link resolution support | Implemented |
| `opendata-core` | application entry points, JavaFX GUI, CLI, bootstrap/configuration, registry/runtime orchestration, downloads, parsers, audit, bundled plugins | Implemented |

## Functional inventory

| Area | Current classes/contracts | Status |
|---|---|---|
| Bootstrap | `OpenData`, `OpenDataApplication`, `ApplicationInfo`, `ExecutionStatus` | Implemented |
| JavaFX GUI | launcher/application, splash, FXML controller, plugin table, administration/detail/settings/log/help/about/execution services | Implemented |
| CLI | command arguments, processor and selection resolver | Implemented |
| Configuration sources | classpath/JDBC sources and registration service | Implemented |
| Shared property processing | `PluginPropertyValues`, `ValueParser` | Implemented in `opendata-common`; used by Ofgem, OpenMeteo and Octopus |
| Shared validation | `ValidationRules`, `SqlIdentifiers` | Implemented in `opendata-common` |
| Download/discovery | JDK HTTP, Jsoup discovery and strategies | Implemented across `opendata-core` and `opendata-common` |
| Shared JDBC execution | transaction template, cleanup callback, batch executor and typed upsert executor | Implemented in `opendata-common` |
| Connection pooling | DBCP-backed SQL Server resource and pool snapshot contracts | Implemented across `opendata-core` and `opendata-api` |
| Plugin registry | packaged catalogue, persistent registry, execution coordinator and reflection factory | Implemented in `opendata-core` |
| Bundled plugins | Ofgem, OpenMeteo, Octopus, Octopus Adjustment | Implemented in `opendata-core`; future project extraction deferred |
