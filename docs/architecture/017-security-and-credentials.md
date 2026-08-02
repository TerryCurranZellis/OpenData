# Security and Credentials

**Document ID:** ARCH-017  
**Version:** 1.2  
**Status:** Partial; critical credential remediation required  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

## Credential rule

The target rule is that secrets are never committed to Git, copied into example
files, written to logs or stored in execution snapshots. The maintained
bootstrap resource at `src/main/resources/config/application.properties`
currently leaves `database.password` blank in Git, but any environment-specific
override file still requires the same protection.

`OverrideConfiguration` can supply
`application.database.password` from a protected external properties file.
There is no environment-variable or secret-provider integration. The
credential-reference model is not resolved at runtime.

## SQL Server identity and permissions

- server login: `OpenData`;
- database user: `OpenData`;
- application role: `opendata_app`;
- database: `OpenData`.

SQL scripts create the intended least-privilege role. The broader
`GRANT ... ON SCHEMA::core` in `sql/009-grant-shared-schema-permissions.sql`
should still be reviewed as the permission model evolves.

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

The reusable `HttpDataDownloader` enforces a size limit, but the active Ofgem
`DirectHttpDownloadStrategy` and OpenMeteo response path do not. Ofgem stores
SHA-256 source provenance; it is not a malware check. Source workbook names and
cell references are retained as non-secret lineage metadata.

## Required production hardening

- protected credential provider;
- continued exclusion of live credentials from tracked configuration;
- trusted SQL Server certificate;
- bounded downloads on active plugin paths;
- restricted network path to SQL Server;
- operating-system permissions on configuration and staging directories;
- database backup and restore testing;
- dependency and secret scanning in CI;
- retention rules for source files and error details.
