# OpenData 2.0.0 Release Record

**Status:** Development and release-candidate baseline  
**Documentation baseline:** 3 August 2026  
**Release date:** Not assigned  
**Licence:** Apache License 2.0

## Candidate scope

Version 2.0.0 delivers database-backed configuration registration,
certificate-protected bootstrap credentials, the common plugin lifecycle and
local-file Octopus statement ingestion.

![Version evolution](../diagrams/generated/version-evolution.svg)

## Release gates

The following are mandatory before declaring the release production-ready:

- clean Maven/quality/test evidence;
- clean SQL Server install/reinstall and least-privilege evidence;
- registration/encrypted restart evidence with deployment-specific keys;
- successful Ofgem/OpenMeteo dry runs and representative write runs;
- representative Octopus write-mode/idempotency/archive tests;
- correction of Octopus dry-run before using Octopus or `all` dry-run acceptance;
- removal/rotation of tracked secrets and private keys;
- validated SQL Server certificate trust;
- preview JDBC dependency decision;
- documentation builds and archive compliance review.

## Historical boundary

Version 1.0.0 records remain under `docs/release/Release-1.0.0.md`. They must not
be used as evidence for 2.0.0.

## Decision record

Assign the release date and tag only after the final checklist and evidence index
are complete. Any waiver must state scope, risk owner, expiry/review date and
compensating controls.
