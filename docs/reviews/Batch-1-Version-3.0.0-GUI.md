# Batch 1 Version 3.0.0 GUI Implementation Notes

**Completed:** 12 August 2026  
**Scope:** JavaFX main-page presentation baseline and supporting documentation

## Added

- Version 3 JavaFX package documentation.
- Explicit checkbox-based plugin selection.
- Main-window menu and toolbar layout aligned to the GUI specification.
- Plugin table columns for identifier, description, enabled state, last-run
  status and last-run date.
- Status-bar loading/ready text and selected-item count.
- Logfile toolbar image resource.
- ADR-0051 for the JavaFX graphical-interface decision.
- JavaFX GUI architecture, user-guide page and screenshot plan.

## Changed

- Removed prototype `All`/`Selected` menu variants that are not in the version 3
  GUI specification.
- Added the missing Details menu.
- Replaced prototype Category/Name/Last Updated columns with the written
  specification columns.
- Kept Batch 1 event handlers presentation-only so no production registry,
  database, plugin or CLI operation is changed by this batch.

## Deferred

- JavaFX splash screen and remaining Swing migration.
- Persistent registry loading.
- Administration dialogs and service integration.
- Plugin detail, log, help and About dialogs.
- Execute/Dry-run background tasks and live JUL logging.
- Windows packaging and final screenshot capture.

See [JavaFX GUI architecture](../development/javafx-gui-architecture.md) for the
proposed batch sequence and integration hurdles.
