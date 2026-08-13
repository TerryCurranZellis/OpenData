# Release and Versioning

**Document ID:** DEV-RELEASE-001
**Version:** 2.0
**Status:** Current release process
**Baseline date:** 2 August 2026

---

## Version source

The Maven project version is the build version. Version 2.0.0 is the current code
and documentation baseline. Release notes and the Git tag must use the same
version.

## Release gate

A Version 2.0.0 release candidate requires:

- clean `mvn clean verify` output on Java 24 compatibility;
- documentation validation and successful diagram rendering;
- clean/repeat SQL Server installation;
- successful `--plugin all --register` from a plain bootstrap password;
- successful restart using the encrypted bootstrap password;
- least-privilege verification for the application principal;
- successful Ofgem, OpenMeteo, Octopus and `all` dry runs;
- register/list/enable/disable/unregister lifecycle acceptance;
- representative write-mode, rollback and idempotency tests;
- duplicate and changed-file tests for Octopus statements;
- confirmation that private keys, plaintext credentials, customer PDFs and
  database backups are absent from Git history and release artefacts;
- correction and verification of the broken
  `OPENDATA_CONFIG_KEYSTORE_PASSWORD` environment-variable path, or removal of
  that interface from release claims; and
- resolved critical gaps or an explicit documented release waiver.

## Procedure

1. update the Maven version, changelog and release notes;
2. freeze ADR statuses and documentation baseline dates;
3. run the release gate from a clean checkout;
4. create the source and application artefacts and checksums;
5. tag the exact verified commit as `v2.0.0`;
6. publish artefacts and notices; and
7. retain test, dependency, database and documentation evidence.

## Compatibility

Patch releases preserve documented CLI and schema behaviour. Breaking CLI,
configuration, plugin-contract or database changes require migration notes and
an appropriate semantic-version increment. Historical Version 1.0.0 release
records remain unchanged.
