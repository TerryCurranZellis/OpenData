# Backup and Recovery

**Document ID:** OPS-RECOVERY-001  
**Version:** 2.0  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 3 August 2026

---

## Protected assets

- the `OpenData` SQL Server database and schema-version history;
- the encrypted bootstrap file and replacement certificate/private key;
- protected local override files;
- archived Ofgem workbooks and Octopus statements retained by policy;
- application package/source revision and matching documentation;
- operational logs and release/test evidence.

The encrypted database password is recoverable only with the matching private
key. Back up both under separate access control and test restoration.

## Recovery principles

Restore the database, application revision, schema scripts, bootstrap file and
certificate pair as one compatible set. Never restore a private key into a
broader-access source directory. After restoration, test the application
principal with the same JVM trust configuration used by the service.

## Plugin replay

- **OpenMeteo:** keyed by location/date; an identical replay should skip
  unchanged rows.
- **Ofgem:** can replay a verified workbook/effective period; the period load is
  transactional.
- **Octopus:** a file is skipped only when file name and SHA-256 match a completed
  statement ledger row. Preserve or deliberately reconcile the ledger and
  archived PDFs together.

## Failure cases

- If a transaction failed, confirm rollback and start a new run.
- If audit completion failed after plugin work succeeded, retain the failure and
  reconcile business tables before retrying.
- If Octopus committed but archive movement failed, do not delete or rename the
  source file until its ledger row and target archive have been checked.
- If registration was interrupted, compare configuration tables with the local
  bootstrap file before rerunning `--register`.

## Recovery verification

1. run `sql/010-verification-queries.sql`;
2. verify configuration decryption and `--list-plugins`;
3. run supported Ofgem/OpenMeteo dry runs;
4. perform one controlled write per plugin;
5. reconcile audit, business rows and source/statement ledgers; and
6. resume scheduling only after log-based outcome detection has been tested.
