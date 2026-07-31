# Documentation Engine Refactor Change Log

**Date:** 31 July 2026  
**Scope:** Documentation subsystem only

## Added

- Four per-document manifests under `docs/manifests`.
- Shared cover, copyright and revision-history pages.
- A format-aware TOC Lua filter.
- Manifest-driven architecture and migration documentation.
- ADR-0045 and ADR-0046.
- A manifest-driven documentation-engine diagram.

## Changed

- Refactored `Invoke-Documentation.ps1` to discover and process manifests generically.
- Made `-Action All` build every discovered manifest.
- Removed fixed Technical/User document validation and branching.
- Moved common document defaults into `config/documentation.json`.
- Changed assembly order so all front matter precedes the TOC.
- Suppressed writer-generated HTML/PDF title blocks and enabled DOCX field refresh.
- Corrected Azure Pipelines to call the maintained validation and build wrappers.
- Converted the retired branding patch into a no-op compatibility stub.
- Aligned the PlantUML configuration with `tools/plantuml.jar`.
- Updated documentation validation to validate every manifest.
- Updated PlantUML invocation so it no longer runs a hard-coded local path.
- Updated documentation commands, standards, indexes and the existing generation-flow diagram.

## Removed from maintained behaviour

- The combined `docs/manifest.json` as a build input.
- Pandoc's global `--toc` switch.
- Unconditional local-machine invocations at the end of maintained PowerShell scripts.

## Java impact

None. No Java source or SQL source is changed by this refactor.

## Validation performed

- Parsed the global configuration, deprecated pointer and all four document manifests as JSON.
- Confirmed every manifest section or glob resolves to Markdown and every output/id is unique.
- Ran repository-wide Markdown heading, link and canonical PlantUML-location checks: zero errors and zero warnings.
- Assembled all four documents and passed them through Pandoc for HTML, DOCX and LaTeX output.
- Inspected HTML, DOCX XML and LaTeX output to confirm cover, copyright, revision history, TOC and chapters occur in the required order.
- Confirmed Java and SQL source trees are unchanged.
- Performed a structural delimiter/string scan of the PowerShell files. A native Windows PowerShell 5.1 execution remains part of the Windows CI validation path.
