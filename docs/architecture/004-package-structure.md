# Package Structure

**Document ID:** ARCH-004  
**Version:** 3.0.0  
**Status:** Current implementation and target ownership  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Canonical ownership

| Package | Responsibility |
|---|---|
| root / `app` | Entry point, orchestration and run status |
| `cli` | Commons CLI and immutable arguments |
| `config`, `config.model` | Bootstrap, registration, property sources, plugin definitions and immutable records |
| `plugin` | Provider-neutral contracts, registry, factory, coordinator and run audit |
| `plugin.<id>` | Provider workflow facade |
| `plugin.<id>.initialise` | Typed configuration and orchestration |
| `plugin.<id>.extract` | Provider acquisition and source decoding |
| `plugin.<id>.transform`, `.transform.model`, `.transform.validate` | Provider transformation, records and validation |
| `plugin.<id>.load` | Provider SQL, transactions and load metrics |
| `plugin.<id>.finalise` | Archive, cleanup and completion reporting |
| `download`, `download.strategy` | Shared download contracts and implementations |
| `discovery` | Static HTML discovery and link selection |
| `parser` | CSV, JSON and Excel parsers |
| `validation` | Validation contracts and results |
| `etl` | Reusable extract, transform and load contracts |
| `database`, `database.audit`, `database.jdbc` | Connection resources, pooling, ingestion audit and shared JDBC execution helpers |
| `model` | Framework artefact and result values |
| `logging` | JUL setup and task context |
| `exception` | Framework exception hierarchy |
| `gui` | JavaFX application lifecycle, FXML controller, dialogs, GUI gateways and live log presentation |
| `util` | Small shared formatting and exception-message utilities |

## Plugin-local structure

Ofgem, OpenMeteo and Octopus use the same active pipeline boundary:
`initialise -> extract -> transform -> load -> finalise`. The root plugin class
is a small facade that delegates to the initialise/orchestration class.

The merged Version 3.0.0 source uses the staged provider package layout directly;
the earlier Swing `ui` package and prototype provider compatibility package
structure are no longer part of the current main source tree.

The canonical command-line model is under `cli`. `ExecutionStatus` is the
application status model. Every main-source package has `package-info.java`.
Those package pages group classes, records, interfaces and enums and link each
entry to its type Javadoc.

::: {.landscape}
![OpenData package dependencies](../diagrams/generated/package-dependencies.svg){width=22.5cm}
:::
