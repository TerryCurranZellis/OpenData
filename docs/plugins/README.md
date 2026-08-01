# Plugin Documentation

**Document ID:** PLUGIN-INDEX-001  
**Version:** 1.2  
**Status:** Baseline  
**Baseline date:** 01 Aug 2026

---

::: {.docx-linear-table}

| Plugin | Source type | Persistence | Status |
|---|---|---|---|
| [Ofgem](ofgem/README.md) | HTML discovery and XLSX | Transactional period replacement | Implemented; live write acceptance pending |
| [OpenMeteo](openmeteo/README.md) | JSON API | Idempotent location/date upsert | Implemented; live write acceptance pending |
| [Octopus](octopus/README.md) | Local PDF files | Electricity and gas billing records | Partial — transform implemented; extract, load, finalise are placeholders |

:::

All plugins are registered in `config/plugins/index.properties`, can be selected
together, support dry runs and return standard metrics. Their domain models
remain independent. All use the package structure described in
[Adding a plugin](../guides/adding-a-plugin.md); new implementations should start
from the [Java template](../templates/plugin-java/README.md).

## Plugin pipeline pattern

Each plugin implements a five-step ETL pipeline:

| Step | Package | Responsibility |
|------|---------|----------------|
| Initialise | `initialise` | Load and validate configuration; call remaining steps in order |
| Extract | `extract` | Download source data and make it available for transformation |
| Transform | `transform` | Parse or convert raw data into typed records; validate |
| Load | `load` | Persist records to the database (dry-run: log and skip) |
| Finalise | `finalise` | Clean up temporary files; report final statistics |

All exceptions raised within plugin steps must use
`com.towermarsh.opendata.exception.PluginException` (or another subclass of
`OpenDataException`), not plugin-specific exception types. The `PluginException`
carries the plugin name so failures can be identified in logs without examining
the stack trace.
