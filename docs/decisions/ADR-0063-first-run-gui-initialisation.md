# ADR-0063: Initialise OpenData from a GUI when configured in the bootstrap file

**Status:** Accepted for implementation in version 4.0.0  
**Date:** 7 November 2026

## Context

A new installation cannot use the database-backed registry before the core SQL
Server schema exists. The normal GUI therefore cannot be used until several
manual steps have already been completed. Plugin registration also creates
provider-specific tables.

## Decision

Add a **file-based** `init` Boolean, not a CLI switch. If true, invoke a JavaFX
wizard before normal execution. The wizard collects temporary SQL administrator
and permanent SQL application credentials, configures the AES key file, provisions
the core database, discovers and validates all plugins, installs their tables,
registers them **disabled**, and finally rewrites the bootstrap with `init=false`.
Preserve existing SQL schemas, logins and AES key files for retry safety.

## Consequences

- The first-run path requires a desktop. No automated headless CLI bootstrap.
- SQL administrator rights are required during setup, but its password is not
  persisted to disk.
- DDL can partially succeed; the init flag is cleared only after completion.
- The application login retains DDL rights for legacy interactive plugin
  registration. This requires tightening for low-privilege production use.
- The deployment property file is under `<working directory>/config` rather
  than an IDE source folder. Existing installations require migration.
- API and core have explicit JPMS descriptors, and GUI package injection is
  opened to JavaFX FXML.
