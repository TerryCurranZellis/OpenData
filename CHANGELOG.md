# Changelog

All notable OpenData changes are recorded here. The project follows semantic
versioning for published releases; an unreleased development baseline can still
contain release blockers.

## [Unreleased]

### Planned or under evaluation

- Correct the keystore-password environment-variable implementation.
- Remove tracked deployment secrets/private keys and establish key rotation.
- Correct Octopus dry-run database-ledger access.
- Validate SQL Server certificate trust and least-privilege deployment.
- Replace or explicitly approve the preview SQL Server JDBC dependency.
- Produce and verify a self-contained executable distribution.
- Evaluate, separately, whether an Octopus API can support authorised statement
  acquisition; no such feature is implemented.

## [2.0.0] - Release candidate, not yet tagged

### Added

- Database-backed application and plugin configuration registration.
- `--register` control command and minimal post-registration bootstrap file.
- RSA OAEP encryption/decryption of the bootstrap database password.
- Common `initialise`, `extract`, `transform`, `load`, `finalise` plugin flow.
- Bounded parallel plugin execution and contextual JUL logging.
- Local Octopus PDF discovery, filename/SHA-256 completion ledger,
  transactional load and post-commit archive.
- Manifest-driven Technical, Administrator, Developer and API manuals.
- Expanded architecture, operations, developer, governance and release evidence
  documentation.

### Changed

- Most runtime/plugin properties move from classpath files to SQL Server after
  registration.
- Plugin-specific local exception hierarchies are replaced by shared framework
  exceptions and boundary handling.
- Documentation describes actual limitations rather than presenting planned
  features as implemented.

### Known limitations

- Octopus dry-run is incompatible with the framework's no-database dry-run
  resource model.
- Tracked development credential/private-key artifacts must not be used for
  production.
- The intended environment-variable keystore password route is defective.
- Development SQL Server configuration does not validate the server certificate.
- `mssql-jdbc` is currently a preview version.
- Executable JAR packaging has not been proven.

## [1.0.0] - 29 July 2026

Initial documented public baseline with the core framework, Ofgem and OpenMeteo
plugins, SQL Server persistence, dry-run support and documentation automation.
This release record is historical and does not describe the current 2.0.0 code.

[1.0.0]: https://github.com/TerryCurranZellis/OpenData/releases/tag/v1.0.0
