# OpenData Processing Framework

OpenData is a Java 17 framework for downloading, parsing, validating, transforming, and loading public datasets.

The current codebase provides the framework foundations and a working command-line/configuration bootstrap. The complete dataset plugin execution path is still under development.

## Current status

**Version:** `1.0.0` in Maven metadata  
**Runtime target:** Java 17  
**Build:** Maven  
**Primary database implementation:** Microsoft SQL Server  
**Status:** Active development

## Implemented foundations

- Apache Commons CLI command-line processing
- layered properties-based configuration
- immutable configuration and data models
- configuration validation
- JDK HTTP client download abstraction
- CSV and JSON parser abstractions
- validation contracts and results
- ETL service interfaces
- database repository abstraction
- SQL Server repository implementation
- `java.util.logging`
- project-specific exception hierarchy
- package-level Javadoc documentation
- JUnit 5 test dependency

## Current application behaviour

`com.towermarsh.opendata.Main` currently:

1. parses command-line arguments;
2. handles help, version, and plugin-list requests;
3. resolves and validates configuration;
4. logs the selected plugin and dry-run state;
5. leaves plugin registry and pipeline execution as the next integration step.

The current plugin listing reports `ofgem`, but full plugin discovery and execution are not yet wired into `Main`.

## Build

```bash
mvn clean test
mvn clean package
```

## Command-line examples

```bash
java -jar target/opendata-1.0.0.jar --help
java -jar target/opendata-1.0.0.jar --version
java -jar target/opendata-1.0.0.jar --list-plugins
java -jar target/opendata-1.0.0.jar --plugin ofgem
java -jar target/opendata-1.0.0.jar --plugin ofgem --file path/to/override.properties
java -jar target/opendata-1.0.0.jar --plugin ofgem --dry-run
```

Exact options should remain aligned with `CommandLineArgumentsProcessor`.

## Core package structure

```text
com.towermarsh.opendata
├── cli
├── config
├── database
├── download
├── etl
├── exception
├── logging
├── model
├── parser
├── validation
└── Main
```

Some earlier documents also describe broader application and plugin packages. Those represent intended architecture rather than all currently integrated runtime components.

## Dependencies

- Jackson Databind
- Apache Commons CSV
- Microsoft SQL Server JDBC
- Apache Commons CLI
- JUnit Jupiter

The framework otherwise prefers Java standard-library facilities.

## Architecture

See `docs/architecture/ARCHITECTURE.md`.

## Architecture decisions

See `docs/decisions/ADR-REGISTER.md`.

## Licence

Apache License 2.0.
