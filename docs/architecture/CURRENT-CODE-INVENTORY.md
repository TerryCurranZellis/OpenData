# Current Code Inventory

**Reviewed:** 24 July 2026 against the uploaded source archive.

| Area | Current classes/contracts | Status |
|---|---|---|
| Application boundary | `Main`, `OpenDataApplication`, `ExecutionStatus` | Implemented |
| CLI | `cli.CommandLineArguments`, processor and exception | Multi-plugin selection, `all`, parallelism and informational commands implemented |
| Runtime configuration | `ApplicationRuntimeConfiguration`, `ExecutionConfiguration`, `DatabasePoolConfiguration`, `LoggingConfiguration` | Implemented |
| Plugin definitions | explicit index, descriptors and properties loader | Implemented |
| Plugin execution | selection resolver, reflection factory, bounded coordinator and summaries | Implemented |
| OpenMeteo | API client, configuration, plugin, records and repository | Implemented; database-writing execution requires schema and credentials |
| Ofgem | discovery/download/parser service, workbook extractor, records and repository | Foundation present; configured executable `plugin.ofgem.OfgemPlugin` class absent |
| Persistence | `DatabaseResourceManager`, `SQLServerResource`, JDBC repositories | Implemented foundations |
| Runtime audit | `core.PluginRun` through `JdbcPluginRunAudit` | Implemented for non-dry-run plugin tasks |
| Earlier ingestion audit | `core.ingestion_run`, source files and errors | Separate Phase 3 foundation; not integrated with runtime plugin audit |
| Logging | contextual JUL formatter and plugin/run context | Implemented |
| Documentation | Markdown plus PlantUML source/generated contract | Revised |
| Scheduling | external scheduler | Internal scheduling deferred |

## Transitional or duplicate areas

- `ApplicationConfig`, `ApplicationConfigurationService`, `ConfigurationLoader` and `app.CommandLineArguments` represent the earlier single-plugin path.
- `DatabasePoolConfig` and `DatabasePoolSnapshot` overlap the configuration used by the current runtime.
- The root and `sql/sqlserver` script sets describe different audit models and require a future consolidation decision.

## Version behaviour

Maven builds version `1.0.0`. `--version` reads the package implementation version and therefore prints the packaged Maven version; an IDE/classpath run without manifest metadata prints `development`.
