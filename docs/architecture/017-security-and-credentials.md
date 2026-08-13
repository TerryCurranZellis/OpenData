# Security and Credentials

**Document ID:** ARCH-017
**Version:** 2.0
**Status:** Encryption implemented; source-baseline remediation required
**Baseline date:** 3 August 2026
**Minimum Java version:** 24

---

## Security objective

Secrets must not be committed to Git, copied into examples, written to logs,
stored in documentation or retained in ordinary source resources. Encryption is
one control; key separation, operating-system permissions and deployment design
are equally important.

## Implemented password protection

`RsaConfigurationPasswordCipher` uses
`RSA/ECB/OAEPWithSHA-256AndMGF1Padding`:

- the public X.509 certificate encrypts the database password;
- the matching PKCS#12 private key store decrypts it;
- encrypted values use the `{enc}` prefix followed by Base64;
- an already encrypted value is not encrypted a second time;
- `ApplicationBootstrapPropertiesLoader` decrypts before constructing runtime
  database configuration;
- `--plugin <id|all> --register` stores the encrypted password in both
  `core.application_property` and the rewritten bootstrap file.

A PKCS#12 password can be supplied through the Java system property
`opendata.config.keystore.password`. The intended environment-variable path is
currently defective: `KEYSTORE_PASSWORD_ENVIRONMENT_VARIABLE` is set to
`nopassword`, so the runtime does not read the documented
`OPENDATA_CONFIG_KEYSTORE_PASSWORD` name. This must be corrected and tested
before that environment interface is claimed. Neither mechanism is a managed
secret provider; both are local password inputs.

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
