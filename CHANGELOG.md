# Changelog

All notable OpenData changes are recorded here. The project follows semantic
versioning for published releases; an unreleased development baseline can still
contain release blockers.

## [Unreleased]

### Added

- JavaFX startup splash with a five-second minimum display before the GUI main window.
- JavaFX main table backed by the persistent plugin registry and latest plugin-run audit.
- Asynchronous GUI plugin loading through a controller/service boundary so SQL Server I/O does not block the JavaFX application thread.
- JavaFX plugin administration for Register, Register from File, Enable, Disable and Unregister.
- Configuration-folder discovery of unregistered plugin `.properties` definitions with validation and confirmation before registration.
- JavaFX `FileChooser` registration for plugin definitions outside the normal configuration folder.
- Supported JavaFX GUI launcher in `com.towermarsh.opendata.gui`.
- Persistent SQL Server plugin registry in `core.plugin_registry`.
- Command-line plugin administration: `--register`, `--unregister`/`--remove`,
  `--enable`, `--disable` and `--list-plugins`.
- Repeated `--plugin` selection and `--plugin all` support across runs and
  administration commands.
- External single-plugin registration with
  `--plugin <id> --register --file <filename>`.
- `--verbose` and bounded `--parallelism 1-64` validation for the expanded CLI.
- New `-n` short option for `--dry-run`; `-d` is reserved for `--disable`.
- Explicit `--Execute` / `-x` execution authorisation required for normal and
  dry-run plugin execution.
- Unix-style `opendata(1)` manual page source in `docs/reference/opendata.1`.
- SQL migration and grants for persistent plugin metadata/status.

### Changed

- The minimum supported Java runtime is now Java 24; GitHub build and release workflows verify on Java 24.
- Legacy Swing UI helpers are deprecated from version 3.1.0 while JavaFX replacements are completed.
- Selecting `--plugin <id|all>` no longer starts execution by itself. Normal and
  dry-run execution must include `--Execute` or `-x`.
- `--Execute` is rejected with register, unregister/remove, enable and disable
  administration operations.
- Normal execution now selects only plugins that are both registered and
  enabled in SQL Server.
- Packaged classpath plugin definitions are a registration catalogue rather than
  the authoritative runtime registry.
- `--file` is no longer a run-time override; it is accepted only for registering
  one named plugin.
- Octopus dry run no longer reads the processed-file ledger and is compatible
  with the framework's unavailable dry-run database resource.
- Obsolete override configuration classes and tests were removed.

### Planned or under evaluation

- Correct the keystore-password environment-variable implementation.
- Remove tracked deployment secrets/private keys and establish key rotation.
- Validate SQL Server certificate trust and least-privilege deployment.
- Replace or explicitly approve the preview SQL Server JDBC dependency.
- Produce and verify a self-contained executable distribution.
- Evaluate, separately, whether an Octopus API can support authorised statement
  acquisition; no such feature is implemented.

## [2.0.0] - Release candidate, not yet tagged

### Added

- Database-backed application and plugin configuration registration.
- RSA OAEP encryption/decryption of the bootstrap database password.
- Common `initialise`, `extract`, `transform`, `load`, `finalise` plugin flow.
- Bounded parallel plugin execution and contextual JUL logging.
- Local Octopus PDF discovery, filename/SHA-256 completion ledger,
  transactional load and post-commit archive.
- Manifest-driven Technical, Administrator, Developer and API manuals.
- Expanded architecture, operations, developer, governance and release evidence
  documentation.

### Known limitations

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
