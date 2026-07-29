# Changelog

All notable changes to OpenData are recorded in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and
this project uses semantic versioning for published releases.

## [Unreleased]
### Changed

- Standardised Java, PowerShell and SQL source headers with Apache-2.0 SPDX identifiers.
- Added a canonical plain-text `LICENSE` and project licensing policy.


### Added

- Configuration-driven documentation framework and document manifest.
- Reusable documentation templates and shared manual sections.
- Root-level contributor, conduct, security and notice files.
- Repository standards and open-source release guidance.

### Changed

- Documentation generation now consumes `config/documentation.json` and
  `docs/manifest.json` rather than relying only on hard-coded source lists.
- Community and release information now uses conventional repository-root files.
- The project README now provides a clearer status statement, quick start and
  contributor navigation.

### Fixed

- Removed machine-specific invocations from documentation scripts.
- Corrected documentation renderer references and PlantUML source locations.

## [1.0.0] - Unreleased

This version is reserved for the first supported public release. Production
acceptance, executable packaging and final database verification remain release
gates and must be completed before this heading receives a release date.

## [0.1.0] - 2026-07-22

### Added

- Initial Java 17 modular-monolith framework.
- Properties-based plugin registry and command-line processing.
- SQL Server persistence direction and database scripts.
- Ofgem and OpenMeteo reference plugins.
- Architecture, ADR, guide, operations and reference documentation.

[Unreleased]: https://github.com/TerryCurranZellis/OpenData/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/TerryCurranZellis/OpenData/releases/tag/v0.1.0
