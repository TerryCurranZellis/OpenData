# Security and Credentials

**Document ID:** ARCH-017
**Version:** 3.0.0  
**Status:** Encryption implemented; source-baseline remediation required
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Security objective

Secrets must not be committed to Git, copied into examples, written to logs,
stored in documentation or retained in ordinary source resources. Encryption is
one control; key separation, operating-system permissions and deployment design
are equally important.

## Implemented password protection (AES migration)

`AesConfigurationPasswordCipher` uses AES-256-GCM with a fresh 96-bit nonce,
128-bit authentication tag and a 256-bit random key in an ACL-protected file
outside the repository and packaged resources. Ciphertext uses the `{aes}`
prefix followed by unpadded Base64URL. An unchanged `ConfigurationPasswordCipher`
interface keeps bootstrap loading, application registration and the GUI
integration intact. The bootstrap and `core.application_property` are both
re-encrypted when configuration is registered. Legacy `{enc}` RSA values need
an explicit migration from the known SQL password.

For setup and migration see
`docs/guides/database-configuration-and-security.md`.

## Critical baseline finding

The uploaded project baseline includes a non-blank plaintext database password
in the tracked bootstrap resource and a private PKCS#12 key store below
`src/main/resources/config/security`. Co-locating encrypted values and the
private decryption key in the repository removes most of the protection offered
by encryption and risks publishing live credentials.

Before release or production use:

1. rotate the database password if the committed value has ever been usable;
2. remove plaintext credentials and private key stores from Git history and the
   distributable source archive;
3. add local bootstrap, private key and certificate-output paths to an explicit
   ignore/deployment policy;
4. generate environment-specific key material outside the repository;
5. restrict the bootstrap and private key store to the service identity;
6. use a strong PKCS#12 password supplied outside source control;
7. verify that build, release and documentation archives exclude private key
   material.

The public certificate may be distributed when appropriate. The private key
must not be treated as an application resource.

## SQL Server identity and permissions

- server login: `OpenData`;
- database user: `OpenData`;
- application role: `opendata_app`;
- database: `OpenData`.

The role should receive only required read/write and execute rights. Broad
schema grants must be reviewed against the configuration, audit and plugin
operations actually performed.

## Transport security

The local development URL uses `encrypt=true` with
`trustServerCertificate=true`. That encrypts traffic without validating the
server identity. Production requires a trusted SQL Server certificate and
`trustServerCertificate=false`.

## Logging and files

Passwords, private-key passwords, tokens, authentication headers and decrypted
configuration values are prohibited at every log level. Source files may contain
personal or commercially sensitive data; Octopus statements and archives need
restricted directory permissions and an explicit retention policy.

## Remaining hardening

- external writable configuration location;
- managed or operating-system-backed secret retrieval;
- trusted SQL Server certificate;
- bounded downloads and retry policies;
- restricted database and filesystem access;
- backup/restore tests;
- dependency, secret and release-archive scanning;
- secure deletion and retention rules for customer statements and failure data.
