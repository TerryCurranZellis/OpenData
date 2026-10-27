# Configure Database Access Securely — AES-256-GCM

**Applies to:** OpenData 3.1.0 AES migration patch  
**Requires:** JDK 24+; Windows PowerShell 5.1 for the included setup script

## Design

`AesConfigurationPasswordCipher` uses `AES/GCM/NoPadding` with a **random
12-byte nonce**, a **128-bit authentication tag** and a **32-byte key** stored
outside the project and JAR. Values are prefixed `{aes}` and Base64URL encoded
without padding. A 12-character ASCII password produces approximately 59
characters of stored ciphertext (the length varies with the password).

There is no certificate, `.pfx` or keystore password. The key file is one line
containing Base64 text representing exactly 32 cryptographically random bytes.
**Do not store the key file in Git, Maven resources, the JAR or SQL Server.**

## First-time setup (Windows PowerShell 5.1)

1. Choose a private location outside the OpenData checkout, accessible only to
   the OpenData runtime account and administrators.
2. Generate the key file **once**:

   ```powershell
   .\scripts\New-ConfigurationEncryptionKey.ps1 `
       -Path 'C:\ProgramData\OpenData\security\database.key'
   ```

3. Restrict file and containing-folder NTFS permissions to the runtime account
   and trusted administrators. Do not rely on the generator to set ACLs.
4. Configure the environment variable in the shell/service that starts OpenData:

   ```powershell
   $env:OPENDATA_CONFIG_KEY_FILE = 'C:\ProgramData\OpenData\security\database.key'
   ```

   Alternatively pass the **file path**, not the key, with
   `-Dopendata.config.key-file=C:\ProgramData\OpenData\security\database.key`.
   This only changes the JVM startup arguments; no secret is passed on the
   command line.

## Migration from the old RSA setup

Back up your bootstrap file and SQL Server configuration table, and arrange
maintenance downtime. Existing `{enc}` RSA ciphertext **cannot be decrypted**
by the replacement cipher. Do not throw away the old key until the migration
is verified (store it securely offline, not in the repository).

1. Generate and protect the new AES key file (as above).
2. Set the local `opendata-core/src/main/resources/config/application.properties`
   `database.password` to the correct **cleartext SQL login password**.
   The DB login password itself does **not** have to change to adopt AES.
3. Keep `database.url` and `database.user` unchanged; you may set
   `application.use-database-properties=false` until registration completes.
4. Run `opendata --plugin all --register` using the new build. This code
   encrypts the password into BOTH `core.application_property` and the
   bootstrap file, with new `{aes}` values.
5. Confirm both values start with `{aes}` and test `--list-plugins`, the GUI
   and a normal plugin execution. Ensure the process still has key file access.
6. Remove all plaintext temporary copies; scrub old private `.pfx` and `.cer`
   files from distributable resources. Remove them from source control, and
   rotate the SQL Server credential if it was ever exposed publicly.

**Important:** Existing production SQL Server configuration may contain a
legacy RSA value. A single successful registration updates that value before
normal execution resumes. Do not run normal plugins before re-registration.

## Key lifecycle and safety

- Back up the AES key file securely: losing it makes existing ciphertext
  undecryptable. Do not regenerate it casually; that is a key rotation.
- A random nonce is generated on each encryption. Two values for the same
  password will differ; this is expected.
- Invalid or tampered ciphertext fails authentication. Legacy `{enc}` values
  fail with an explicit migration error; no silent fallback to plaintext.
- Do not store the key file beside `application.properties`, in the JAR or
  alongside encrypted SQL properties.
- Check that release builds and documentation archives contain **no** private
  keys, passwords or development secrets.
- Production SQL connections should validate the server certificate, not use
  `trustServerCertificate=true`.
