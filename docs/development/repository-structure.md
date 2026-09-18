# Repository Structure

**Document ID:** DEV-REPOSITORY-001  
**Version:** 3.1.0  
**Status:** Version 3.1.0 modular build baseline  
**Baseline date:** 18 September 2026

---

![OpenData repository structure](../diagrams/generated/repository-structure.svg)

## Top-level layout

| Path | Purpose |
|---|---|
| `opendata-api` | Shared plugin contracts, immutable configuration records, and database resource interfaces |
| `opendata-common` | Shared validation, JDBC helpers, base exceptions, and reusable support code |
| `opendata-core` | Application entry points, runtime orchestration, GUI/CLI, SQL Server resources, and bundled plugins |
| `sql` | Ordered SQL Server installation, schemas, permissions and verification |
| `docs` | Authoritative Markdown, PlantUML, manifests, examples and templates |
| `config` | Documentation and code-quality configuration |
| `scripts` | Build, documentation, certificate, quality and release automation |
| `.github/workflows` | Build, documentation and release workflows |
| `tools` | Local third-party tool placement guidance |

## Module layout

Each Maven module uses the standard layout:

```text
src/main/java
src/main/resources
src/test/java
```

Tests must live with the module that owns the production code they verify.
`opendata-common` therefore owns the shared JDBC, validation, and reusable
strategy tests, while `opendata-core` keeps application, plugin, parser, GUI,
and provider tests.

## Java ownership

The split source tree keeps one logical package namespace below
`com.towermarsh.opendata`, but package ownership is now module-specific:

- `opendata-api`: `config.model`, `database`, `plugin`
- `opendata-common`: `database`, `database.jdbc`, `download.strategy`, `exception`, `validation`
- `opendata-core`: root/app entry points, runtime `config`, `database.audit`, `discovery`, `download`, `etl`, core `exception`, `gui`, `logging`, `model`, `parser`, and plugin implementations

Provider-specific code remains below:

```text
com.towermarsh.opendata.plugin.<plugin-id>
```

The provider plugin package structure remains:

```text
<plugin-id>
├── initialise
├── extract
├── transform
│   ├── model
│   └── validate
├── load
└── finalise
```

`docs/templates/plugin-java` is the structural template for new provider code.
`docs/examples/example-plugin` is a compact copyable API example including
registry and properties snippets. Neither tree is compiled by Maven, so example
changes require an explicit temporary compile check.
