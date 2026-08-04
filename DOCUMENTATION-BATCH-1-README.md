# OpenData documentation refresh — Batch 1: Shared framework

## Purpose

This is the first documentation batch following the Version 2.0.0 shared
validation and persistence refactor. It documents the provider-neutral
foundation before the individual Ofgem, OpenMeteo and Octopus documents are
updated in the next batch.

The code batches 0 through 3 must already be applied to the local branch.

Repository documentation baseline inspected: `main` commit
`d352a0015aa46f436512182523fbfffe628c22fa`.

## Included documentation

- new architecture chapter `ARCH-028`;
- new accepted decision `ADR-0049` and updated ADR register;
- new shared validation and JDBC API reference;
- updated database persistence and pooling architecture;
- updated plugin-authoring guide and package standard;
- updated plugin-property reference;
- updated architecture, development, reference and diagram indexes;
- updated Developer Guide and API Reference manifests;
- updated PlantUML and committed SVG for shared validation/persistence.

## Compatibility and API lifecycle documentation

The documents record the project rule that materially adjusted public APIs use
Javadoc `@since 2.0.0`. A retained obsolete public procedure uses both Java
`@Deprecated` and Javadoc `@deprecated`. Private helpers without external
callers are removed rather than kept as dead compatibility wrappers.

## Installation

Extract this archive into the repository root after the four code batches:

```powershell
Expand-Archive -Path .\OpenData-Documentation-Batch-1-Shared-Framework.zip `
    -DestinationPath . -Force

git status
git diff --check
mvn clean verify
.\scripts\Invoke-Documentation.ps1 -Action Validate
.\scripts\Invoke-Documentation.ps1 -Action All -RenderDiagrams
```

The exact documentation command parameters should follow the current local
script help if they have changed after the inspected baseline.

## Next documentation batch

Batch 2 will update the Ofgem, OpenMeteo and Octopus architecture, plugin,
configuration, schema, user-guide, release and traceability documentation. It
will also revise their provider-specific sequence diagrams where necessary.
