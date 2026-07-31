# OpenData Documentation

**Document ID:** DOC-INDEX-001  
**Version:** 1.3  
**Status:** Baseline  
**Baseline date:** 31 July 2026  
**Minimum Java version:** 17

---

This directory contains the maintained architecture, decisions, reference, guides, operations material, plugin documentation and diagram sources for OpenData.

| Area | Purpose |
|---|---|
| [Document manifests](manifests/README.md) | One composition definition per generated guide |
| [Technical user guide sources](user-guide/README.md) | Installation, configuration and routine operation |
| [Architecture](architecture/ARCHITECTURE.md) | Boundaries, components, data flows and evolution |
| [Development](development/README.md) | Build, test, dependency and release workflows |
| [Standards](standards/README.md) | Coding, testing, documentation and security rules |
| [Operations](operations/README.md) | Runbook, logging, monitoring and recovery |
| [Decisions](decisions/README.md) | Durable technical choices and their consequences |
| [Reference](reference/README.md) | Exact configuration, schemas, statuses and data dictionaries |
| [Guides](guides/README.md) | Task-oriented engineering instructions |
| [Plugins](plugins/README.md) | Dataset-specific design and mapping documentation |
| [Templates](templates/plugin-java/README.md) | Copyable Java structure for a provider plugin |
| [Roadmap](roadmap/README.md) | Current delivery priorities and acceptance gates |
| [Diagrams](diagrams/README.md) | PlantUML sources and rendered-image conventions |
| [Reviews](review/README.md) | Gap analysis, completion and verification records |
| [Migration notes](migration/MANIFEST-DRIVEN-DOCUMENTATION-ENGINE.md) | Documentation-engine migration instructions |

## Documentation engine

`config/documentation.json` contains global settings and common defaults. The engine discovers every JSON manifest in `docs/manifests`, assembles shared front matter and ordered Markdown, inserts the TOC after the front matter, and publishes the requested formats through Pandoc.

The initial generated documents are the Technical User Guide, Administrator Guide, Developer Guide, and API and Configuration Reference. Adding another guide requires no PowerShell modification.

See [Documentation Standards](Documentation-Standards.md), [ADR-0045](decisions/ADR-0045-documentation-delivery-baseline.md), [ADR-0046](decisions/ADR-0046-manifest-driven-documentation-engine.md), and the [manifest-driven engine architecture](architecture/025-manifest-driven-documentation-engine.md).

## Repository policies

- [Contributing](../CONTRIBUTING.md)
- [Code of Conduct](../CODE_OF_CONDUCT.md)
- [Security policy](../SECURITY.md)
- [Changelog](../CHANGELOG.md)
- [Apache 2.0 licence](../LICENSE.md)
- [Notice](../NOTICE)
