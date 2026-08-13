# Batch 2 Version 3.1.0 GUI Implementation Notes

**Completed:** 13 August 2026  
**Scope:** JavaFX startup lifecycle, splash screen, package cleanup, Java 24 CI baseline and Swing deprecation

## Added

- `com.towermarsh.opendata.gui.GuiLauncher` as the supported GUI launch boundary.
- `OpenDataSplashScreen`, implemented entirely with JavaFX.
- A GUI-owned copy of the OpenData splash image resource.
- A five-second minimum splash-display policy implemented with
  `PauseTransition`, without blocking the JavaFX application thread.
- ADR-0052 documenting Java 24, JavaFX lifecycle ownership and staged Swing
  retirement.

## Changed

- `OpenDataGuiApplication` now displays the JavaFX splash before the main window.
- `OpenData.main` launches the supported GUI launcher from the GUI package.
- The GUI launcher source/package mismatch from the prototype has been removed.
- The GitHub build and release workflows now verify on Java 24.
- GUI and build documentation now records Java 24 as the minimum supported
  runtime.

## Deprecated

The following transitional `com.towermarsh.opendata.ui` helpers are marked
`@Deprecated(since = "3.1.0")` rather than being deleted:

- `AboutDialog`;
- `StartupSplashScreen`;
- `OpenDataImageLoader`; and
- the compatibility `ui.GuiLauncher` wrapper.

The Swing About dialog and the legacy command-line run splash remain available
for compatibility until their final migration decision. No graphical JavaFX
startup code uses Swing.

## Lifecycle behaviour

`Application.launch(...)` returns only after the JavaFX application has exited.
The existing `OpenData.main` `finally` block consequently remains the correct
place to log final status and shut down `LoggingManager`; those actions do not
run while the main GUI window is open.

## Deferred

- Real plugin-registry loading and refresh (Batch 3).
- Register/enable/disable/unregister actions and confirmation dialogs (Batch 4).
- JavaFX Settings, Detail, Logs, Help and About dialogs (Batch 5).
- Execute/Dry-run background tasks and live JUL log display (Batch 6).
- Final integration, packaging and screenshot completion (Batch 7).
