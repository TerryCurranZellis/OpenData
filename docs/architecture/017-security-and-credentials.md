# Security and Credentials

**Document ID:** ARCH-017  
**Version:** 4.0.0  
**Status:** AES-256-GCM and external key-file configuration implemented; environment verification required  
**Baseline date:** 10 November 2026  
**Minimum Java version:** 24

---

## Security objective

OpenData must not commit usable passwords, AES keys, authentication tokens,
customer statements or backups to Git. Key separation and filesystem
permissions are as important as ciphertext encryption.

## Credential protection

`AesConfigurationPasswordCipher` implements AES-256-GCM with a random
nonce and authentication tag. Encrypted values use the `{aes}` marker and
Base64URL encoding. `ApplicationBootstrapPropertiesLoader` reads
`security.aes-key-file` and other database properties from
`config/application.properties`; the user is **not** required to configure
environment variables or JVM system properties.

The 4.0.0 first-run wizard accepts temporary SQL Server administrator
credentials, creates or validates the external AES key, provisions the
application account, saves encrypted application credentials, and clears
`init=true` only on successful setup. It does not persist the administrator
password. Store the AES key outside tracked source and distributable resources.

## Historical credential risks

Earlier development baselines documented plaintext passwords and PKCS#12
private-key material inside source resources. The reviewed current tree includes
encrypted bootstrap settings with an external key-file path, but that fact
**does not establish** that historical Git commits or previously released
artifacts are clean. Review repository history and previous releases separately,
and rotate any credentials that may ever have been exposed.

Never include a real key, certificate private key, SQL password, customer
statement or database backup in screenshots, logs, documentation or examples.

## SQL Server identity and permissions

The wizard configures the application SQL login/database user and database
permissions. Apply least privilege and test registration, execution and
database access using the configured application account after provisioning.
Do not continue using administrator credentials for normal execution.

## Transport security

Production SQL Server connections should validate the server identity using
trusted certificates. The first-run wizard offers an explicit option to trust
a self-signed SQL Server certificate for local environments only. Do not use
that option as an unexamined production default.

## Logging, files and recovery

Passwords, tokens, authentication headers, AES key material and decrypted
property values must never appear in logs, including detailed debug logs.
Octopus PDFs and downloaded files may contain personal/commercially sensitive
information and require appropriate access restrictions and retention rules.

Core schema creation, plugin installation and bootstrap-file writes do not
form a single distributed transaction. A failed setup leaves `init=true`,
so retry must inspect existing SQL objects and correct permissions before
attempting setup again.

## Release verification

- Confirm key-file permissions and ensure the actual key is not in Git or packages.
- Verify `{aes}` encryption/decryption and bootstrap recovery on a clean Windows
  installation.
- Validate SQL Server TLS certificate policy and least-privilege grants.
- Scan release artifacts and Git history for credentials and private key files.
- Test backup/restore, ingestion idempotency and customer-data retention.

See [first-run setup](../guides/first-run-initialisation.md) and the
[database security guide](../guides/database-configuration-and-security.md).
