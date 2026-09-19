# OpenData

Version: **3.1.0**

OpenData is a Java 24 modular monolith for acquiring, validating, and loading
OpenData sources through a shared framework, a JavaFX desktop interface, and a
command-line entry point.

## Maven modules

| Module | Responsibility |
|---|---|
| `opendata-api` | Shared plugin contracts, immutable plugin/configuration records, and database resource interfaces |
| `opendata-common` | Shared validation, JDBC helpers, shared exceptions, utility helpers, logging context, and reusable download-support strategies |
| `opendata-plugins` | Bundled plugin definitions and provider implementations |
| `opendata-core` | Application entry points, runtime orchestration, plugin registry/execution infrastructure, SQL Server resources, and GUI/CLI |

The current bundled plugins now build in `opendata-plugins` and remain part of
the same reactor and executable release train.

## Build

Prerequisites:

- Java 24 or later for the full reactor build (`opendata-core` uses JavaFX)
- Maven 3.9 or later

Run the full reactor build from the repository root:

```powershell
mvn clean verify
```

Each module now carries the same build-environment, test, coverage, static
analysis, and dependency-analysis verification so the split modules are checked
independently inside the shared reactor build.

Older JDKs may still be used for limited compilation checks of the non-JavaFX
modules, but the supported full build remains Java 24 or later.

## Documentation

Use the [documentation index](docs/DOCUMENTATION-INDEX.md) for the maintained
manual set and the [developer guide](docs/development/README.md) for build,
module, and contribution guidance.
