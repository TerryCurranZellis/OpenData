# Architecture Documentation Index

## Core documents

| Document | Purpose |
|---|---|
| [ARCHITECTURE.md](ARCHITECTURE.md) | Overall architectural vision, principles, constraints, and system views |
| [COMPONENTS.md](COMPONENTS.md) | Detailed modules, packages, classes, and responsibilities |
| [PLUGIN-ARCHITECTURE.md](PLUGIN-ARCHITECTURE.md) | Dataset plugin contract and extension model |
| [DATA-FLOW.md](DATA-FLOW.md) | Download, validation, transformation, and persistence flows |
| [CONFIGURATION.md](CONFIGURATION.md) | CLI and plugin parameter-file design |
| [PERSISTENCE.md](PERSISTENCE.md) | SQL Server persistence, transactions, metadata, and idempotency |
| [DEPLOYMENT.md](DEPLOYMENT.md) | Runtime, build, release, scheduling, and deployment architecture |
| [SECURITY.md](SECURITY.md) | Input, credentials, transport, database, and dependency controls |
| [LOGGING-AND-OPERATIONS.md](LOGGING-AND-OPERATIONS.md) | `java.util.logging`, diagnostics, exit codes, and operational support |
| [TESTING.md](TESTING.md) | Unit, integration, contract, and end-to-end testing strategy |
| [EXTENDING-OPENDATA.md](EXTENDING-OPENDATA.md) | Procedure for adding another dataset plugin |
| [GLOSSARY.md](GLOSSARY.md) | Project terminology |

## Architecture Decision Records

The `decisions` directory contains numbered Architecture Decision Records (ADRs).

| ADR | Decision |
|---|---|
| [ADR-0001](decisions/ADR-0001-plugin-architecture.md) | Use a dataset plugin architecture |
| [ADR-0002](decisions/ADR-0002-apache-commons-cli.md) | Use Apache Commons CLI |
| [ADR-0003](decisions/ADR-0003-java-util-logging.md) | Standardise on `java.util.logging` |
| [ADR-0004](decisions/ADR-0004-parameter-file-layering.md) | Use plugin defaults plus optional override files |
| [ADR-0005](decisions/ADR-0005-sql-server-persistence.md) | Use SQL Server as the first persistence target |
| [ADR-0006](decisions/ADR-0006-package-info-documentation.md) | Use `package-info.java` for package documentation |
| [ADR-0007](decisions/ADR-0007-modular-monolith.md) | Begin as a modular monolith |
| [ADR template](decisions/ADR-TEMPLATE.md) | Template for new decisions |

## Diagram sources

The `diagrams` directory contains editable source files.

- PlantUML: `.puml`
- Mermaid: `.mmd`

The Markdown documents also include selected Mermaid diagrams for direct GitHub rendering.
