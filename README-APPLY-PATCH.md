# OpenData 3.1.0 — AES-256-GCM migration patch

This is a **source-file patch**, not a complete OpenData distribution.
Copy the included files into your project checkout, keeping the relative
folders. Back up your repository and work from a new Git branch.

## What this patch changes

- Replaces uses of `RsaConfigurationPasswordCipher` in CLI, runtime, GUI and
  JDBC configuration integrations with `AesConfigurationPasswordCipher`.
- Keeps the `ConfigurationPasswordCipher` interface unchanged.
- Uses `{aes}` + unpadded Base64URL ciphertext, AES-256-GCM with an independent
  nonce for each encrypted value.
- Uses `OPENDATA_CONFIG_KEY_FILE` or `-Dopendata.config.key-file=...` to select
  a private 32-byte key file outside your repository and application JAR.
- Preserves prefixed encrypted values from SQL Server, and fails explicitly
  for obsolete RSA/unprefixed legacy encrypted values rather than guessing.
- Adds JUnit tests and a Windows PowerShell 5.1 key generator.
- Updates primary security/setup documentation and `.gitignore`.

## Files to remove from repository after applying the patch

These OLD files are intentionally **not included** in the patch ZIP:

- `opendata-core/src/main/java/com/towermarsh/opendata/config/RsaConfigurationPasswordCipher.java`
- `opendata-core/src/test/java/com/towermarsh/opendata/config/RsaConfigurationPasswordCipherTest.java`
- `scripts/New-ConfigurationCertificate.ps1`
- `opendata-core/src/main/resources/config/security/opendata-config-private.pfx`
- `opendata-core/src/main/resources/config/security/opendata-config-public.cer`

The private `.pfx` is present in the source ZIP you uploaded. Ensure it is
removed from **Git history and distributable archives** if it was used to
protect real credentials; only removing it from the working tree is not
sufficient. If exposed, rotate the affected SQL password.

## Migration procedure

1. Back up the bootstrap and the SQL Server `core.application_property` table.
2. Create and ACL-protect a private AES key file using the PowerShell script;
   configure `OPENDATA_CONFIG_KEY_FILE` in the OpenData process environment.
3. Ensure OpenData can connect using the **real existing SQL password in cleartext**
   by temporarily setting `database.password` in the local bootstrap file.
   You can leave the SQL login itself unchanged unless rotating it for security.
4. Run the NEW build with `--plugin all --register`. Registration updates the
   encrypted password in both the SQL Server configuration table and bootstrap.
   Do not try normal runtime execution until registration completes.
5. Check both stored values start with `{aes}`. Verify `--list-plugins` and a
   normal execution, and securely remove plaintext copies.
6. Remove/rotate the old RSA material. Review source-control history and
   release packaging to prevent distributing credentials or the old `.pfx`.

**Never package the temporary cleartext bootstrap into a JAR or publish it.**
When building locally, keep the bootstrap ciphertext until immediately before
migration/registration, and rebuild only after verifying the generated `{aes}`
bootstrap file. The existing loader first tries the local source-tree path,
then packaged resource; check your chosen NetBeans/Maven working directory.

## Build and validation

- The original Maven build requires JDK 24 or later. This container only has
  JDK 21 and no Maven, so the full OpenData test suite was **not run**.
- The new cipher itself compiled using `javac` 21; a standalone smoke test
  passed password roundtrip, variable nonce, ciphertext length, tamper
  detection and legacy-RSA rejection.
- Run `mvn -pl opendata-core -am test` with JDK 24+ in your own project.

See `docs/guides/database-configuration-and-security.md` for full details.
