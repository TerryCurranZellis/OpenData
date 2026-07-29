# Batch 4 Implementation Notes

## Purpose

Standardise OpenData licensing and source-file ownership declarations for an open-source release.

## Changes completed

- Added the canonical root `LICENSE` file while retaining `LICENSE.md` for compatibility.
- Confirmed Apache License, Version 2.0 as the project licence.
- Standardised headers in 230 Java files, 5 PowerShell files and 10 SQL files.
- Used `Copyright © 2026 Terry Curran` and `SPDX-License-Identifier: Apache-2.0`.
- Added `docs/Licensing-Policy.md`.
- Updated project metadata and documentation references.
- Preserved `com.towermarsh.opendata` because changing a Java namespace would be a breaking technical migration, not a licensing correction.

## Diagram assessment

No PlantUML diagram was required. Licensing ownership and attribution are policy concerns and do not introduce a new system architecture or execution flow.

## Validation

- All targeted Java, PowerShell and SQL files were checked for the canonical SPDX identifier.
- The project manifest JSON was parsed after modification.
- The release archive structure was checked after creation.
