# Database Configuration Reference

**Document ID:** REF-DB-CONFIG-001  
**Version:** 2.0  
**Status:** Version 2.0.0 implementation reference  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## Bootstrap settings

| Property | Required | Description |
|---|---|---|
| `database.url` | yes | Microsoft JDBC connection URL |
| `database.user` | yes | SQL Server login/database user |
| `database.password` | yes | plaintext before registration, `{enc}` RSA ciphertext afterwards |
| `application.use-database-properties` | yes | selects SQL Server configuration after registration |
| `application.version` | no | version marker; defaults to `2.0.0` |

## Configuration tables

- `core.application_property(property_key, property_value, is_encrypted, updated_at)`
- `core.plugin_property(plugin_id, property_key, property_value, updated_at)`

Database application values marked encrypted are normalised with the `{enc}`
prefix when loaded and decrypted by the RSA password cipher.

## Certificate and key-store paths

```text
src/main/resources/config/security/opendata-config-public.cer
src/main/resources/config/security/opendata-config-private.pfx
```

The only dependable non-code PFX password input in this baseline is JVM property
`opendata.config.keystore.password`. The intended environment-variable constant
is incorrect.

## Production controls

Use a trusted SQL Server certificate, `trustServerCertificate=false`, protected
bootstrap/private-key files, a rotated password and the supplied least-privilege
role. The current source-tree paths and tracked development secrets must be
corrected before production packaging.
