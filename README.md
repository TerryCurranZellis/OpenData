# OpenData

Version: **4.0.0**

OpenData is a Java 24 modular monolith for acquiring, validating, and loading
OpenData sources through a shared framework, a JavaFX desktop interface, and a
command-line entry point.

## Maven modules

| Module | Responsibility |
|---|---|
| `opendata-api` | Shared plugin contracts, immutable plugin/configuration records, and database resource interfaces |
| `opendata-common` | Shared validation, JDBC helpers, shared exceptions, utility helpers, logging context, and reusable download-support strategies |
| `opendata-core` | Application entry points, runtime orchestration, plugin registry/execution infrastructure, SQL Server resources, GUI/CLI, and runtime dependencies on provider plugins |
| `opendata-plugins` | Aggregator for independent provider plugin projects and shared plugin-support projects |

Each provider plugin (Ofgem, OpenMeteo, Octopus, Octopus Adjustment, Electric
Demand and Electric Supply) is its own Maven project under `opendata-plugins`.
Shared NESO and Octopus implementation support also has separate projects.
`opendata-core` depends on the provider projects so their per-project classpath
catalogues, implementations, and registration SQL remain available at runtime.

## New in version 4: first-run configuration

Set `init=true` in `config/application.properties` or use the packaged
first-run defaults, and start OpenData with a graphical desktop. The JavaFX
wizard provisions SQL Server, registers all discovered plugins **disabled**,
and saves encrypted settings with `init=false` at completion. Administrator
SQL credentials are not stored. See the
[First-run guide](docs/guides/first-run-initialisation.md).

Existing version 3 users should migrate their bootstrap file rather than
reinitialize a live database. Full runtime validation is pending on JDK 24+.

## Build

Prerequisites:

- Java 24 or later for the full reactor build (`opendata-core` uses JavaFX)
- Maven 3.9 or later

Run the full reactor build from the repository root:

```powershell
mvn clean verify
```

The reactor verifies the shared modules, application, provider plugins, and
plugin-support projects together. Provider projects can also be selected with
their required dependencies, for example:

```powershell
mvn -pl opendata-plugins/opendata-plugin-openmeteo -am verify
```

Older JDKs may still be used for limited compilation checks of the non-JavaFX
modules, but the supported full build remains Java 24 or later.

## Documentation

Use the [documentation index](docs/DOCUMENTATION-INDEX.md) for the maintained
manual set and the [developer guide](docs/development/README.md) for build,
module, and contribution guidance.


## Historic electricity plugins (NESO)

Two independent plugins: `electric-demand` and `electric-supply`. See [electricity plugin guide](docs/electricity-plugins.md).
