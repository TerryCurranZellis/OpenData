# Changelog

All notable OpenData changes are recorded here. The project follows semantic
versioning for published releases; an unreleased development baseline can still
contain release blockers.

## [3.1.0] - Release candidate, not yet tagged

### Added

- Split the source tree into `opendata-api`, `opendata-common`, and `opendata-core` Maven modules while retaining one reactor build.
- Added module-local build verification to `opendata-api` and `opendata-common`, including Java/Maven enforcement, Surefire, Checkstyle, SpotBugs, JaCoCo, Javadoc, and dependency analysis.
- Added new package-level Javadocs for split API/common packages and ADRs covering module boundaries, module-local build verification, and test ownership.

### Changed

- Corrected the reactor module list to build the `opendata-api` directory.
- Replaced the stale `OpenDataPluginSupport` core dependency with the split `opendata-common` module.
- Moved shared JDBC, validation, and HTML link resolver tests into `opendata-common` so tests are owned by the module that compiles the production code.
- Updated repository, architecture, and developer documentation to describe the three-module layout.

## [3.0.0] - Release candidate, not yet tagged

### Added

- JavaFX startup splash with a five-second minimum display before the GUI main window.
- JavaFX main table backed by the persistent plugin registry and latest plugin-run audit.
- Asynchronous GUI plugin loading through a controller/service boundary so SQL Server I/O does not block the JavaFX application thread.
- JavaFX plugin administration for Register, Register from File, Enable, Disable and Unregister.
- JavaFX Plugin Detail dialog backed by the persistent plugin configuration store, with sensitive values masked.
- Read-only JavaFX Settings/Preferences dialog showing effective application configuration without exposing the database password.
- Scrollable JavaFX viewer for the current rotating OpenData JUL log.
- Built-in JavaFX fallback Help window and JavaFX About dialog, including the standalone `--about` command.
- JavaFX Execute and Dry-run actions with selection confirmation, background plugin execution and automatic main-table refresh.
- Modal live execution-log window with a completion-gated Close button and batched thread-safe JUL-to-JavaFX streaming.
- Configuration-folder discovery of unregistered plugin `.properties` definitions with validation and confirmation before registration.
- JavaFX `FileChooser` registration for plugin definitions outside the normal configuration folder.
- Supported JavaFX GUI launcher in `com.towermarsh.opendata.gui`.
- Persistent SQL Server plugin registry in `core.plugin_registry`.
- Command-line plugin administration: `--register`, `--unregister`/`--remove`,
  `--enable`, `--disable` and `--list-plugins`.
