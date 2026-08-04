# OpenData Architecture Manual

**Document ID:** ARCH-INDEX-001
**Version:** 2.1
**Status:** Version 2.0.0 implementation baseline
**Baseline date:** 4 August 2026
**Minimum Java version:** 17

---

The numbered documents describe the current OpenData modular monolith as
implemented in the Version 2.0.0 source baseline. The application has a packaged
registration catalogue, persistent SQL Server plugin registry, database-backed
configuration, bounded parallel execution, contextual logging, pooled SQL Server
access and three executable plugins: Ofgem, OpenMeteo and Octopus Energy.

The documentation distinguishes implemented behaviour from remaining release
hardening. In particular, RSA password encryption is implemented, but encryption
does not provide adequate protection while a private key store or a plaintext
bootstrap password is retained in the source tree. That is an operational and
release issue, not a future architecture feature.

::: {.docx-linear-table}

| Document | Subject |
|---|---|
| [001](001-project-vision.md) | Project Vision |
| [002](002-system-overview.md) | System Overview |
| [003](003-high-level-architecture.md) | High-Level Architecture |
| [004](004-package-structure.md) | Package Structure |
| [005](005-dependency-rules.md) | Dependency Rules |
| [006](006-layered-architecture.md) | Layered Architecture |
| [007](007-plugin-architecture.md) | Plugin Architecture |
| [008](008-pipeline-engine.md) | Pipeline Engine |
| [009](009-dataset-lifecycle.md) | Dataset Lifecycle |
| [010](010-component-interactions.md) | Component Interactions |
| [011](011-configuration.md) | Configuration Architecture |
| [012](012-logging.md) | Logging Architecture |
| [013](013-exception-strategy.md) | Exception Strategy |
| [014](014-database-architecture.md) | Database Architecture |
| [015](015-future-architecture.md) | Future Architecture |
| [016](016-download-and-parsing.md) | Download and Parsing Architecture |
| [017](017-security-and-credentials.md) | Security and Credentials |
| [018](018-testing-and-quality.md) | Testing and Quality |
| [019](019-database-persistence-and-pooling.md) | Database Persistence and Connection Pooling |
| [020](020-ingestion-audit-and-provenance.md) | Ingestion Audit and Provenance |
| [021](021-ofgem-price-cap-architecture.md) | Ofgem Price-Cap Architecture |
| [022](022-deployment-and-environments.md) | Deployment and Environments |
| [023](023-operational-failure-and-recovery.md) | Operational Failure and Recovery |
| [024](024-architecture-traceability.md) | Architecture Traceability |
| [025](025-manifest-driven-documentation-engine.md) | Manifest-Driven Documentation Engine |
| [026](026-openmeteo-historical-weather-architecture.md) | OpenMeteo Historical Weather Architecture |
| [027](027-octopus-energy-statement-architecture.md) | Octopus Energy Statement Architecture |
| [028](028-shared-validation-and-jdbc-infrastructure.md) | Shared Validation and JDBC Infrastructure |

:::

Principal diagrams are indexed in [the diagram directory](../diagrams/README.md).
The current implementation inventory and documentation audit are maintained in
[CURRENT-CODE-INVENTORY.md](CURRENT-CODE-INVENTORY.md) and
[DOCUMENTATION-AUDIT.md](DOCUMENTATION-AUDIT.md).
