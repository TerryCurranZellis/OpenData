# Current Code Inventory

**Document ID:** ARCH-INVENTORY-001
**Version:** 2.1
**Status:** Version 2.0.0 processing-refactor baseline
**Baseline date:** 4 August 2026
**Minimum Java version:** 24

This inventory reflects the repository baseline plus accepted local refactor
Batches 0–3.

| Area | Current classes/contracts | Status |
|---|---|---|
| Bootstrap | `OpenData`, `OpenDataApplication`, `ExecutionStatus` | Implemented |
| CLI | command arguments, processor and selection resolver | Implemented |
| Configuration sources | classpath/JDBC sources and registration service | Implemented |
| Shared property processing | `PluginPropertyValues`, `ValueParser` | Implemented; used by Ofgem, OpenMeteo and Octopus |
| Shared validation | `ValidationRules`, `SqlIdentifiers`, validation result contracts | Implemented |
| Download/discovery | JDK HTTP, Jsoup discovery and strategies | Implemented |
| Parsing | shared CSV/JSON/Excel plus provider PDF/Excel/JSON parsing | Implemented |
| ETL contracts | extract/transform/load contracts and provider stage packages | Implemented |
| Shared JDBC execution | transaction template, cleanup callback, batch executor and typed upsert executor | Implemented |
| Connection pooling | DBCP-backed `DatabaseResourceManager` | Implemented for SQL Server |
| Ofgem | shared typed configuration, HTML/XLSX processing and transactional period replacement | Implemented; live acceptance outstanding |
| OpenMeteo | shared typed configuration, safe identifiers, staged set-based load and pooled-session cleanup | Implemented; live acceptance outstanding |
| Octopus | shared path parsing, local PDF processing and generic typed electricity/gas upserts | Implemented; live acceptance outstanding |
| Logging | manager, formatter and plugin log context | Implemented |
| Plugin registry | packaged catalogue, persistent registry and reflection factory | Implemented |
| Audit | `core.PluginRun` plus Ofgem ingestion/source provenance | Implemented but duplicated identity remains |
| Scheduling | external scheduling | Internal scheduler deferred |

## Compatibility and deprecation

- amended public refactor APIs are marked `@since 2.0.0`;
- `OpenMeteoConfiguration.sqlIdentifier(...)` is retained temporarily and marked
  with Java `@Deprecated` plus Javadoc `@deprecated`;
- removed provider-private parsing helpers were not retained as dead wrappers;
- provider SQL remains explicit and provider-owned.

## Consistency notes

- Java 24 is the minimum build/runtime baseline; the version 3 GUI implementation is in progress;
- dry-run behaviour and public plugin workflow are unchanged by the refactor;
- the shared JDBC layer owns mechanics, not provider business keys or SQL;
- temporary-table/session cleanup is explicit for pooled OpenMeteo connections;
- the plugin template now demonstrates shared property validation and
  transaction/batch infrastructure;
- production readiness still requires live SQL, permissions, rollback,
  packaging, certificate and tracked-secret remediation evidence.
