# Plugin Documentation

**Document ID:** PLUGIN-INDEX-001
**Version:** 2.0
**Status:** Version 2.0.0 implementation baseline
**Baseline date:** 3 August 2026

---

::: {.docx-linear-table}

| Plugin | Source type | Persistence | Current status |
|---|---|---|---|
| [Ofgem](ofgem/README.md) | HTML discovery and XLSX | Transactional period replacement with source lineage | Implemented; live SQL acceptance pending |
| [OpenMeteo](openmeteo/README.md) | Historical JSON API | Idempotent location/date upsert | Implemented; live SQL acceptance pending |
| [Octopus](octopus/README.md) | Local PDF statements | Transactional electricity/gas upsert plus processed-file ledger | Write path implemented; dry-run defect and live SQL acceptance pending |

:::

All installed plugins are listed in
`src/main/resources/config/plugins/index.properties`, are constructed through the
reflection factory, return `PluginMetrics` and use plugin-local pipeline packages.
Their business models remain independent.

## Active pipeline pattern

| Step | Package | Responsibility |
|---|---|---|
| Initialise | `initialise` | Build typed configuration and control stage order |
| Extract | `extract` | Acquire or read source data |
| Transform | `transform` | Parse, validate and create typed records |
| Load | `load` | Persist transactionally or return dry-run metrics |
| Finalise | `finalise` | Archive/cleanup successful source artefacts and report completion |

The active classes are those imported by each plugin's `initialise` class. Some
Ofgem and OpenMeteo compatibility classes with similar names remain in older
`config`, `download` or `extract` locations; new code should not copy those
parallel paths.

## Dry-run status

Ofgem and OpenMeteo complete end-to-end dry runs without plugin database writes.
Octopus does not currently complete a dry run: `OctopusExtract` reads
`octopus.statement_file` before the load-stage dry-run branch, while the
framework supplies an unavailable database resource. Until the Java defect is
fixed, do not use `--plugin octopus --dry-run` or `--plugin all --dry-run` as an
acceptance command.

New implementations should start from the
[example plugin](../examples/example-plugin/README.md).
