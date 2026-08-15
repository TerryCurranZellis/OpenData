# GUI Screenshot Plan

**Document ID:** DEV-GUI-SCREENSHOTS-001  
**Version:** 3.0.0  
**Status:** Capture required before Version 3.0.0 release approval  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Purpose

This plan defines the exact PNG files required for the Version 3.0.0 graphical
interface documentation. Capture source images into `docs/diagrams/source`.
After review, copy the approved publication images into
`docs/diagrams/generated` before generating the final manuals.

Do not resize, annotate or embed figure numbers in the source PNG. Figure numbers
and captions belong to the document-generation pipeline.

## Required screenshots

| Filename | What must be visible | Primary use |
|---|---|---|
| `gui-splash-screen.png` | Complete JavaFX splash centred on desktop | GUI User Guide startup |
| `gui-main-window.png` | Maximised main window, menus, toolbar, real plugin rows, status `Ready`, selection count | GUI overview and architecture |
| `gui-register-discovered.png` | Register confirmation listing one or more newly discovered plugin definitions | Registration workflow |
| `gui-register-from-file.png` | JavaFX FileChooser with a representative plugin `.properties` file selected | External-file registration |
| `gui-plugin-administration-confirm.png` | Enable, Disable or Unregister OK/Cancel confirmation with multiple selected plugin ids | Administration workflow |
| `gui-plugin-detail.png` | Read-only Property/Value table; sensitive value masked where naturally present | Plugin information |
| `gui-settings.png` | Read-only application settings with database/execution/logging rows and no exposed password | Settings/Preferences |
| `gui-execute-confirm.png` | Execute OK/Cancel confirmation listing selected plugin ids | Execute workflow |
| `gui-execution-log-running.png` | Live execution log while active, contextual lines visible, Close disabled | Live execution behaviour |
| `gui-execution-log-complete.png` | Completed execution outcome, Close enabled | Completion behaviour |
| `gui-log-viewer.png` | Current log path and scrollable formatted JUL output | Existing-log viewer |
| `gui-help.png` | Compiled Windows Help opened from OpenData, or documented JavaFX fallback if CHM is intentionally unavailable | Help behaviour |
| `gui-about-dialog.png` | OpenData artwork, version 3.0.0, Java runtime, licence/copyright | About dialog |

These thirteen images are sufficient for the Version 3.0.0 manuals. A separate
screenshot is not required for every Enable/Disable/Unregister variant or for the
standard `No plugin selected` warning because those controls use the same dialog
patterns.

## Capture environment

Use the release-candidate main branch with:

- OpenData version 3.0.0;
- JDK 26 for the current development capture environment;
- Apache NetBeans 31 only when an IDE is needed to launch the application; and
- the normal JavaFX 26.0.1 dependency set from `pom.xml`.

The screenshots document the application, not the IDE, so NetBeans should not be
visible in the cropped application captures unless desktop context is needed for
the splash.

## Main-window preparation

For `gui-main-window.png`:

- wait until the lower-left status reads `Ready`;
- show the complete menu bar, toolbar, table and status bar;
- use real registered Ofgem, OpenMeteo and Octopus rows where available;
- if practical, include at least one completed plugin run so Last Run Status and
  Date of Last Run are populated;
- select one or two rows so the Selected checkboxes and selection count are easy
  to understand; and
- do not create artificial production data solely to make the screenshot fuller.

## Registration captures

For `gui-register-discovered.png`, ensure the confirmation includes plugin id,
name and source filename. Do not expose private absolute paths unless they are
safe development paths intended for publication.

For `gui-register-from-file.png`, show a representative `.properties` filename.
Avoid folders containing usernames, credentials or unrelated private files.

## Configuration captures

For `gui-plugin-detail.png` and `gui-settings.png`:

- verify password, token, secret and credential values are masked;
- use enough rows to show vertical scrolling where useful;
- do not substitute fake masking if the application itself is not masking the
  value; treat an exposed secret as a release defect instead; and
- prefer a plugin/configuration set that illustrates the real Version 3.0.0
  layout without revealing sensitive deployment details.

## Execution captures

For `gui-execute-confirm.png`, select at least one enabled plugin. Two plugins are
preferred because they show the multi-selection behaviour clearly.

For `gui-execution-log-running.png`:

- capture while processing is still active;
- ensure the centred **Close** button is visibly disabled;
- show enough formatted log lines to demonstrate live output;
- prefer a multi-plugin run if it naturally shows plugin/run context; and
- check the visible lines for customer statement content, tokens, passwords,
  account identifiers or machine-private paths before publication.

For `gui-execution-log-complete.png`, capture the same window after completion so
the final outcome and enabled **Close** button are obvious.

## Help capture

For the Windows release path, build the Technical User Guide CHM first and place
`OpenData-Technical-User-Guide.chm` in a supported help location. Launch Help
from the OpenData menu or toolbar and capture the resulting Windows HTML Help
window as `gui-help.png`.

If the final distribution deliberately does not include the CHM, capture the
built-in JavaFX fallback instead and record that packaging decision in the
release evidence index.

## Image quality and privacy

- Use PNG, not JPEG.
- Capture at normal Windows display scaling where practical so text remains
  readable in the generated manuals.
- Do not include passwords, API tokens, PFX passwords, account numbers, customer
  statement content or unnecessary personal filenames.
- Keep Windows title bars and dialog buttons when they explain behaviour.
- Avoid excessive desktop background around normal application windows.
- Review every image at 100% before copying it into `docs/diagrams/generated`.

## Caption guidance

Recommended captions are:

1. OpenData Version 3.0.0 startup splash.
2. OpenData graphical interface main window.
3. Registering discovered plugin definitions.
4. Registering a plugin definition from file.
5. Confirming a plugin administration action.
6. Viewing registered plugin configuration.
7. Viewing effective application settings.
8. Confirming execution of selected plugins.
9. Live plugin execution logging in progress.
10. Completed plugin execution log.
11. Viewing the current OpenData application log.
12. OpenData Help launched from the graphical interface.
13. OpenData About dialog.

Final figure numbering is assigned by document order, not by the PNG filename.
