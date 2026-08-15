# Security Policy

## Supported versions

| Version | Status |
|---|---|
| 3.0.0 development/release-candidate baseline | Security fixes and release review are active |
| 2.0.0 historical development baseline | Upgrade to the current candidate is recommended |
| 1.0.0 historical release record | Upgrade is recommended |
| Earlier development snapshots | Not supported |

Version 3.0.0 must not be described as production-ready until the mandatory
release gates in the final release checklist are resolved or formally waived.

## Reporting a vulnerability

Do not open a public issue containing exploit details, credentials, private keys
or customer data. Send a private report to `terry.curran@towermarsh.co.uk` with
the subject `OpenData security report`.

Include the affected version/commit, component, reproduction steps, impact and
suggested remediation. Use synthetic or redacted evidence.

## Known Version 3.0.0 release security items

The current source uses an encrypted `{enc}` bootstrap database-password value
and correctly checks `OPENDATA_CONFIG_KEYSTORE_PASSWORD` after the JVM system
property. The following deployment/release issues still require explicit review:

1. A private PKCS#12 key store is tracked beneath
   `src/main/resources/config/security`; deployment-specific private keys must
   not be published or reused as production key material.
2. `RsaConfigurationPasswordCipher` retains `nopassword` as its development
   fallback when no keystore password property/environment variable is supplied.
3. The development JDBC URL uses `trustServerCertificate=true`, which should be
   replaced by validated SQL Server certificate trust for production.
4. The SQL Server JDBC dependency is a preview build and requires explicit
   release approval or replacement with a verified stable version.

Documentation of a release item does not remove it. Use deployment-specific
keys and credentials, test the secret-input path, validate SQL Server trust and
review the resolved dependency set before final release approval.

## Credential model

Registration encrypts the bootstrap database password with the public certificate
and startup decrypts `{enc}` values using the private key. RSA encryption protects
the value at rest in the properties file; it does not protect the password after
decryption, replace filesystem permissions, or provide key rotation.

Production controls must include:

- deployment-specific public/private keys;
- a strong PFX password supplied through a tested secret channel;
- private-key access limited to the application identity;
- backup, rotation, expiry and recovery procedures;
- least-privilege SQL Server credentials;
- validated TLS with `trustServerCertificate=false`; and
- failure-closed tests for unavailable, wrong or expired keys.

## Customer and operational data

Octopus statements can contain personal and financial information. Protect input,
archive, failure, log and backup locations. Do not log complete statement text,
credentials, connection strings, private keys or unredacted identifiers. Use
synthetic fixtures for tests and documentation wherever possible.

Ofgem and Open-Meteo datasets are less sensitive but still require integrity,
provenance and attribution controls. Validate downloaded content, retain hashes
and do not trust remote filenames or content types without checking them.

## Dependency and build security

- Build from a clean, reviewed commit.
- Pin direct dependency and build-plugin versions.
- Inspect the resolved dependency graph and vulnerability results.
- Treat preview dependencies as explicit release risks.
- Protect release credentials and signing material outside the repository.
- Verify generated archive contents and SHA-256 checksums.
- Never publish local configuration, customer files, database backups or private
  deployment keys.

## Disclosure and credit

Validated reports may be credited in release notes unless the reporter requests
anonymity. Disclosure timing must prioritise a verified fix and protection of
users and customer data.
