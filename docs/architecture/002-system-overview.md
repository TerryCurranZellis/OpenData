# System Overview

**Document ID:** ARCH-002  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## Boundary

OpenData is a command-line application. It makes outbound HTTPS requests, stores
work/archive/failure artefacts on the file system, and optionally loads accepted
data into SQL Server.

## External participants

| Participant | Interaction |
|---|---|
| Operator | Selects a plugin and supplies overrides |
| Publisher | Provides API, file or HTML page |
| Credential provider | Resolves secret references |
| File system | Stores raw and intermediate artefacts |
| SQL Server | Initial relational target |
| Documentation toolchain | Publishes Markdown and PlantUML |

## Capabilities and status

CLI, logging and structured properties are implemented. Registry-driven listing,
Commons CSV, JSoup discovery and Apache POI form the consolidated integration
baseline. Validation, ETL and database services are foundations. Complete
pipeline history and database plugin configuration are not complete.

## Constraint

Dataset names, URLs and listing text must not be hard-coded in `Main`.

See [system-context.puml](../diagrams/system-context.puml).
