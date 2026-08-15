# Backup and Recovery

**Document ID:** OPS-RECOVERY-001  
**Version:** 3.0.0  
**Status:** Version 3.0.0 operational baseline  
**Baseline date:** 15 August 2026  

---

## Protected assets

- `OpenData` database including `core.plugin_registry`, properties and schema history;
- encrypted bootstrap and matching protected private key;
- external complete plugin definition files retained by deployment policy;
- provider archives, logs and release/test evidence.

Restore database, application revision, schema scripts, bootstrap and certificate
as one compatible set. Test the application principal and JVM trust settings.

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
