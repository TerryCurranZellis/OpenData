# Batch 3 Version 3.1.0 GUI Implementation Notes

**Completed:** 13 August 2026  
**Scope:** Persistent plugin table, latest-run status, asynchronous loading and GUI service boundary

## Added

- `PluginTableEntry`, a plain immutable data record used between backend reads
  and JavaFX presentation mapping.
- `PluginTableDataService`, combining persistent `PluginRegistry` metadata with
  the latest `core.PluginRun` row for each plugin.
- `PluginTableDataLoader`, responsible for bootstrap configuration, database
  pool lifecycle and construction of the read-only GUI service.
- Unit tests for registry/run-audit composition and `PluginRow` presentation
  mapping.
- ADR-0053 documenting the asynchronous controller/service boundary.

## Changed

- `OpenDataMainController` now loads plugin data with a JavaFX `Task` instead of
  creating sample rows.
- The status bar displays `Loading plugin details...` during the database read
  and `Ready` only after the table has been populated successfully.
- The table placeholder distinguishes loading, no registered plugins and load
  failure.
- `PluginRow` now maps real persistent values and displays stored UTC run times
  in the workstation's local time zone.

## Data displayed

The table uses the database-backed registry for:

- plugin identifier;
- plugin description; and
- enabled/disabled state.

The most recent `core.PluginRun` row supplies:

- status; and
- run start date/time.

If a registered plugin has never been run, both run columns remain blank as
required by the GUI specification.

## Threading

Database and configuration work does not run on the JavaFX application thread.
`OpenDataMainController` starts a daemon worker containing a JavaFX `Task`. The
task's success or failure handler updates the controls on the JavaFX thread.

A refresh cancels any previous in-flight GUI load before starting another one.
This refresh boundary is intentionally reusable by later administration
operations.

## Failure behaviour

A plugin-table load failure:

- is written to JUL;
- leaves the table empty and disabled;
- changes the table placeholder to `Plugin details could not be loaded`; and
- changes the lower-left status to `Unable to load plugin details`.

No new JavaFX warning dialog is introduced in this batch; common warning and
confirmation dialogs remain part of the administration/dialog batches.

## Deferred

- Register/enable/disable/unregister actions and confirmation dialogs (Batch 4).
- JavaFX Settings, Detail, Logs, Help and About dialogs (Batch 5).
- Execute/Dry-run background tasks and live JUL log display (Batch 6).
- Final integration, packaging and screenshot completion (Batch 7).
