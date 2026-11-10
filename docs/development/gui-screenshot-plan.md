# OpenData 4.0.0 GUI Screenshot Capture Plan

**Document ID:** DEV-GUI-SCREENSHOTS-001  
**Application baseline:** 4.0.0  
**Documentation maintenance target:** 4.1.0  
**Status:** Screenshots pending operator capture  
**Minimum Java version:** 24

## Purpose and storage

This supersedes the Version 3.0.0 screenshot checklist. Screenshots must depict **actual 4.0.0 application behaviour**, not mock-ups. Capture unedited PNG originals in `docs/diagrams/source/`, then place reviewed publication copies in `docs/diagrams/generated/` using the same filenames. The document-generation pipeline supplies captions and figure numbers.

A first-run SQL Server test environment is required to capture the initialisation wizard. Use a disposable database, isolated test credentials and a non-production AES key; never display usable passwords, tokens or sensitive customer data. Record the application build or commit alongside the screenshots.

## A. Startup and first-run initialisation (8 images)

| Filename | Required content | Suggested documentation placement |
|---|---|---|
| `gui-splash-screen.png` | Complete OpenData 4.0.0 JavaFX splash screen | User Guide: startup |
| `gui-init-database-server.png` | Wizard **Step 1 of 3 — Database server**: hostname, TCP port, database name, administrator login, masked administrator password, and the self-signed certificate option | First-run guide: SQL Server connection |
| `gui-init-application-account.png` | Wizard **Step 2 of 3 — Application account and encryption**: application login, masked password and confirmation, AES key path, Browse control | First-run guide: application credentials |
| `gui-init-aes-key-file-chooser.png` | JavaFX chooser for an existing or new AES key file, displaying a safe demonstration directory | First-run guide: encryption key |
| `gui-init-review.png` | Wizard **Step 3 of 3 — Confirm installation**: non-secret server/database/account/key summary and Initialise button | First-run guide: confirmation |
| `gui-init-progress.png` | Configuring OpenData page, scrollable progress output while schema and plugin registration run; no exposed credentials | First-run guide: progress |
| `gui-init-success.png` | Completed progress page with **Finish — Open OpenData** action; plugin registrations remain disabled | First-run guide: completion |
| `gui-init-failure.png` | Safe, representative failed setup showing the error and recovery context (for example, a disposable installation with a deliberately unavailable SQL connection); no production data | First-run guide: troubleshooting |

The first-run wizard has **three input/review steps and an additional progress state** (`InitialisationWizard`); do not describe it as a four-step form. The failure capture may require a separate test run and must not disturb a working installation.

## B. Main interface, registration and administration (7 images)

| Filename | Required content | Suggested documentation placement |
|---|---|---|
| `gui-main-window.png` | Full main window, menu, toolbar, populated plugin table, status bar and selected rows | GUI user guide: overview |
| `gui-register-discovered.png` | Register-discovered-plugins confirmation containing plugin IDs and definitions | GUI user guide: discovery |
| `gui-register-from-file.png` | FileChooser selecting a representative `.properties` plugin definition | GUI user guide: registration |
| `gui-plugin-administration-confirm.png` | Enable/Disable/Unregister confirmation for selected plugin rows | GUI user guide: administration |
| `gui-plugin-detail.png` | Read-only plugin details, with any sensitive values masked by the actual application | GUI user guide: plugin details |
| `gui-settings.png` | Application configuration display without decrypted secrets | Administrator guide: settings |
| `gui-about-dialog.png` | About dialogue with 4.0.0 version, runtime and project information | GUI user guide: About |

## C. Execution, logs and help (6 images)

| Filename | Required content | Suggested documentation placement |
|---|---|---|
| `gui-execute-confirm.png` | Confirmation for running selected enabled plugins | GUI user guide: execution |
| `gui-execution-log-running.png` | Live log scrolling during execution, with Close disabled | GUI user guide: live execution |
| `gui-execution-log-complete.png` | Final execution result and enabled Close control | GUI user guide: completion |
| `gui-log-viewer.png` | Existing formatted application logfile in the scrollable viewer | GUI user guide: logs |
| `gui-help.png` | Installed CHM help opened from GUI, or the implemented fallback when CHM is not packaged | GUI user guide: Help |
| `gui-dry-run-confirm.png` | Dry-run confirmation/dialogue, if visibly different from ordinary execution; otherwise document reuse of `gui-execute-confirm.png` and omit this optional image | GUI user guide: dry run |

**Total: 20 mandatory PNGs plus one conditional dry-run PNG.** The 20 mandatory captures comprise eight startup/initialisation, seven main/administration, and five execution/log/help captures.

## Capture checklist

1. Use the same approved 4.0.0 build for all images; note its Git commit in the release evidence.
2. Capture at native Windows scale where possible; keep captions outside the image.
3. Remove personal paths, account numbers, passwords, SQL administrator credentials, AES key material and statement content. Do not retouch a genuinely unmasked secret: treat it as a defect.
4. Use real application states and a disposable SQL Server database for initialisation. Do not claim completion when setup fails.
5. Verify plugin status after setup, including that newly registered plugins are disabled.
6. Review image legibility at 100% and ensure each documented filename exists before generating DOCX/PDF/CHM manuals.
7. Keep historical screenshots only in historical release evidence; do not silently reuse a 3.x image for current 4.0.0 behaviour.

## Screenshots and diagrams are different assets

PlantUML diagrams are maintainable architecture illustrations; GUI screenshots must be captured from the running JavaFX application. Update `docs/diagrams/source/*.puml` whenever the corresponding architecture changes, and regenerate the SVGs using the existing documentation build.
