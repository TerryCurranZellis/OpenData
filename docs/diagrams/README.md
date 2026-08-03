# PlantUML Diagram Index

**Document ID:** DIAG-INDEX-001
**Version:** 2.0
**Status:** Version 2.0.0 baseline
**Baseline date:** 3 August 2026

---

All canonical PlantUML files are stored in [source](source/). Rendered SVG or PNG
files are written to `generated/`. No `.puml` file should be maintained directly
under `docs/diagrams` or another legacy subdirectory.

| Diagram | Canonical source file | Rendered SVG |
|---|---|---|
| Project overview | `project-overview.puml` | [image](generated/project-overview.svg) |
| Repository structure | `repository-structure.puml` | [image](generated/repository-structure.svg) |
| Version evolution | `version-evolution.puml` | [image](generated/version-evolution.svg) |
| Documentation hierarchy | `documentation-hierarchy.puml` | [image](generated/documentation-hierarchy.svg) |
| Architecture overview | `architecture-overview.puml` | [image](generated/architecture-overview.svg) |
| System context | `system-context.puml` | [image](generated/system-context.svg) |
| Component architecture | `component-architecture.puml` | [image](generated/component-architecture.svg) |
| Package dependencies | `package-dependencies.puml` | [image](generated/package-dependencies.svg) |
| Plugin registry | `plugin-registry.puml` | [image](generated/plugin-registry.svg) |
| Command-line flow | `command-line-flow.puml` | [image](generated/command-line-flow.svg) |
| Configuration loading | `configuration-loading-sequence.puml` | [image](generated/configuration-loading-sequence.svg) |
| Configuration registration | `configuration-registration-sequence.puml` | [image](generated/configuration-registration-sequence.svg) |
| Plugin execution | `plugin-execution-sequence.puml` | [image](generated/plugin-execution-sequence.svg) |
| Parallel execution | `plugin-parallel-execution.puml` | [image](generated/plugin-parallel-execution.svg) |
| Plugin pipeline | `pipeline-sequence.puml` | [image](generated/pipeline-sequence.svg) |
| Dataset lifecycle | `dataset-lifecycle.puml` | [image](generated/dataset-lifecycle.svg) |
| Download strategies | `download-strategy-classes.puml` | [image](generated/download-strategy-classes.svg) |
| Database architecture | `database-architecture.puml` | [image](generated/database-architecture.svg) |
| Database schemas | `opendata-database.puml` | [image](generated/opendata-database.svg) |
| Database persistence components | `database-persistence-components.puml` | [image](generated/database-persistence-components.svg) |
| Database persistence sequence | `database-persistence-sequence.puml` | [image](generated/database-persistence-sequence.svg) |
| Ofgem import | `ofgem-price-cap-import-sequence.puml` | [image](generated/ofgem-price-cap-import-sequence.svg) |
| SQL Server deployment | `sql-server-deployment.puml` | [image](generated/sql-server-deployment.svg) |
| Run state | `ingestion-run-state.puml` | [image](generated/ingestion-run-state.svg) |
| OpenMeteo data model | `openmeteo-data-model.puml` | [image](generated/openmeteo-data-model.svg) |
| OpenMeteo persistence | `openmeteo-persistence.puml` | [image](generated/openmeteo-persistence.svg) |
| Octopus statement processing | `octopus-statement-processing.puml` | [image](generated/octopus-statement-processing.svg) |
| Octopus data model | `octopus-data-model.puml` | [image](generated/octopus-data-model.svg) |
| Documentation generation | `documentation-generation-flow.puml` | [image](generated/documentation-generation-flow.svg) |
| Operational lifecycle | `operational-lifecycle.puml` | [image](generated/operational-lifecycle.svg) |
| Manifest-driven documentation engine | `manifest-driven-documentation-engine.puml` | [image](generated/manifest-driven-documentation-engine.svg) |

Run the documentation build with `-RenderDiagrams`. Markdown embeds the committed
SVG files, and documentation validation fails when a referenced rendered image is
missing.
