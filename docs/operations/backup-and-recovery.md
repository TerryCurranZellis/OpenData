# Backup and Recovery

**Document ID:** OPS-RECOVERY-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

## Protected assets

- SQL Server database and schema version;
- external override and secret configuration;
- archived Ofgem source workbooks;
- application package and matching documentation;
- release/test evidence and logs retained by policy.

Working downloads are replaceable and are not a substitute for an archive or
database backup.

## Recovery principles

Database recovery is owned by SQL Server backup policy. Restore the database,
application package, schema scripts and configuration from mutually compatible
versions. Validate the application principal before permitting writes.

## Plugin replay

OpenMeteo is keyed by location/date and can safely replay the same range; an
identical replay should report all rows skipped. Ofgem replaces one effective
period transactionally and can replay a verified archived workbook.

## Failed runs

Do not manually mark a failed or abandoned run successful. Confirm transaction
rollback, diagnose the cause, then start a new run with a new identity. Retain
the earlier failure as audit evidence.

## Recovery verification

After restore, run plugin listing and dry runs, database health/permission checks,
one controlled write per plugin and data/audit reconciliation before resuming
scheduled operation.
