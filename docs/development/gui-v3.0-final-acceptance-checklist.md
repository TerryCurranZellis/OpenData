# OpenData GUI 3.0 Final Acceptance Checklist

**Document ID:** DEV-GUI-ACCEPT-001  
**Version:** 3.0.0  
**Status:** Release-candidate acceptance  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

Use this checklist against the merged Version 3.0.0 main branch after rebuilding
all generated documentation.

## Automated checks

- [ ] `mvn clean verify` succeeds on JDK 24.
- [ ] The same main branch is smoke-tested on the current JDK 26 development environment.
- [ ] GUI-specific PowerShell tests supplied by the repository complete successfully.
- [ ] All project documentation builds successfully.
- [ ] `OpenData-Technical-User-Guide.chm` is generated successfully on Windows.
- [ ] Any configured Windows application-image packaging step succeeds.

## Startup and lifecycle

- [ ] Starting `OpenData` with no arguments displays the JavaFX splash for the minimum period and then the maximised main window.
- [ ] `--gui` and `-g` start the same graphical interface.
- [ ] Closing the main window terminates JavaFX cleanly and final status/duration are logged.
- [ ] No obsolete Swing UI package is required at runtime.

## Plugin table

- [ ] Registered plugins load from SQL Server.
- [ ] Selection count is correct for zero, one and multiple checked rows.
- [ ] Enabled state is correct.
- [ ] Latest run status/date displays correctly after a normal execution.
- [ ] A never-run plugin displays blank latest-run fields as expected.

## Registration and state changes

- [ ] Register scans the supported plugin properties folders and offers only valid unregistered definitions.
- [ ] Duplicate `plugin.id` definitions are rejected.
- [ ] Register from File opens the JavaFX file chooser and registers a valid definition.
- [ ] Unregister requires confirmation and refreshes the table.
- [ ] Enable and Disable require confirmation and refresh the table.
- [ ] Empty selections produce an appropriate warning.

## Execute and Dry-run

- [ ] Execute uses exactly the checked plugin ids.
- [ ] Dry-run uses exactly the checked plugin ids.
- [ ] Execution work runs away from the JavaFX application thread.
- [ ] Live log text updates while execution is active.
- [ ] Close/window-close are blocked while work is active and enabled after completion.
- [ ] Plugin failures are reported without freezing or terminating the GUI.
- [ ] Dry-run produces no persistent provider-data writes or generic run-audit writes.
- [ ] The main table refreshes after execution completes.

## Information dialogs

- [ ] Plugin Detail accepts exactly one selected plugin.
- [ ] Plugin Detail displays stored configuration read-only.
- [ ] Settings/Preferences displays effective application values read-only.
- [ ] Passwords, tokens, secrets and credentials remain masked.
- [ ] Logs displays the active application log without shutting logging down.
- [ ] About displays Version 3.0.0 application metadata and the active Java runtime.

## Help

- [ ] With the CHM present on Windows, Help opens the compiled Technical User Guide.
- [ ] With the CHM temporarily unavailable, Help opens the JavaFX fallback viewer.
- [ ] A compiled-Help launch failure does not terminate the GUI.

## CLI regression

- [ ] `--help`, `--about` and `--list-plugins` behave as documented.
- [ ] `--plugin ofgem --execute` authorises normal execution.
- [ ] `--plugin ofgem --dry-run` authorises dry-run without requiring `--execute`.
- [ ] `-n` is equivalent to `--dry-run` and `-d` remains `--disable`.
- [ ] `--plugin <id> --detail` displays exactly one registered plugin configuration.

## Documentation screenshots

Capture and review all required files from
[GUI Screenshot Plan](gui-screenshot-plan.md):

- [ ] `gui-splash-screen.png`
- [ ] `gui-main-window.png`
- [ ] `gui-register-discovered.png`
- [ ] `gui-register-from-file.png`
- [ ] `gui-plugin-administration-confirm.png`
- [ ] `gui-plugin-detail.png`
- [ ] `gui-settings.png`
- [ ] `gui-execute-confirm.png`
- [ ] `gui-execution-log-running.png`
- [ ] `gui-execution-log-complete.png`
- [ ] `gui-log-viewer.png`
- [ ] `gui-help.png`
- [ ] `gui-about-dialog.png`

- [ ] All screenshots are free of secrets, customer data and unnecessary personal paths.
- [ ] Approved PNG files are copied from `docs/diagrams/source` to the publication/generated location.
- [ ] Final manuals include reviewed captions and figure numbering.
