# OpenData

Version: **4.0.0**

OpenData is a Java 24 modular monolith for acquiring, validating, and loading
OpenData sources through a shared framework, a JavaFX desktop interface, and a
command-line entry point.

## Maven modules

| Module | Responsibility |
|---|---|
| `opendata-api` | Shared plugin contracts, immutable configuration records, database interfaces, exception hierarchy and dependency-free validation/formatting |
| `opendata-common` | Provider-oriented JDBC helpers, compatibility utilities and reusable download-support strategies |
| `opendata-core` | JavaFX/CLI, first-run setup, execution orchestration, SQL Server infrastructure; depends on API only |
| `opendata-plugins` | Aggregator for independent provider plugin projects and shared plugin-support projects |
| `opendata-launcher` | Runnable composition module depending on Core, Common and all provider plugins; owns packaged-plugin integration tests |

Each provider plugin (Ofgem, OpenMeteo, Octopus, Octopus Adjustment, Electric
Demand and Electric Supply) is its own Maven project under `opendata-plugins`.
Shared NESO and Octopus implementation support also has separate projects.
`opendata-launcher` depends on Core, Common and the provider projects, so the
per-project classpath catalogues, implementations and registration SQL are available at runtime.
Core itself must not depend on Common or provider implementations.

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
mvn -Dquality.failOnViolation=true clean verify
```

The reactor verifies the shared modules, application, provider plugins, and
plugin-support projects together. Provider projects can also be selected with
their required dependencies, for example:

```powershell
mvn -pl opendata-plugins/opendata-plugin-openmeteo -am verify
```

Older JDKs may still be used for limited compilation checks of the non-JavaFX
modules, but the supported full build remains Java 24 or later.

To launch a complete plugin-capable application from the repository root, install
the reactor artifacts and run the launcher:

```powershell
mvn clean install
mvn -pl opendata-launcher exec:java
```

Launch with a desktop JavaFX session when `init=true`. Running `opendata-core` alone
no longer bundles provider JARs; use the launcher for normal application execution.

## Documentation

Use the [documentation index](docs/DOCUMENTATION-INDEX.md) for the maintained
manual set and the [developer guide](docs/development/README.md) for build,
module, and contribution guidance.


## Historic electricity plugins (NESO)

Two independent plugins: `electric-demand` and `electric-supply`. See [electricity plugin guide](docs/electricity-plugins.md).

## Code quality and architectural review

The 4.1.0 review branch runs strict Checkstyle, SpotBugs and test verification:

```powershell
mvn -Dquality.failOnViolation=true clean verify
```

Development of strict module boundaries is tracked in the
[4.1.0 module-boundary review](docs/architecture/031-module-boundaries-4.1-review.md).
The 4.1.0 review branch separates provider assembly into `opendata-launcher`,
removes Common and provider dependencies from Core, and adds source/POM boundary
tests. The branch still requires final CI and desktop verification before release.
JavaFX 26.0.2 is selected in the review branch to preserve JDK 24 compatibility.
