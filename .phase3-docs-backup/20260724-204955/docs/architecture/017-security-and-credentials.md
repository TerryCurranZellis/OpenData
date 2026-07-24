# Security and Credentials

**Document ID:** ARCH-017  
**Version:** 1.1  
**Status:** Implemented with production hardening required  
**Baseline date:** 24 July 2026  
**Minimum Java version:** 17

---

## Credential rule

Secrets are never committed to Git, copied into example files, written to logs
or stored in execution snapshots. Configuration files in the repository contain
only empty values or credential references.

The database password is resolved externally and supplied to
`ApplicationConfig`. The initial practical provider may be a protected local
properties file outside the repository. Later providers can include Windows
Credential Manager, an environment-specific secret store or a vault.

## SQL Server identity and permissions

- server login: `OpenData`;
- database user: `OpenData`;
- application role: `opendata_app`;
- database: `OpenData`.

The application role receives only the DML and execute permissions required for
normal imports. It does not receive `db_owner`, `ALTER ANY SCHEMA`, login
management or database creation permissions.

## Transport security

The local-development JDBC URL uses encryption with
`trustServerCertificate=true`. This encrypts traffic but does not validate the
server certificate chain. Production must use a trusted SQL Server certificate
and `trustServerCertificate=false`.

## Logging controls

Connection URLs may be logged only after removing user information and secret
parameters. Passwords, tokens, complete authentication headers and credential
provider payloads are prohibited at every log level.

## Files and provenance

Downloaded files are size-limited and stored in controlled directories. SHA-256
is used for provenance and duplicate detection; it is not a malware check.
Source workbook names and cell references are retained as non-secret lineage
metadata.

## Required production hardening

- protected credential provider;
- trusted SQL Server certificate;
- restricted network path to SQL Server;
- operating-system permissions on configuration and staging directories;
- database backup and restore testing;
- dependency and secret scanning in CI;
- retention rules for source files and error details.
