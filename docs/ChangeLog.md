# Change Log

All notable changes to the OpenData project are documented here.

The format is based on Keep a Changelog. The project is under active development.

## [Unreleased] - 2026-07-22

### Added

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

- Confirmed Java 17 as the Maven compiler source and target.
- Maven project version is now `1.0.0`.
- Architecture documentation now distinguishes implemented, partial, deferred, and shelved capabilities.
- SQL Server is described as the current database implementation behind an abstraction rather than complete database independence.
- Plugin architecture is described as accepted design with runtime registry and pipeline wiring still pending.
- Internal scheduling is explicitly documented as deferred.
- Configuration remains properties-file based while database-hosted plugin settings remain shelved.
- Application bootstrap now resolves and validates configuration before future plugin execution.
- Failure handling now separates command-line, configuration, and unexpected runtime failures.
- Dependency documentation now includes Jackson, Commons CSV, SQL Server JDBC, Commons CLI, and JUnit 5.

### Fixed

- Documentation no longer describes the current application as production-ready.
- Documentation no longer claims that the full ETL pipeline is executed by `Main`.
- Documentation now includes the `cli` package.
- Documentation now records the difference between Maven version `1.0.0` and the current `--version` banner `0.1.0-SNAPSHOT`.

### Deferred

- Complete plugin registry and discovery.
- End-to-end Ofgem plugin execution.
- Pipeline coordinator integration into `Main`.
- Framework metadata and run-history persistence.
- Internal scheduling.
- Database-backed plugin configuration.
- Additional database implementations.

## [0.1.0] - Initial framework design

### Added

- Initial modular-monolith architecture.
- Initial package structure.
- Initial ADR set.
- Documentation-first project rules.
- Properties-based plugin configuration direction.
- SQL Server-first persistence direction.
- Ofgem reference-plugin direction.
