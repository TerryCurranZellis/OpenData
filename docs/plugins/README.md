# Plugin Documentation

**Document ID:** PLUGIN-INDEX-001  
**Version:** 3.0.0  
**Status:** Version 3.0.0 implementation baseline  
**Baseline date:** 15 August 2026  

---

| Plugin | Source type | Persistence | Current status |
|---|---|---|---|
| [Ofgem](ofgem/README.md) | HTML discovery and XLSX | Transactional period replacement with source lineage | Implemented; live SQL acceptance pending |
| [OpenMeteo](openmeteo/README.md) | Historical JSON API | Idempotent location/date upsert | Implemented; live SQL acceptance pending |
| [Octopus](octopus/README.md) | Local PDF statements | Transactional electricity/gas upsert plus processed-file ledger | Implemented; live SQL acceptance pending |

Packaged plugin definitions are listed in
`src/main/resources/config/plugins/index.properties`. They are available for
registration but are not automatically installed. The SQL Server table
`core.plugin_registry` is authoritative for list, status and execution.

## Lifecycle commands

```text
opendata --plugin all --register
opendata --list-plugins
opendata --plugin octopus --disable
opendata --plugin octopus --enable
opendata --plugin octopus --unregister
```

## Active pipeline pattern

| Step | Package | Responsibility |
|---|---|---|
| Initialise | `initialise` | Build typed configuration and control stage order |
| Extract | `extract` | Acquire or read source data |
| Transform | `transform` | Parse, validate and create typed records |
| Load | `load` | Persist transactionally or return dry-run metrics |
| Finalise | `finalise` | Archive/cleanup successful source artefacts and report completion |

Ofgem, OpenMeteo and Octopus support side-effect-free dry runs. Octopus dry run
skips completion-ledger access, parses every matching input PDF and does not move
files.

New implementations should start from the
[example plugin](../examples/example-plugin/README.md).
