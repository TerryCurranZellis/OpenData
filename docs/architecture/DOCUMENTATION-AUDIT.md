# Documentation Audit

**Document ID:** REVIEW-DOC-AUDIT-001  
**Version:** 2.0  
**Status:** Current Version 2.0.0 implementation audit  
**Baseline date:** 3 August 2026

## Summary

The uploaded Version 2.0.0 source has advanced beyond the 26 July documentation
baseline. Database-backed configuration registration and the local-file Octopus
pipeline are implemented and must no longer be described as future or
placeholder work.

## Corrected statements

| Superseded statement | Current wording |
|---|---|
| Octopus has placeholder extract/load/finalise stages | The normal write path implements local PDF discovery, hashing, parsing, transactional persistence and post-commit archive; dry run still fails at the processed-ledger lookup |
| Database-backed plugin settings are future work | Application and plugin property tables, registration and JDBC loading are implemented |
| Configuration remains packaged only | The bootstrap remains local, while runtime/plugin values can be SQL Server-backed after `--register` |
| Password protection is not implemented | RSA OAEP encryption/decryption is implemented, but key separation and tracked-source remediation are inadequate |
| Only Ofgem and OpenMeteo are executable reference plugins | Ofgem, OpenMeteo and Octopus are executable through the same coordinator |
| Bootstrap class is `Main` | The current entry class is `OpenData` |
| Current baseline is 26 July | This audit is based on the uploaded 3 August 2026 archive |

## Material that remains future or transitional

Documents must retain implementation qualifiers for:

- dynamic plugin installation or database-backed implementation registry;
- internal scheduling;
- multiple database engines;
- a unified run/provenance identity model;
- installed/executable packaging with external writable configuration;
- non-zero process exit-code mapping;
- managed secret-provider integration;
- direct Octopus account/API or IMAP statement acquisition;
- completed live SQL Server acceptance and production hardening.

## Release-blocking documentation finding

The source archive contains sensitive bootstrap material. Documentation must not
suggest that RSA encryption alone makes this safe. The private key store and any
usable plaintext password must be excluded from source and release archives,
and affected credentials should be rotated.

## Recommended repository cleanup

1. remove plaintext credentials and private key material from tracked history;
2. externalise bootstrap and key-store paths;
3. protect the PKCS#12 password outside source control;
4. unify `core.PluginRun` and domain ingestion identities;
5. remove or deprecate duplicate compatibility classes;
6. configure executable packaging and process exit codes;
7. complete live SQL Server, rollback, idempotency and permission verification.
