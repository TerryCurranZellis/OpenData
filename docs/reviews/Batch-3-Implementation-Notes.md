# Batch 3 Implementation Notes

> **Historical implementation note:** The later command-line and persistent plugin-registry update supersedes statements here about standalone registration, invocation override files, or Octopus/`all` dry-run limitations. Current CLI and release documents take precedence.

**Completed:** 3 August 2026
**Scope:** Plugin-specific architecture, operator and reference documentation

## Updated

- Plugin index and status table for Ofgem, OpenMeteo and Octopus.
- Ofgem workflow, data model, configuration and import architecture.
- OpenMeteo requested variables, date-resolution behavior, persistence and
  configuration caveats.
- Octopus filename/hash ledger, PDF parsing, transaction, archive and required
  directory configuration.
- Plugin registry reference and entry-class correction.
- Octopus ADR status and transactional-persistence decision.
- Operator/developer commands that incorrectly recommended Octopus or all-plugin
  dry runs.

## Added

- OpenMeteo architecture chapter.
- Octopus architecture chapter.
- Octopus plugin and schema references.
- Octopus processing and data-model diagrams.

## Important implementation variances recorded

- Octopus dry run fails because extract reads the processed-file ledger from an
  unavailable dry-run database resource.
- OpenMeteo currently ignores `default-start-days-ago` and
  `include-current-date` when resolving dates.
- Octopus `working.directory` is currently unused and blank archive values
  resolve to the process working directory.
- Several packaged Ofgem properties and duplicate provider classes are not used
  by the active initialise pipeline.
- The current keystore-password environment-variable constant is `nopassword`,
  so `OPENDATA_CONFIG_KEYSTORE_PASSWORD` is not honoured despite earlier
  documentation claiming support.
- The uploaded source contains a tracked plaintext bootstrap password and private
  PFX; both are documented as release blockers requiring removal and rotation.

## Exclusions

No Java, SQL, PowerShell or build-script files were changed.
