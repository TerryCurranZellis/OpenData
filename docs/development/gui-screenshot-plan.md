# OpenData 4.0.0 GUI screenshot capture plan

**Document ID:** DEV-GUI-SCREENSHOTS-001  
**Application baseline:** 4.0.0  
**Documentation maintenance:** 4.1.0  
**Status:** Screenshots outstanding; use the running Windows application as the authority.

## Scope and storage

Capture full-size, unannotated PNG screenshots from the actual OpenData 4.0.0 JavaFX application on Windows with JDK 24+ (prefer the same JDK and JavaFX runtime used for release testing). Place original screenshots in `docs/diagrams/source/`; after privacy review, copy the publication-approved images to `docs/diagrams/generated/`. The document-generation pipeline supplies captions and figure numbers. Do not fabricate screenshots or substitute PlantUML diagrams for the real interface.

The first-run wizard appears when `config/application.properties` contains `init=true`. Use a *disposable test SQL Server/database* for all wizard captures. Never initialise an existing live OpenData database solely to produce screenshots.

## First-run initialisation wizard — required screenshots

| Filename | Required visible content | Purpose |
|---|---|---|
| `gui-initialisation-sql-server.png` | Initial configuration window; SQL Server host, port, database, temporary administrator login/password and certificate-trust option; password masked | Step 1: server connection |
| `gui-initialisation-application-credentials.png` | Application SQL login, masked password and confirmation, AES key-file path and Browse button | Step 2: runtime identity and encryption |
| `gui-initialisation-aes-key-browse.png` | JavaFX file selector for an existing or new AES key-file path (show a safe example path) | Key selection |
| `gui-initialisation-review.png` | Review page with host, port, database, application login and AES path; credentials excluded; **Initialise** button | Step 3: confirm settings |
| `gui-initialisation-progress.png` | Progress page showing plugin discovery/validation, SQL schema operations and log area while the background task is active | Step 4: installation in progress |
| `gui-initialisation-complete.png` | Completed progress log and **Finish — Open OpenData** button | Successful installation |
| `gui-initialisation-failure.png` | Representative error dialog and failed progress state from a safely induced, recoverable problem; `init=true` remains | Failure and recovery |

## Normal JavaFX interface — required screenshots

| Filename | Required visible content | Purpose |
|---|---|---|
| `gui-splash-screen.png` | Complete OpenData splash image at application startup | Normal startup |
| `gui-main-window.png` | Full menu, toolbar, registered plugin table, status, selection count and the columns: Selected, Plugin ID, Plugin Description, Enabled, Last Run Status, Date of Last Run | Main window overview |
| `gui-register-discovered.png` | Confirmation for one or more newly discovered plugin configuration files | Automatic registration |
| `gui-register-from-file.png` | FileChooser selecting a sample `.properties` plugin definition | Manual registration |
| `gui-plugin-administration-confirm.png` | Multi-selection confirmation for Enable, Disable or Unregister | Administrator actions |
| `gui-plugin-detail.png` | Read-only Property/Value table with automatically masked secrets | Plugin details |
| `gui-settings.png` | Read-only effective application configuration without exposed secrets | Application settings |
| `gui-execute-confirm.png` | Execute confirmation with selected enabled plugin IDs | Execution |
| `gui-dry-run-confirm.png` | Dry-run confirmation clearly stating that changes are not persisted | Dry run |
| `gui-execution-log-running.png` | Live execution log while processing is active; Close disabled | Progress monitoring |
| `gui-execution-log-complete.png` | Completion status and enabled Close button | Completion |
| `gui-log-viewer.png` | Read-only current application log, path and scrollable output | Log viewer |
| `gui-help.png` | Actual compiled CHM help, or real JavaFX fallback if CHM not distributed | Help |
| `gui-about-dialog.png` | About window, actual application version 4.0.0 and runtime/licensing information | About |

**Required: 21 PNG screenshots** (7 wizard and 14 normal-interface).

## Optional / conditional captures

| Filename | When useful |
|---|---|
| `gui-empty-plugin-list.png` | A fresh installation after setup, before enabling plugins; show the empty or disabled state as actually rendered |
| `gui-no-selection-warning.png` | Troubleshooting a disabled/invalid action; not needed in the main manual |
| `gui-initialisation-retry.png` | Documenting an actual retry after an interrupted or partially completed setup |
| `gui-help-fallback.png` | Both CHM and JavaFX fallback are shipped and each requires explanation |

## Capture order

Capture wizard screens before finalising a disposable installation. Use harmless placeholder credentials and redact machine/private details **outside** the running application only for publication copies. Complete the setup, capture the splash and main GUI, register a few test plugins, then capture information, administration and execution dialogues. Preserve the full log-window title, status and buttons in screenshots.

## Safety and accuracy checks

- Do not expose passwords, AES key material, tokens, SQL administrator credentials, personal paths, energy account identifiers or statement content.
- Do not publish a screenshot displaying unmasked credentials; report this as an application defect instead.
- Do not claim the GUI is tested from screenshots alone: capture after a real, successful JDK 24+ GUI and SQL Server acceptance run.
- Keep Windows dialog titles and actionable buttons visible; use PNG at legible native resolution.
- Avoid displaying NetBeans or its console unless explaining the development workflow.
- Verify screenshot filenames against the published Markdown manifests and their image references.
- Use the first-run guide (`docs/guides/first-run-initialisation.md`) and GUI user guide (`docs/user-guide/12-graphical-interface.md`) as the destinations for the relevant illustrations.

## Diagram companions

`docs/diagrams/source/gui-application-flow.puml` shows normal startup versus `init=true`. `docs/diagrams/source/gui-initialisation-sequence.puml` shows the wizard's SQL/schema/bootstrap sequence. Generate their SVGs using the project's PlantUML documentation pipeline; they complement rather than replace screenshots.
