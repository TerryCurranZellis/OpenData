# GUI Screenshot Plan

**Document ID:** DEV-GUI-SCREENSHOTS-001  
**Version:** 3.1.0  
**Status:** Planned  
**Baseline date:** 13 August 2026  
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
| `gui-plugin-detail.png` | Batch 5 | `user-guide/12-graphical-interface.md` | Two-column plugin property/value dialog |
| `gui-log-viewer.png` | Batch 5 | `user-guide/12-graphical-interface.md` | Scrollable existing-log viewer |
| `gui-about-dialog.png` | Batch 5 | `user-guide/12-graphical-interface.md` | JavaFX About dialog with OpenData image and version information |
| `gui-execution-log.png` | Batch 6 | `user-guide/12-graphical-interface.md`; `development/javafx-gui-architecture.md` | Live Execute/Dry-run log with Close disabled until completion |
| `gui-help-window.png` | Batch 7 | `user-guide/12-graphical-interface.md` | Final Windows compiled-help integration or fallback JavaFX help display |

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

## Caption sequence

The final generated manuals should add Word-compatible figure captions through
the documentation pipeline. Figure numbering is assigned by document order, not
embedded in the PNG itself.
