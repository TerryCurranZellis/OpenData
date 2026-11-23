# Configure Database Access Securely — AES-256-GCM

**Applies to:** OpenData 4.x with the external AES key-file setting  
**Build requirements:** Windows PowerShell 5.1 for the supplied key generator; current Maven reactor requires JDK 26+

## Encryption design

`AesConfigurationPasswordCipher` uses AES/GCM/NoPadding with a random 12-byte nonce, a 128-bit authentication tag and a 32-byte AES key. Encrypted values use the `{aes}` prefix with Base64URL ciphertext. A new nonce is generated for every encryption, so encrypting the same password twice gives different text.

The external key file contains Base64 representing exactly 32 random bytes. **It is not encrypted.** Never commit the key file, database credentials, personal input files or usable tokens to Git or a release package.

## New installations

New installations should use the [first-run GUI wizard](first-run-initialisation.md), which provisions SQL Server using a temporary administrator connection and writes the completed `config/application.properties` only after successful setup. Choose an AES key-file path outside the source checkout; the wizard can create a new random key or validate an existing one.

For a manually prepared key on Windows PowerShell 5.1:

```powershell
.\scripts\New-ConfigurationEncryptionKey.ps1 -Path 'C:\ProgramData\OpenData\security\database.key'
```

Configure the key-file **path** through the normal bootstrap property:

```properties
security.aes-key-file=C:/ProgramData/OpenData/security/database.key
```

Do not require `OPENDATA_CONFIG_KEY_FILE` or `-Dopendata.config.key-file`. Those were legacy fallbacks; `security.aes-key-file` is the current setting. Limit access to the file and its containing directory to the OpenData runtime identity and trusted administrators using NTFS ACLs.

## Upgrading existing installations

1. Back up the SQL Server database, writable deployment `config/application.properties` and existing encryption material securely.
2. If the application was already migrated to `{aes}`, **retain the original key file** and set `security.aes-key-file` to its correct absolute path. Do not generate a replacement key.
3. If upgrading from the old `{enc}` RSA certificate format, perform a controlled credential migration with an administrator in a disposable/test environment before production. The AES reader cannot decrypt `{enc}`. Recover the correct SQL password through the normal credential-management process; do not publish it or store it in Maven resources.
4. Verify SQL connectivity, persisted `core.application_property` values, plugin registry visibility, GUI startup and safe provider dry runs. For existing working databases keep `init=false` and do not rerun the first-run wizard.
5. Securely remove any temporary plaintext passwords. Retain old migration material only as required for recovery, outside source control.

Losing the AES key makes existing ciphertext unrecoverable. Key rotation is a separate migration: re-encrypt stored ciphertext before discarding the old key. Always validate the server TLS certificate for remote or production SQL Server connections.

See the [first-run guide](first-run-initialisation.md) and [configuration reference](../reference/configuration-reference.md).
