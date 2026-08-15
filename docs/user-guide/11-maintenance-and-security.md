# 12. Maintenance and Security

**Document ID:** USER-011  
**Version:** 3.0.0  
**Status:** Version 3.0.0 pre-production baseline  
**Baseline date:** 15 August 2026  

---

## Routine maintenance

- review Ofgem page/workbook and Open-Meteo API changes;
- monitor final status, terminal audit rows, metrics, logs and database capacity;
- reconcile Octopus input, archive and statement ledger after every warning;
- test replay/idempotency after code, schema or configuration changes;
- retain source archives and SQL backups according to policy; and
- rebuild and validate documentation when behaviour changes.

## Security controls

- remove the tracked plaintext bootstrap credential and tracked private PFX;
- rotate any credential that may have been exposed;
- protect the replacement private key and bootstrap file with operating-system
  ACLs;
- use a least-privilege SQL principal;
- use `encrypt=true` with a trusted SQL Server certificate and
  `trustServerCertificate=false` outside local development;
- treat downloaded workbooks, API responses and PDFs as untrusted input; and
- restrict Octopus statements, logs and backups as personal data.

Encryption of the bootstrap password is not a substitute for protecting the
private key. Anyone who can read both the encrypted value and the PFX can recover
the database password.

## Recovery

After a failure, confirm transaction rollback and retain the failed audit row.
Start a new run rather than changing an old row to success. After database
restore, verify configuration, plugin listing, supported dry runs and one
controlled write per plugin before resuming routine execution.

See [Backup and recovery](../operations/backup-and-recovery.md) for the complete
administrator procedure.
