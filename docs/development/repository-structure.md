# Repository Structure

**Document ID:** DEV-REPOSITORY-001  
**Version:** 3.0.0  
**Status:** Version 3.0.0 implementation baseline  
**Baseline date:** 15 August 2026  

---

![OpenData repository structure](../diagrams/generated/repository-structure.svg)

## Top-level layout

| Path | Purpose |
|---|---|
| `src/main/java` | Application, framework infrastructure and provider plugins |
| `src/main/resources/config` | Bootstrap defaults, registry definitions and current certificate resources |
| `src/test/java` | Unit and mock-based component tests |
| `sql` | Ordered SQL Server installation, schemas, permissions and verification |
| `docs` | Authoritative Markdown, PlantUML, manifests, examples and templates |
| `config` | Documentation and code-quality configuration |
| `scripts` | Build, documentation, certificate, quality and release automation |
| `.github/workflows` | Build, documentation and release workflows |
| `tools` | Local third-party tool placement guidance |

## Java package layout

Framework packages sit below `com.towermarsh.opendata`, including:

```text
app
cli
config
config.model
database
database.audit
database.jdbc
discovery
download
download.strategy
etl
exception
gui
logging
model
parser
plugin
util
validation
```

Provider-specific code belongs below:

```text
com.towermarsh.opendata.plugin.<plugin-id>
```

The provider plugin package structure used by Version 3.0.0 is:

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

The root plugin class implements `OpenDataPlugin` and delegates orchestration to
the `initialise` stage. Temporary compatibility packages such as provider-local
`config` or `download` may still exist in the current source; new code should
not expand those duplicates.

All 42 production packages currently contain `package-info.java`; each inventory groups and links its classes, records, interfaces and enums with a short description. The shared
`exception` package owns framework exception types; plugin-specific errors are
translated at the plugin boundary rather than creating an unrelated exception
hierarchy.

## Documentation examples

`docs/templates/plugin-java` is the structural template for new provider code.
`docs/examples/example-plugin` is a compact copyable API example including registry
and properties snippets. Neither tree is compiled by Maven, so example changes
require an explicit temporary compile check.

## Generated content

PlantUML source lives under `docs/diagrams/source`; maintained SVG output lives
under `docs/diagrams/generated`. Generated manuals belong under the configured
build directory and are not authoritative Markdown source.
