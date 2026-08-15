# OpenData GUI 3.1 Final Acceptance Checklist

**Version:** 3.1.0  
**Date:** 14 August 2026

---

Use this checklist after Batch 7 and after rebuilding all generated
documentation.

## Automated checks

- [ ] Run `mvn clean test` successfully on JDK 24 or later.
- [ ] Run `scripts/Test-Gui-Batch7.ps1` successfully.
- [ ] Build all project documentation successfully.
- [ ] Confirm `OpenData-Technical-User-Guide.chm` is generated.
- [ ] Build `scripts/Build-Windows-Package.ps1 -Type app-image` successfully.
- [ ] Start both packaged launchers.

## Startup and lifecycle

- [ ] Starting `OpenData` with no arguments shows the JavaFX splash for the
  configured minimum period and then the maximised main window.
- [ ] Closing the main window terminates the JavaFX application cleanly.
- [ ] No Swing splash appears during CLI execution.
- [ ] No deprecated `com.towermarsh.opendata.ui` warning is emitted by normal
  build/test output.

## Plugin table

- [ ] Registered plugins load from the database.
- [ ] checkbox selection count is correct for zero, one and multiple rows.
- [ ] latest run status/date displays correctly after execution.

## Registration and state changes

- [ ] Register scans the configured plugin properties folder for new plugins.
- [ ] Register from File opens the file chooser and registers a valid properties
  file.
- [ ] Unregister requires confirmation and refreshes the table.
- [ ] Enable and Disable require confirmation and refresh the table.
- [ ] actions reject an empty selection with an appropriate message.

## Execute and Dry-run

- [ ] Execute Selected uses exactly the checked plugin IDs.
- [ ] Dry-run Selected uses exactly the checked plugin IDs.
- [ ] execution runs away from the JavaFX application thread.
- [ ] the live-log window scrolls while execution is active.
- [ ] its completion/close control is disabled while work is active and enabled
  when execution completes.
- [ ] plugin failures are shown without freezing or terminating the GUI.
- [ ] Dry-run produces no persistent plugin-data writes or run-audit writes as
  defined by the CLI contract.

## Information dialogs

- [ ] Plugin Detail displays one selected plugin only.
- [ ] Settings/Preferences displays effective read-only values.
- [ ] passwords, tokens and other sensitive values remain masked.
- [ ] Logs displays the active application log.
- [ ] About displays version 3.1.0 metadata from
  `com.towermarsh.opendata.app.ApplicationInfo`.

## Help

- [ ] With the CHM present on Windows, Help opens the compiled Technical User
  Guide.
- [ ] With the CHM temporarily renamed, Help falls back to the JavaFX help viewer.
- [ ] A Help launch failure does not terminate the GUI.

## Windows packaged image

- [ ] `OpenData.exe` starts the GUI without an unwanted console window.
- [ ] `OpenData-CLI.exe --help` opens a console and displays CLI help.
- [ ] `OpenData-CLI.exe --plugin ofgem --dry-run` follows the corrected dry-run
  command contract.
- [ ] icon, version and application description are correct.
- [ ] compiled Help is included when built before packaging.

## Documentation screenshots

Capture final screenshots only after the application has passed the functional
checks above. Suggested stable names:

- `gui-main-window.png`
- `gui-register-confirmation.png`
- `gui-register-from-file.png`
- `gui-plugin-detail.png`
- `gui-settings.png`
- `gui-live-execution-log.png`
- `gui-log-viewer.png`
- `gui-about.png`
- `gui-windows-help.png`

Update the user/developer documentation image references only after the final
screenshots have been captured from the release-candidate build.
