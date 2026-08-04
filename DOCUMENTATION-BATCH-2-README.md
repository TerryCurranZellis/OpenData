# OpenData documentation update — Batch 2: Plugin and release completion

## Purpose

This batch completes the documentation for the shared validation and JDBC
processing refactor delivered by code Batches 0–3 and Documentation Batch 1.
It updates the provider-specific architecture, reference, plugin-development,
release and audit material for OpenData Version 2.0.0.

Apply this archive after:

1. `OpenData-Refactor-Batch-0.zip`;
2. `OpenData-Refactor-Batch-1-Ofgem.zip`;
3. `OpenData-Refactor-Batch-2-OpenMeteo.zip`;
4. `OpenData-Refactor-Batch-3-Octopus.zip`; and
5. `OpenData-Documentation-Batch-1-Shared-Framework.zip`.

The original repository baseline inspected was `main` commit
`d352a0015aa46f436512182523fbfffe628c22fa`. This patch assumes the preceding
local batches are already present on the same branch.

## Delivered documentation

- Ofgem configuration, transaction and batch persistence design;
- OpenMeteo typed configuration, safe SQL identifiers, staging and pooled-session
  cleanup;
- Octopus generic electricity/gas upsert adapters and atomic ledger completion;
- updated architecture traceability and current-code inventory;
- updated Version 2.0.0 release record and revision history;
- updated Java plugin template using shared validation and JDBC infrastructure;
- refreshed provider persistence diagrams in PlantUML and committed SVG form;
- final documentation audit for the processing refactor; and
- generated-document manifest and documentation-index updates.

## Compatibility and API documentation

All new or amended public Version 2.0.0 code is documented as `@since 2.0.0`.
A retained obsolete public procedure must use both Java `@Deprecated` and the
Javadoc `@deprecated` tag. The retained
`OpenMeteoConfiguration.sqlIdentifier(...)` compatibility method is documented
accordingly. Removed private helper methods are not retained as dead deprecated
wrappers.

## Installation

From the repository root on the local refactor branch:

```powershell
Expand-Archive -Path .\OpenData-Documentation-Batch-2-Plugin-Completion.zip `
    -DestinationPath . -Force

git status
git diff --check
mvn clean verify

.\scripts\Invoke-Documentation.ps1 -Action Validate
.\scripts\Invoke-Documentation.ps1 -Action All -RenderDiagrams
```

Review generated DOCX/PDF output, especially the landscape provider diagrams,
before declaring the refactor complete.

## Completion boundary

The source documentation for this processing refactor is complete after this
batch. Production-release acceptance remains dependent on the repository's
existing live SQL Server, permissions, rollback, archive, security and packaging
gates. Those gates are not silently converted into completed evidence by a
documentation update.
