# Backup and Recovery

**Document ID:** OPS-RECOVERY-001  
**Version:** 4.2.0 (current Maven source)  
**Status:** Current AES configuration recovery guidance  
**Baseline date:** 15 August 2026  

---

## Protected assets

- `OpenData` database including `core.plugin_registry`, properties and schema history;
- writable `config/application.properties` and the matching external AES-256 key file specified by `security.aes-key-file`;
- external complete plugin definition files retained by deployment policy;
- provider archives, logs and release/test evidence.

Restore the database, compatible application JARs, SQL scripts, bootstrap and
**matching original AES key file** as one compatible set. Do not generate a new AES
key when restoring existing `{aes}` ciphertext. Test the application SQL
principal and SQL Server TLS certificate trust settings.

Unregister removes metadata/configuration, not provider data or historical audit.
After accidental unregister, re-register the correct definition and restore the
intended enabled status.

## Recovery verification

1. run `sql/010-verification-queries.sql`;
2. verify decryption and `--list-plugins`;
3. compare registry/status with the recovery record;
4. run `--plugin all --dry-run`;
5. perform controlled write/replay tests;
6. reconcile audit, provider data and file ledgers.
