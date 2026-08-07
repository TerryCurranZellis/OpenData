# OpenData 2.0.0 Release Notes

**Status:** Development and release-candidate baseline  
**Release date:** Not assigned  
**Documentation reviewed:** 7 August 2026

## Overview

OpenData 2.0.0 moves runtime and plugin configuration into SQL Server, adds a
persistent plugin lifecycle registry, protects the bootstrap database password
using an X.509/PKCS#12 key pair, standardises the plugin pipeline, completes
local-file Octopus statement ingestion, and introduces an explicit execution
authorisation switch.

This document describes the candidate capability. It is not a declaration that
all release checks have passed.

## Highlights

- `--plugin <id|all> --register` imports selected packaged definitions into SQL
  Server and rewrites the bootstrap configuration for database-backed startup.
- One external definition can be registered with
  `--plugin <id> --register --file <filename>`.
- Registered plugins can be listed, enabled, disabled and unregistered without
  editing packaged resources.
- Repeated `--plugin` options select several plugins for registration,
  administration or execution.
- Normal and dry-run execution require the explicit `--Execute` switch or its
  short form `-x`; plugin selection by itself no longer starts a load.
- Ofgem, OpenMeteo and Octopus support side-effect-free dry runs.
- Octopus scans authorised local PDFs, ignores completed filename/hash pairs in
  write mode, transforms all new statements as a batch, loads electricity/gas
  data and the completion ledger transactionally, then archives successful files.
- The documentation system builds four manifest-defined manuals and includes an
  additional Unix-style `opendata(1)` manual-page source.
- Governance documentation separates project licensing, dependency notices,
  provider attribution and private customer-document handling.

## Command-line compatibility note

Version 2.0.0 requires explicit execution authorisation:

```text
opendata --plugin ofgem --Execute
opendata --plugin all --Execute --dry-run
```

`--Execute` / `-x` is required for both normal and dry-run execution and cannot
be combined with `--register`, `--unregister`/`--remove`, `--enable`, or
`--disable`.

The requested short form `-d` was assigned to both disable and dry-run. A command
line cannot distinguish those meanings, so OpenData 2.0.0 uses:

- `-d` for `--disable`;
- `-n` for `--dry-run`.

The long option `--dry-run` is unchanged.

## Upgrade impact

Version 1.x operators must install the current SQL schema, including
`sql/003a-create-plugin-registry.sql`, create deployment keys, register plugins
and migrate custom plugins to the current lifecycle/packages. Read
`docs/migration/version-1-to-version-2.md` before replacing a working
installation.

After migration, verify:

```text
opendata --plugin all --register
opendata --list-plugins
opendata --plugin all --Execute --dry-run
```

## Security and operational warnings

Do not deploy the tracked development credentials/private key. Rotate any
password exposed in repository history. The environment-variable PFX password
route, SQL Server trust configuration and preview JDBC dependency must be
resolved or explicitly waived before a production-ready release.

## Release decision

The source and documentation remain a release candidate until the final release
checklist and evidence index are completed in the target environment.
