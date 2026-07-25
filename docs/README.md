# OpenData Documentation

**Document ID:** DOC-INDEX-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 24 July 2026  
**Minimum Java version:** 17

---

This directory contains the maintained architecture, decision, reference, guide,
plugin and diagram sources for OpenData.

| Area | Purpose |
|---|---|
| [Architecture](architecture/ARCHITECTURE.md) | Boundaries, components, data flows and evolution |
| [Decisions](decisions/README.md) | Durable technical choices and their consequences |
| [Reference](reference/) | Exact configuration, schemas, statuses and data dictionaries |
| [Guides](guides/) | Task-oriented installation and operating instructions |
| [Plugins](plugins/) | Dataset-specific design and mapping documentation |
| [Diagrams](diagrams/README.md) | PlantUML sources and generated-image contract |
| [Reviews](review/) | Completion, readiness and verification records |

Status terms for architecture and delivery documents are **Implemented**,
**Partial**, **Proposed**, **Deferred**, **Shelved** and **Superseded**. ADR
statuses are defined in the [ADR index](decisions/README.md).

The Phase 3 baseline adds managed SQL Server connection pooling, shared ingestion
audit metadata, source-file provenance, a normalised Ofgem price-cap schema and
transactional persistence.
