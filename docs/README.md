# OpenData Documentation

**Document ID:** DOC-INDEX-001  
**Version:** 1.2  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

This directory contains the maintained architecture, decision, reference, guide,
plugin and diagram sources for OpenData.

| Area | Purpose |
|---|---|
| [User guide](user-guide/README.md) | Installation, configuration and routine operation |
| [Architecture](architecture/ARCHITECTURE.md) | Boundaries, components, data flows and evolution |
| [Development](development/README.md) | Build, test, dependency and release workflows |
| [Standards](standards/README.md) | Coding, testing, documentation and security rules |
| [Operations](operations/README.md) | Runbook, logging, monitoring and recovery |
| [Decisions](decisions/README.md) | Durable technical choices and their consequences |
| [Reference](reference/README.md) | Exact configuration, schemas, statuses and data dictionaries |
| [Guides](guides/README.md) | Task-oriented engineering instructions |
| [Plugins](plugins/README.md) | Dataset-specific design and mapping documentation |
| [Templates](templates/plugin-java/README.md) | Copyable Java structure for a new provider plugin |
| [Roadmap](roadmap/README.md) | Current delivery priorities and acceptance gates |
| [Diagrams](diagrams/README.md) | PlantUML sources and rendered-image conventions |
| [Reviews](review/README.md) | Gap analysis, completion and verification records |

Status terms for architecture and delivery documents are **Implemented**,
**Partial**, **Proposed**, **Deferred**, **Shelved** and **Superseded**. ADR
statuses are defined in the [ADR index](decisions/README.md).

The current baseline includes executable Ofgem and OpenMeteo plugins, bounded
parallel execution, contextual JUL logging, managed SQL Server connection
pooling, runtime audit rows, Ofgem source provenance, relational persistence and
plugin-local pipeline package boundaries.
Known mismatches and acceptance gaps are tracked in the
[documentation gap analysis](review/DOCUMENTATION-GAP-ANALYSIS-2026-07-26.md);
the outstanding completion actions are collected separately in the
[unresolved toolchain and specification summary](review/UNRESOLVED-TOOLCHAIN-AND-SPECIFICATION-GAPS-2026-07-26.md).
