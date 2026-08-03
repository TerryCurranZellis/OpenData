# Security Policy

## Supported versions

| Version | Supported |
|---|---|
| `2.0.x` development and release-candidate baseline | Yes |
| `1.0.x` | Security fixes only when explicitly backported |
| Historical development snapshots | No |

Support periods may be refined when Version 2.0.0 is formally tagged.

## Reporting a vulnerability

Do not open a public issue for a suspected vulnerability. Send a private report
to `terry.curran@towermarsh.co.uk` with the subject `OpenData security report`.

Include the affected version or commit, component, configuration, reproduction
steps, likely impact and any suggested remediation. Do not attach live database
credentials, private keys, customer statements or unredacted personal data.

## Version 2.0.0 credential model

OpenData needs database credentials before it can load runtime configuration from
SQL Server. The bootstrap file therefore contains the database URL, username and
an encrypted password.

- Registration encrypts the password using the public key in
  `opendata-config-public.cer`.
- Startup decrypts `{enc}` values using the private key in
  `opendata-config-private.pfx`.
- The PFX password protects access to the private key; it is not the encryption
  key itself.
- The supplied development PFX password is `nopassword`.
- A deployment can override the PFX password with the JVM property
  `opendata.config.keystore.password` or environment variable
  `OPENDATA_CONFIG_KEYSTORE_PASSWORD`.

The supplied certificate pair and default password are development conveniences.
For production:

1. generate a deployment-specific certificate and private key;
2. use a strong, separately protected PFX password;
3. restrict read access to the PFX to the application identity;
4. do not commit replacement private keys or passwords;
5. establish certificate rotation and recovery procedures; and
6. test that an unavailable or incorrect private key fails closed.

RSA encryption protects the stored bootstrap value. It does not protect a
password after decryption in application memory, replace operating-system access
controls, or eliminate the need for SQL Server transport security and least
privilege.

## Sensitive data

Octopus Energy statements and their extracted records can contain names,
addresses, account references, meter identifiers, tariff details, payment data,
consumption and billing history. Deployments must:

- restrict access to input, working, archive and failure directories;
- exclude statement PDFs and extracted fixtures from source control;
- protect the Octopus tables and database backups;
- avoid logging full statement text or personal identifiers;
- securely dispose of temporary files;
- define a retention policy for source PDFs and extracted records; and
- redact data before sharing logs, screenshots or test evidence.

## General security expectations

- Use a least-privilege SQL Server account.
- Replace `trustServerCertificate=true` with validated certificate trust outside
  controlled development environments.
- Keep Java, dependencies, SQL Server and build tooling patched.
- Validate remote locations, downloaded content and local input filenames.
- Treat plugins, property rows, certificates and configuration files as trusted
  deployment inputs.
- Keep writable directories outside locations served publicly.
- Test database and certificate backup and restoration.
- Review dependency and data-source notices for each release.

## Disclosure and credit

Validated reports will be credited in release notes unless the reporter requests
anonymity. The maintainer will coordinate remediation and disclosure according
to the risk and availability of a verified fix.
