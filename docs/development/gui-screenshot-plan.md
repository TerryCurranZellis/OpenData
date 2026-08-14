# GUI Screenshot Plan

**Document ID:** DEV-GUI-SCREENSHOTS-001
**Version:** 3.1.0
**Status:** Planned
**Baseline date:** 14 August 2026
**Minimum Java version:** 24

---

## Asset workflow

Capture each screenshot as PNG using the filename below. Store the maintained
capture in `docs/diagrams/source` and copy the publication copy to
`docs/diagrams/generated` before building the manuals.

The table names the proposed screenshots now so documentation can refer to
stable filenames as each implementation batch is completed.

| Screenshot filename | Capture after | Primary documentation location | Purpose |
|---|---:|---|---|
| `gui-main-window.png` | Batch 3 | `user-guide/12-graphical-interface.md`; `development/javafx-gui-architecture.md` | Full main window showing menus, toolbar, checkbox plugin table and status bar |
| `gui-splash-screen.png` | Batch 2 | `user-guide/12-graphical-interface.md` | JavaFX startup splash screen shown before the main window |
| `gui-register-confirm.png` | Batch 4 | `user-guide/12-graphical-interface.md` | Confirmation showing new plugin definitions discovered in the configuration folder |
| `gui-register-from-file.png` | Batch 4 | `user-guide/12-graphical-interface.md` | File chooser used to select a plugin configuration file |
| `gui-plugin-administration-confirm.png` | Batch 4 | `user-guide/12-graphical-interface.md` | Enable, Disable or Unregister confirmation for checked plugin rows |
| `gui-confirm-action.png` | Batch 4 | `user-guide/12-graphical-interface.md` | Standard OK/Cancel confirmation flow for a selected action |
| `gui-plugin-detail.png` | Batch 5 | `user-guide/12-graphical-interface.md` | Two-column plugin property/value dialog with a real registered plugin |
| `gui-settings.png` | Batch 5 | `user-guide/12-graphical-interface.md` | Read-only effective Settings/Preferences dialog with password masked |
| `gui-log-viewer.png` | Batch 5 | `user-guide/12-graphical-interface.md` | Scrollable existing-log viewer |
| `gui-about-dialog.png` | Batch 5 | `user-guide/12-graphical-interface.md` | JavaFX About dialog with OpenData image and version information |
| `gui-execution-log.png` | Batch 6 | `user-guide/12-graphical-interface.md`; `development/javafx-gui-architecture.md` | Live Execute/Dry-run log with Close disabled until completion |
| `gui-help-window.png` | Batch 5 | `user-guide/12-graphical-interface.md` | Built-in JavaFX fallback help display; recapture in Batch 7 only if compiled Help replaces it |

## Batch 3 main-window capture guidance

Capture `gui-main-window.png` after the persistent registry has loaded and the
lower-left status reads `Ready`. Use real registered plugin rows so the image
shows enabled state and, where available, last-run status/date. Include at least
one never-run plugin if the current database naturally contains one; do not
create artificial production data solely for the screenshot.

## Batch 2 capture guidance

Capture `gui-splash-screen.png` while the splash is centred on the desktop and
before the main window appears. Do not crop the OpenData image itself; leave
enough surrounding desktop context to make clear that it is the application
startup window.


## Batch 5 capture guidance

For `gui-plugin-detail.png`, select exactly one registered plugin and capture a
representative set of stored properties. If the chosen plugin contains a value
marked sensitive, confirm that the screenshot shows only masked text.

For `gui-settings.png`, include the configuration source and several database,
execution and logging rows while ensuring the Database password row remains
masked. `gui-log-viewer.png` should show the source log filename and enough
formatted log lines to demonstrate both vertical and horizontal scrolling.

Capture `gui-about-dialog.png` with the splash image and version information
visible. Capture `gui-help-window.png` from the built-in JavaFX fallback help; if
Batch 7 launches compiled Windows Help instead, replace that screenshot then.


## Batch 6 capture guidance

For `gui-execution-log.png`, select at least one enabled plugin and capture the
window while processing is still active so live formatted log messages are
visible and the centred **Close** button is visibly disabled. If practical, use
a multi-plugin run so contextual plugin/run fields demonstrate that concurrent
output shares the same window. Do not capture sensitive configuration or source
data in the log area.

## Caption sequence

The final generated manuals should add Word-compatible figure captions through
the documentation pipeline. Figure numbering is assigned by document order, not
embedded in the PNG itself.
