# Change Log

All notable changes to the OpenData project are documented here.

The format is based on Keep a Changelog. The project is under active development.

## [Unreleased] - 2026-07-26

### Added

- Executable Ofgem and OpenMeteo plugins.
- Repeated, comma-separated and `all` plugin selection.
- Bounded parallel plugin execution and aggregate result reporting.
- `--parallelism`, `--dry-run` and multi-plugin override scoping.
- Contextual `java.util.logging` output with worker, plugin and run identifiers.
- Apache DBCP connection pooling and SQL Server pool health information.
- Generic `core.PluginRun` runtime audit records.
- OpenMeteo location and daily-weather persistence with idempotent upserts.
- SQL Server application locks for same-location OpenMeteo concurrency.
- Complete user-guide, operations, development, standards and roadmap sections.
- Separate technical and user-documentation build targets.
- Standalone PlantUML-to-SVG rendering command.
- A4 portrait/landscape filters for DOCX and PDF figures.
- ADR-0038 through ADR-0042 for the current execution and persistence decisions.
- ADR-0043 for plugin-local pipeline package ownership.
- Current documentation gap analysis and verification matrix.
- Separate unresolved toolchain and specification hand-off summary.
- Java package templates for new plugin config, download, extract, transform,
  model, validation and load stages.
- Dedicated `cli` package for command-line concerns.
- Immutable command-line argument model.
- Apache Commons CLI argument processor.
- Command-line processing exception boundary.
- Help, version, and plugin-list control paths.
- Layered configuration service.
- Configuration loader and validation chain.
- Standard configuration validator.
- Immutable application configuration.
- JDK HTTP-based downloader abstraction and result model.
- CSV parser based on Apache Commons CSV.
- JSON parser based on Jackson Databind.
- Data validation contracts and result model.
- Extract, transform, and load service boundaries.
- Database repository abstraction.
- SQL Server repository and connection manager.
- Framework domain models for datasets, sources, files, downloads, imports, and validation.
- `java.util.logging` support and logging manager.
- Framework-specific exception hierarchy.
- Package-level Javadoc documentation.
- JUnit Jupiter test dependency.
- Maven Surefire and compiler plugin configuration.
- ADR-0021 for configuration resolution and validation.
- ADR-0022 for CLI control commands and process exit codes.
- ADR-0023 for format parser adapters.
- Current-code inventory and documentation audit.

### Changed

- The plugin registry now creates executable plugins by reflection from the
  implementation class recorded in each descriptor.
- Ofgem and OpenMeteo provider code now lives entirely below its plugin id in
  distinct `config`, `download`, `extract`, `transform` and `load` packages.
- Root provider classes now contain workflow ordering rather than source,
  transformation or persistence implementation.
- The application entry point now coordinates configuration, database lifecycle,
  audit and plugin execution.
- Ofgem now performs discovery, download, extraction, optional archiving and
  transactional persistence through the registered CLI plugin.
- OpenMeteo ADR-0028 is now Accepted.
- Diagram sources are canonical under `docs/diagrams/source`; Markdown embeds
  committed rendered SVG files from `docs/diagrams/generated`.
- Historical duplicate ADRs for concurrency and OpenMeteo storage are mapped to
  unique canonical records ADR-0038 through ADR-0041.
- Confirmed Java 17 as the Maven compiler source and target.
- Maven project version is now `1.0.0`.
- Architecture documentation now distinguishes implemented, partial, deferred, and shelved capabilities.
- SQL Server is described as the current database implementation behind an abstraction rather than complete database independence.
- Plugin architecture is described as implemented for the registered Ofgem and
  OpenMeteo plugins; the generic stage-contract pipeline remains a framework
  boundary rather than the runtime orchestrator.
- Internal scheduling is explicitly documented as deferred.
- Configuration remains properties-file based while database-hosted plugin settings remain shelved.
- Application bootstrap resolves and validates configuration before executable
  plugin selection and execution.
- Failure handling now separates command-line, configuration, and unexpected runtime failures.
- Dependency documentation now includes Jackson, Commons CSV, SQL Server JDBC, Commons CLI, and JUnit 5.

### Fixed

- Removed unused `app.CommandLineArguments` and `ApplicationRunStatus`.
- Added operator-facing descriptions to `ExecutionStatus` and final status logs.
- Removed the disconnected legacy Ofgem import service and repository stack.
- Corrected the OpenMeteo HTTP user-agent spelling.
- Moved affected JUnit tests with the production package structure and added
  validator/status coverage.
- Removed stale claims that Ofgem orchestration, run identifiers, parallel plugin
  execution and OpenMeteo persistence were still pending.
- Replaced Markdown links to PlantUML source files with rendered SVG references.
- Corrected documentation that implied the Maven artifact was already an
  executable fat JAR.
- Corrected version documentation: `--version` now reads the package
  implementation version and otherwise reports `development`.
- Documentation no longer describes the current application as production-ready.
- Documentation no longer claims that the full ETL pipeline is executed by `Main`.
- Documentation now includes the `cli` package.
- Database configuration examples now use the runtime property names and
  external-override prefix.
- SQL Server setup now gives the combined transitional order for Ofgem,
  `PluginRun` and OpenMeteo scripts.
- Security and persistence documents now record the tracked-password,
  unbounded-download and ADR-0030 lifecycle variances.

### Deferred

- Internal scheduling.
- Database-backed plugin configuration.
- Additional database implementations.
- Executable/fat-JAR packaging and reliable process exit-code mapping.
- Unification of `core.PluginRun` and `core.ingestion_run`.
- Production secret-provider integration and removal of classpath passwords.
- Detailed Ofgem component-value import and historical backfill.

## [0.1.0] - Initial framework design

### Added

- Initial modular-monolith architecture.
- Initial package structure.
- Initial ADR set.
- Documentation-first project rules.
- Properties-based plugin configuration direction.
- SQL Server-first persistence direction.
- Ofgem reference-plugin direction.

## 2026-07-27

- Corrected command-line parsing when an IDE or wrapper passes the complete argument line as one `String[]` element.
- Added regression tests for `--plugin all --dry-run`, quoted override-file paths, and unmatched quotes.
- Added a copyable `example` plugin implementation and properties templates under `docs/examples/example-plugin`.
