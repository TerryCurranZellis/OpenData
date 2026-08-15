# ADR-0055: Use JavaFX read-only information dialogs and mask sensitive configuration values

**Status:** Accepted  
**Date:** 2026-08-14  
**Decision owners:** OpenData maintainers  
**Version:** 3.0.0

---

## Context

Batch 5 of the Version 3.1 graphical interface must implement Plugin Detail,
Settings/Preferences, the existing-log viewer, Help and About. Plugin details
and application settings can contain values loaded from SQL Server and may
include credentials or other sensitive configuration. Log files can also be
large enough that reading them on the JavaFX application thread would cause a
visible pause.

The previous About window is Swing based. New graphical-interface code is
JavaFX, and the active GUI and standalone `--about` routes should no longer need
the Swing About implementation.

The Version 3 specification describes Settings but does not define an editable
settings model or Save semantics.

## Decision

1. Implement custom read-only JavaFX dialogs for configuration tables, text/log
   viewing and About information.
2. Load plugin details and effective application settings behind focused gateway
   classes on JavaFX `Task`s. Controllers do not perform JDBC work.
3. Display Plugin Detail for exactly one checked plugin at a time.
4. Read plugin properties from the same persistent
   `JdbcConfigurationPropertiesSource` used by the CLI detail command.
5. Mask values whose plugin metadata declares `.sensitive=true`, together with
   conventional password/secret/token/credential property names.
6. Display Settings/Preferences read-only until an editable settings contract is
   specified. The GUI must never expose the decrypted database password.
7. Flush the active JUL handlers before reading the current application log and
   show the log in a non-editable scrollable text area.
8. Provide built-in JavaFX help text as the fallback until Windows compiled Help
   integration is completed.
9. Replace the active Swing About usage with JavaFX for both the main GUI and the
   standalone `--about` command. Retain the old Swing classes only as deprecated
   source-compatibility remnants.

## Consequences

### Positive

- The JavaFX GUI no longer depends on Swing for About.
- Plugin and application configuration can be inspected without exposing known
  secret-bearing values.
- Database and log-file reads do not block the JavaFX application thread.
- The same stored plugin configuration is presented by CLI and GUI detail
  operations.
- The information dialogs establish reusable controls for later execution/log
  work.

### Trade-offs

- Settings cannot yet be edited from the GUI because no editing/save contract is
  defined in the specification.
- Sensitive-name masking is intentionally defensive and may hide a value that is
  technically non-secret if it is named like a credential.
- Windows `.chm` Help integration remains a later packaging task; Batch 5 uses a
  JavaFX fallback help page.

## Alternatives considered

### Make Settings editable immediately

Rejected because field ownership, validation, persistence, password handling and
Save semantics are not defined by the current GUI specification.

### Display every stored property verbatim

Rejected because a diagnostic/detail view should not disclose credentials merely
because they exist in the underlying configuration source.

### Continue using Swing About

Rejected for active GUI use because the Version 3 desktop interface is JavaFX and
the migration should not require two UI toolkits for normal information dialogs.

---
