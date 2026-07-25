# Documentation Audit

**Document ID:** DOC-AUDIT-002  
**Version:** 2.0  
**Status:** Completed against uploaded source baseline  
**Audit date:** 24 July 2026

## Scope

The audit compared Markdown, PlantUML sources, configuration, SQL scripts and Java package/class names in the uploaded repository. It also checked relative Markdown links and the documentation build configuration.

## Corrections made

- Rebuilt the entire diagram set using standard PlantUML syntax and a flat `docs/diagrams/source` directory.
- Removed duplicate `.puml` files from `docs/diagrams` and the obsolete `docs/diagrams/database` directory.
- Reserved `docs/diagrams/generated` for converted SVG or PNG output.
- Changed document illustrations from `.puml` targets to embedded `generated/*.svg` targets.
- Updated the diagram renderer and validation rules for the source/generated contract.
- Updated command-line documentation for repeated/comma-separated plugins, `all` and `--parallelism`.
- Updated configuration documentation for the current `ApplicationRuntimeConfiguration` path and identified legacy single-plugin classes.
- Corrected pool defaults to match `config/application.properties`.
- Documented that the repository currently contains two different audit/schema foundations: `core.PluginRun` for the active plugin runtime and `core.ingestion_run` for the earlier Phase 3 foundation.
- Corrected Ofgem status: service, workbook extraction, repository and SQL foundations exist, but the configured `com.towermarsh.opendata.plugin.ofgem.OfgemPlugin` implementation is absent from the uploaded source baseline.

## Remaining implementation issues reflected in the documentation

1. The Ofgem descriptor cannot create its configured plugin class until that class is implemented or the descriptor is changed.
2. The duplicate audit models require an ADR and migration before they can be presented as one production schema.
3. Transitional single-plugin classes duplicate current CLI/configuration concepts and should be removed or explicitly retained.
4. Generated SVG files are intentionally not produced by this documentation-only change. Run the documented PlantUML build before producing HTML, DOCX or PDF output.

## Verification

- Every maintained `.puml` file contains one `@startuml` and one `@enduml` marker.
- PlantUML source basenames are unique and map directly to generated image basenames.
- No documentation illustration points to a `.puml` file.
- Relative non-generated Markdown links resolve in the revised tree.
