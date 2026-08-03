# Changelog

All notable changes to OpenData are documented in this file. The project follows
semantic versioning.

## [Unreleased]

### Planned

- Additional plugins and data sources.
- Possible Octopus Energy API extraction after API capabilities and terms are
  separately evaluated.
- Executable packaging with an explicit main class and dependency strategy.

## [2.0.0] - Unreleased

### Added

- Database-backed application and plugin configuration registration.
- `--register` control command.
- Minimal bootstrap configuration containing only version, database mode,
  database URL, database username and database password.
- RSA OAEP encryption of the bootstrap database password using an X.509 public
  certificate and PKCS#12 private key store.
- Source-tree and classpath lookup for configuration certificate resources.
- PFX password override through `opendata.config.keystore.password` and
  `OPENDATA_CONFIG_KEYSTORE_PASSWORD`.
- Standard plugin packages: `initialise`, `extract`, `transform`, `load` and
  `finalise`.
- Central plugin exception handling without plugin-local exception hierarchies.
- Octopus Energy local PDF discovery and batch processing.
- Octopus statement-file ledger with filename and SHA-256 duplicate prevention.
- Transactional Octopus electricity, gas and file-ledger persistence.
- Successful-statement archiving after non-dry-run processing.
- Per-document manifests for the Technical User Guide, Administrator Guide,
  Developer Guide and API Reference.
- Shared document front matter and manifest-driven document composition.

### Changed

- Runtime configuration is loaded from SQL Server after bootstrap when database
  mode is enabled.
- Root plugin classes are thin framework entry points; plugin-specific initialise
  classes control execution flow.
- Extract stages obtain data and pass it to transform stages rather than mixing
  acquisition, transformation and persistence.
- Documentation generation builds every discovered document manifest.
- Front matter is assembled before the table of contents in generated formats.
- Documentation and release material now identify Version 2.0.0 as the active
  architectural baseline.

### Fixed

- PFX password handling no longer treats `nopassword` as an environment-variable
  name.
- Certificate lookup works from both the development source tree and packaged
  classpath resources.
- Octopus files are not recorded as completed unless their associated database
  transaction commits.

### Removed

- Plugin-specific exception packages from the standard plugin architecture.
- The combined documentation manifest from the maintained build path.
- Hard-coded Technical/User document branching in the documentation engine.

## [1.0.0] - 2026-07-29

### Added

- Initial Java 17 modular-monolith framework.
- Properties-based plugin registry and command-line processing.
- Ofgem and OpenMeteo reference plugins.
- Concurrent plugin execution and side-effect-free dry runs.
- SQL Server persistence, connection pooling and plugin-run auditing.
- Configuration-driven documentation framework and reusable templates.
- Repository governance, contribution, security and licensing standards.
- Checkstyle, PMD, SpotBugs, Javadoc and JaCoCo quality tooling.
- GitHub Actions build, documentation and tagged-release automation.

[Unreleased]: https://github.com/TerryCurranZellis/OpenData/compare/v2.0.0...HEAD
[2.0.0]: https://github.com/TerryCurranZellis/OpenData/releases/tag/v2.0.0
[1.0.0]: https://github.com/TerryCurranZellis/OpenData/releases/tag/v1.0.0
