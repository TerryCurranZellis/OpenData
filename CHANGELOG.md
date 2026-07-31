## [Unreleased]

### Added

- Per-document manifests for the Technical User Guide, Administrator Guide, Developer Guide and API Reference.
- Shared cover, copyright and revision-history Markdown.
- A format-aware TOC filter and manifest-driven documentation architecture.
- ADR-0045 and ADR-0046, migration notes and a documentation-engine change log.

### Changed

- Refactored the documentation builder into a generic manifest processor.
- `Invoke-Documentation -Action All` now discovers and builds every manifest.
- Front matter is assembled before the table of contents in HTML, DOCX and PDF.
- HTML and PDF writer-generated title blocks are suppressed so the cover remains first.
- DOCX output requests automatic refresh of the native Word TOC field.
- Azure Pipelines now validates and builds every discovered manifest through maintained wrappers.
- `documentation.json` now contains global settings and inherited defaults only.
- PlantUML and documentation scripts no longer contain unconditional local-machine invocations.
- The legacy branding patch is now a safe no-op compatibility stub.
- The configured PlantUML JAR path now matches `tools/plantuml.jar`.

### Removed

- The combined `docs/manifest.json` from the maintained build path.
- Hard-coded Technical/User document branching and the global Pandoc `--toc` option.

## [1.0.0] - 2026-07-29

### Added

- Configuration-driven documentation framework and reusable templates.
- Repository governance, contribution, security and licensing standards.
- Standard Apache 2.0 source headers and licensing policy.
- Checkstyle, PMD, SpotBugs, Javadoc and JaCoCo quality tooling.
- GitHub Actions workflows for build, documentation and tagged releases.
- Documentation validation, local release packaging and release checksums.
- Final generated technical documentation and user guide packages.

### Changed

- Documentation generation now uses `documentation.json` and `docs/manifest.json`.
- Build and release processes now enforce Java 17, Maven 3.9 and version consistency.
- Repository documentation and onboarding material were reorganised for public use.

### Fixed

- Removed machine-specific execution paths from utility scripts.
- Corrected documentation workflow references to missing scripts.
- Added missing package documentation and completed diagram inventory metadata.

## [0.1.0] - 2026-07-22

### Added

- Initial Java 17 modular-monolith framework.
- Properties-based plugin registry and command-line processing.
- SQL Server persistence direction and database scripts.
- Ofgem and OpenMeteo reference plugins.
- Architecture, ADR, guide, operations and reference documentation.

[Unreleased]: https://github.com/TerryCurranZellis/OpenData/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/TerryCurranZellis/OpenData/releases/tag/v1.0.0
[0.1.0]: https://github.com/TerryCurranZellis/OpenData/releases/tag/v0.1.0
