# Plugin Documentation

**Document ID:** PLUGIN-INDEX-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

::: {.docx-linear-table}

| Plugin | Source type | Persistence | Status |
|---|---|---|---|
| [Ofgem](ofgem/README.md) | HTML discovery and XLSX | Transactional period replacement | Implemented; live write acceptance pending |
| [OpenMeteo](openmeteo/README.md) | JSON API | Idempotent location/date upsert | Implemented; live write acceptance pending |

:::

Both plugins are registered in `config/plugins/index.properties`, can be selected
together, support dry runs and return standard metrics. Their domain models
remain independent. Both use the package structure described in
[Adding a plugin](../guides/adding-a-plugin.md); new implementations should start
from the [Java template](../templates/plugin-java/README.md).
