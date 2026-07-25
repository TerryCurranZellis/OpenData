# Diagram sources and generated images

**Document ID:** DIAG-INDEX-001  
**Version:** 2.0  
**Status:** Maintained  
**Baseline date:** 24 July 2026

## Directory contract

- `source/` contains the authoritative, hand-maintained PlantUML `.puml` files.
- `generated/` contains the rendered `.svg` or `.png` files used by Markdown and final documents.
- PlantUML source files must not be placed directly in `docs/diagrams` or in additional source subfolders.
- Markdown must embed files from `generated/`; it must not embed or link to `.puml` as the document illustration.

The default documentation build renders SVG because SVG remains sharp in HTML, DOCX and PDF workflows. The generated filenames match the PlantUML source basename.

## Sources

- [`command-line-flow.puml`](source/command-line-flow.puml) → `generated/command-line-flow.svg`
- [`component-architecture.puml`](source/component-architecture.puml) → `generated/component-architecture.svg`
- [`configuration-loading-sequence.puml`](source/configuration-loading-sequence.puml) → `generated/configuration-loading-sequence.svg`
- [`database-architecture.puml`](source/database-architecture.puml) → `generated/database-architecture.svg`
- [`database-persistence-components.puml`](source/database-persistence-components.puml) → `generated/database-persistence-components.svg`
- [`database-persistence-sequence.puml`](source/database-persistence-sequence.puml) → `generated/database-persistence-sequence.svg`
- [`dataset-lifecycle.puml`](source/dataset-lifecycle.puml) → `generated/dataset-lifecycle.svg`
- [`download-strategy-classes.puml`](source/download-strategy-classes.puml) → `generated/download-strategy-classes.svg`
- [`ingestion-run-state.puml`](source/ingestion-run-state.puml) → `generated/ingestion-run-state.svg`
- [`ofgem-price-cap-import-sequence.puml`](source/ofgem-price-cap-import-sequence.puml) → `generated/ofgem-price-cap-import-sequence.svg`
- [`opendata-database.puml`](source/opendata-database.puml) → `generated/opendata-database.svg`
- [`openmeteo-data-model.puml`](source/openmeteo-data-model.puml) → `generated/openmeteo-data-model.svg`
- [`openmeteo-persistence.puml`](source/openmeteo-persistence.puml) → `generated/openmeteo-persistence.svg`
- [`package-dependencies.puml`](source/package-dependencies.puml) → `generated/package-dependencies.svg`
- [`pipeline-sequence.puml`](source/pipeline-sequence.puml) → `generated/pipeline-sequence.svg`
- [`plugin-execution-sequence.puml`](source/plugin-execution-sequence.puml) → `generated/plugin-execution-sequence.svg`
- [`plugin-registry.puml`](source/plugin-registry.puml) → `generated/plugin-registry.svg`
- [`sql-server-deployment.puml`](source/sql-server-deployment.puml) → `generated/sql-server-deployment.svg`
- [`system-context.puml`](source/system-context.puml) → `generated/system-context.svg`

## Rendering

```powershell
.\scripts\documentation\Invoke-Documentation.ps1 `
    -Action Build `
    -RenderDiagrams `
    -DiagramFormat svg
```

Generated images are build products. They may be committed when the published Markdown must render directly from the repository; otherwise they are recreated before document generation.
