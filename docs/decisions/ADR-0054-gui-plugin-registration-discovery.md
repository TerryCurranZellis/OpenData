# ADR-0054: Discover GUI plugin registrations from configuration files

**Status:** Accepted  
**Date:** 14 August 2026  
**Decision owners:** OpenData project  
**Version:** 3.1.0

---

## Context

The JavaFX Register command must make newly supplied plugin definitions easy to
install without requiring an unregistered plugin to appear in the main plugin
table first. The GUI specification also provides a separate **Register from
File** command for definitions located outside the normal configuration folder.

The command-line interface already has packaged registration semantics based on
the explicit classpath plugin catalogue. Those semantics are useful for the CLI
and must not be changed merely to implement a desktop workflow.

## Decision

The JavaFX administration layer uses two registration routes:

1. **Register** scans OpenData plugin configuration folders for complete
   `*.properties` definitions, validates them, removes definitions whose
   `plugin.id` is already registered, presents the remaining discoveries for
   confirmation, and registers the confirmed set.
2. **Register from File** opens a JavaFX `FileChooser`, reads `plugin.id` from the
   selected complete properties file, validates the definition, and registers
   that one plugin.

The scanner checks `config/plugins` beneath the process working directory first.
While running from a development checkout it also checks
`src/main/resources/config/plugins`. `index.properties` is an index/catalogue,
not a plugin definition, and is ignored by GUI discovery.

Duplicate files that declare the same unregistered `plugin.id` are treated as a
configuration error rather than selecting one definition implicitly.

Registration parsing and implementation-class validation are shared with the
existing CLI through `PluginRegistrationResolver`. Persistent writes continue to
use `ConfigurationRegistrationService` and `JdbcPluginRegistry`.

Enable, Disable and Unregister operate only on explicitly checked main-table
rows, require confirmation, and execute away from the JavaFX application
thread.

## Consequences

- A new plugin can be added to the normal configuration folder and discovered
  without editing the JavaFX table or a GUI-specific catalogue.
- A definition elsewhere on disk can still be registered directly.
- Re-registering an existing plugin remains possible through **Register from
  File**, which retains the existing registry replacement behaviour.
- Normal Register does not unexpectedly overwrite a registered plugin.
- The GUI and CLI share definition validation but are free to expose workflows
  appropriate to their interfaces.
- The development source-tree fallback should eventually be reviewed when a
  final installation layout is established.

## Alternatives considered

### Select unregistered packaged plugins from a dialog

Rejected. An unregistered plugin should be discovered from the configuration
folder rather than requiring a separate GUI catalogue-selection model.

### Change CLI `--register` to scan the filesystem

Rejected. This would alter an established command-line contract solely to serve
the desktop workflow.

### Automatically register without confirmation

Rejected. Registration changes persistent application state; the user should see
which new definitions were found before the write occurs.
