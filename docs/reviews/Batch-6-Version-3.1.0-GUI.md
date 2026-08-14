# Batch 6 Version 3.1.0 GUI implementation review

**Document ID:** REVIEW-GUI-BATCH-006
**Version:** 3.1.0
**Date:** 14 August 2026
**Status:** Ready for local integration testing

---

## Scope

Batch 6 connects the JavaFX Execute and Dry-run actions to the existing OpenData
plugin execution framework and implements the live execution-log window required
by the Version 3 GUI specification.

## Implementation summary

### Selection and confirmation

Execute and Dry-run require one or more rows checked in the **Selected** column.
The controller snapshots those plugin ids before the confirmation dialog opens.
If no plugin is checked, the normal **No plugin selected** warning is displayed.

Execute confirms that persistent plugin data may be inserted or updated. Dry-run
confirms that extraction/transformation still occurs but provider database writes
and generic run-audit rows are disabled.

### Background execution

`PluginExecutionGateway` runs on a JavaFX `Task`. It opens the bootstrap SQL
Server pool long enough to resolve the persistent registry, runtime configuration
and plugin definitions, then closes that pool before starting plugin processing.
Normal execution reopens SQL Server with the runtime database configuration and
uses `JdbcPluginRunAudit`; Dry-run uses `UnavailableDatabaseResourceManager` and
`NoOpPluginRunAudit`.

`PluginSelectionResolver` now has an explicit plugin-id overload so GUI and CLI
selection use the same canonical id and enabled-plugin validation rules. GUI
parallelism comes from the existing `execution.max-parallel-plugins` setting.

### Live JUL window

`JavaFxLogHandler` is attached temporarily to the OpenData application logger.
It does not replace console/file handlers. Because runtime logging configuration
only replaces root handlers, the scoped live handler remains attached when the
run switches to its configured logging settings.

Concurrent log records are formatted with `ContextualLogFormatter`, queued and
forwarded to JavaFX in batches using `Platform.runLater()`. The execution window
contains a non-editable, non-wrapping text area with vertical and horizontal
scrolling.

The window is modal. Its **Close** button is disabled and window-close requests
are consumed until processing finishes. Once the task completes, the window
reports success/failure counts and enables **Close**. Setup failures are appended
to the same window before Close becomes available.

### Main table refresh

After execution finishes the main table is reloaded. A normal run therefore
shows the latest persisted run status/date. A Dry-run does not create generic
run-audit rows, so the previous last-run status/date remain unchanged by design.

## Test focus after overlay

1. Select one enabled plugin and cancel Execute; confirm that nothing runs.
2. Select one enabled plugin, confirm Execute and verify the live window begins
   receiving JUL output while the plugin runs.
3. Verify **Close** and the window close decoration cannot dismiss the live
   window until processing finishes.
4. Run multiple enabled plugins and verify their contextual logs appear in the
   same window while configured parallelism is respected.
5. Run Dry-run and verify output is live but no provider writes or generic
   `core.PluginRun` audit rows are produced.
6. Select a disabled plugin and verify execution is rejected by the persistent
   registry validation path.
7. Force a plugin failure and verify the window remains usable, shows the
   failure summary, and enables Close at completion.
8. After a normal execution, close the log window and verify the main table shows
   the new last-run status/date.
9. Run the complete Maven test suite under JDK 24/25.

## Deferred

- final Windows compiled Help launch/packaging;
- final end-to-end JavaFX automation/packaging tests;
- any future editable Settings model; and
- optional execution cancellation if it is added to a later specification.

---
