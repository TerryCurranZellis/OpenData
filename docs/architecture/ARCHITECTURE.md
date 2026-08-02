# OpenData Architecture Manual

**Document ID:** ARCH-INDEX-001  
**Version:** 1.2  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

The numbered documents describe the current modular-monolith architecture.
Concrete Ofgem and OpenMeteo flows execute through a bounded plugin
coordinator, and the partially implemented Octopus flow uses the same registry
and pipeline boundaries; documents distinguish that implementation from reusable
framework contracts and deferred work.

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

:::

Principal diagrams are indexed in [the diagram directory](../diagrams/README.md).
Known implementation mismatches are recorded in the
[current gap analysis](../review/DOCUMENTATION-GAP-ANALYSIS-2026-07-26.md).
