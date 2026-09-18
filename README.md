# OpenData

Version: **3.1.0**

OpenData is a Java 24 modular monolith for acquiring, validating, and loading
OpenData sources through a shared framework, a JavaFX desktop interface, and a
command-line entry point.

## Maven modules

| Module | Responsibility |
|---|---|
| `opendata-api` | Shared plugin contracts, immutable plugin/configuration records, and database resource interfaces |
| `opendata-common` | Shared validation, JDBC helpers, base exceptions, and reusable download-support strategies |
| `opendata-core` | Application entry points, runtime orchestration, SQL Server resources, GUI/CLI, and bundled plugins |

The current bundled plugins remain in `opendata-core`. They will be split into
separate projects later, but that extraction is not part of the current build.

## Build

Prerequisites:

- Java 24 or later
- Maven 3.9 or later

Run the full reactor build from the repository root:

```powershell
mvn clean verify
```

Each module now carries the same build-environment, test, coverage, static
analysis, and dependency-analysis verification so the split modules are checked
independently inside the shared reactor build.

## Documentation

Use the [documentation index](docs/DOCUMENTATION-INDEX.md) for the maintained
manual set and the [developer guide](docs/development/README.md) for build,
module, and contribution guidance.
