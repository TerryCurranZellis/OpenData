# OpenData

Checked-in Maven version: **4.2.0** (4.0 first-run GUI documentation retained as a feature baseline)

OpenData is a modular Java application targeting Java 24 bytecode and currently requiring JDK 26 for full Maven builds for acquiring, validating, and loading
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
reinitialize a live database. Runtime compatibility must be verified against the JavaFX dependencies for the selected release.

## Build

Prerequisites:

- JDK 26 or later for the full reactor build (as enforced by the root Maven POM; `maven.compiler.release` remains 24)
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
modules, but the full reactor currently requires JDK 26 or later.

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

The checked-in 4.2.0 reactor runs strict Checkstyle, SpotBugs and test verification:

```powershell
mvn -Dquality.failOnViolation=true clean verify
```

The earlier module-boundary migration is documented in the
[4.1.0 module-boundary review](docs/architecture/031-module-boundaries-4.1-review.md).
The current Maven reactor includes `opendata-launcher` and isolates Core from Common
and provider implementations. The checked-in Core POM selects JavaFX 27; verify
runtime support and GUI acceptance with the actual release JDK before distribution.

To generate all four maintained manuals, use
`./scripts/Build-Documentation.ps1 -Document All -Format All -RenderDiagrams`.
HTML and DOCX need Pandoc, PDF also needs the configured XeLaTeX/SVG tools,
and CHM needs Microsoft HTML Help Workshop on Windows.
