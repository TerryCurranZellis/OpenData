# GUI Screenshot Plan

**Document ID:** DEV-GUI-SCREENSHOTS-001  
**Version:** 3.0.0  
**Status:** Planned  
**Baseline date:** 12 August 2026  
**Minimum Java version:** 17

---

## Asset workflow

Capture each screenshot as PNG using the filename below. Store the maintained
capture in `docs/diagrams/source` and copy the publication copy to
`docs/diagrams/generated` before building the manuals.

The table names the proposed screenshots now so documentation can refer to
stable filenames as each implementation batch is completed.

| Screenshot filename | Capture after | Primary documentation location | Purpose |
|---|---:|---|---|
| `gui-main-window.png` | Batch 1 | `user-guide/12-graphical-interface.md`; `development/javafx-gui-architecture.md` | Full main window showing menus, toolbar, checkbox plugin table and status bar |
| `gui-splash-screen.png` | Batch 2 | `user-guide/12-graphical-interface.md` | JavaFX startup splash screen |
| `gui-register-from-file.png` | Batch 4 | `user-guide/12-graphical-interface.md` | File chooser used to select a plugin configuration file |
| `gui-confirm-action.png` | Batch 4 | `user-guide/12-graphical-interface.md` | Standard OK/Cancel confirmation flow for a selected action |
| `gui-plugin-detail.png` | Batch 5 | `user-guide/12-graphical-interface.md` | Two-column plugin property/value dialog |
| `gui-log-viewer.png` | Batch 5 | `user-guide/12-graphical-interface.md` | Scrollable existing-log viewer |
| `gui-about-dialog.png` | Batch 5 | `user-guide/12-graphical-interface.md` | JavaFX About dialog with OpenData image and version information |
| `gui-execution-log.png` | Batch 6 | `user-guide/12-graphical-interface.md`; `development/javafx-gui-architecture.md` | Live Execute/Dry-run log with Close disabled until completion |
| `gui-help-window.png` | Batch 7 | `user-guide/12-graphical-interface.md` | Final Windows compiled-help integration or fallback JavaFX help display |

## Caption sequence

The final generated manuals should add Word-compatible figure captions through
the documentation pipeline. Figure numbering is assigned by document order, not
embedded in the PNG itself.
