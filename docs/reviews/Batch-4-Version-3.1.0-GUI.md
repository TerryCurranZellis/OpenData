# Batch 4 — JavaFX Plugin Administration

**Document ID:** GUI-BATCH-004  
**Version:** 3.1.0  
**Completed:** 14 August 2026  
**Branch:** `feature/gui-v3.1`

---

## Scope

Batch 4 connects the JavaFX Register, Register from File, Unregister, Enable and
Disable commands to the existing persistent plugin-registry/configuration
services. Execute, Dry-run and information dialogs remain later-batch work.

## Implemented

- **Register** scans the standard OpenData plugin configuration folders for
  `*.properties` definitions.
- `index.properties` is excluded from discovery.
- Definitions are fully parsed and validated before registration is offered.
- Files whose `plugin.id` is already registered are omitted from normal Register.
- Duplicate discovered files declaring the same plugin id are reported as an
  error.
- The discovered plugin list is shown in a confirmation dialog before database
  changes are made.
- **Register from File** uses JavaFX `FileChooser` and reads the plugin id from
  the selected definition.
- **Enable**, **Disable** and **Unregister** use the explicit Selected checkboxes,
  warn when nothing is selected, and require OK/Cancel confirmation.
- Administration database work runs in JavaFX `Task` workers and the table is
  refreshed after successful changes.
- Administration actions are disabled while a load or state-changing operation
  is in progress.

## Configuration-folder search order

1. `<working directory>/config/plugins`
2. `<working directory>/src/main/resources/config/plugins` (development
   checkout fallback)

Only regular `*.properties` files are candidates. The second location preserves
current source-tree development behaviour while allowing a deployment-style
configuration directory to become the primary location.

## Shared application changes

`PluginRegistrationResolver` extracts definition resolution and implementation
class validation previously embedded in `OpenDataApplication`. The CLI continues
to use its existing packaged catalogue semantics; the refactor only prevents the
GUI and CLI from implementing different validation rules.

## Validation added

- plugin configuration directory scanning tests;
- packaged and external plugin registration resolver tests;
- source/FXML action wiring validation;
- documentation manifest/path validation.

## Deferred

- Execute and Dry-run with a live JavaFX log window;
- Plugin Detail, Logs, Settings, Help and JavaFX About;
- final installer/runtime configuration-directory policy;
- full end-to-end JavaFX interaction testing.
