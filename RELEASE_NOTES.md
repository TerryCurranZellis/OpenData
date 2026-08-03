# OpenData 2.0.0 Release Notes

**Status:** Development and release-candidate baseline  
**Release date:** Not assigned  
**Documentation reviewed:** 3 August 2026

## Overview

OpenData 2.0.0 moves runtime and plugin configuration into SQL Server, protects
the bootstrap database password using an X.509/PKCS#12 key pair, standardises the
plugin lifecycle and completes local-file Octopus statement ingestion.

This document describes the candidate capability. It is not a declaration that
all release checks have passed.

## Highlights

- `--register` imports classpath configuration into SQL Server and rewrites the
  bootstrap configuration for database-backed startup.
- Ofgem and OpenMeteo use the common lifecycle and support side-effect-free dry
  runs.
- Octopus scans authorised local PDFs, ignores completed filename/hash pairs,
  transforms all new statements as a batch, loads electricity/gas data and the
  completion ledger transactionally, then archives successful files.
- The documentation system builds four manifest-defined manuals.
- Governance documentation now separates project licensing, dependency notices,
  provider attribution and private customer-document handling.

## Upgrade impact

Version 1.x operators must install the current SQL schema, create deployment keys,
run registration and migrate custom plugins to the current lifecycle/packages.
Read `docs/migration/version-1-to-version-2.md` before replacing a working
installation.

## Security and operational warnings

Do not deploy the tracked development credentials/private key. Rotate any
password exposed in repository history. The environment-variable PFX password
route, Octopus dry-run, SQL Server trust configuration and preview JDBC dependency
must be resolved or explicitly waived before a production-ready release.

## Not included

- Direct Octopus website, email or API statement download.
- Internal scheduling.
- Graphical configuration administration.
- Enterprise secret-manager integration.
- A verified self-contained executable JAR.

## Acceptance

The release owner must complete `docs/release/Final-Release-Checklist.md`, retain
evidence using `docs/release/Release-Evidence-Index.md`, and update the current
readiness assessment before assigning a release date or creating tag `v2.0.0`.
