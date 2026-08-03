# Bootstrap the OpenData SQL Server Database

**Document ID:** GUIDE-DB-BOOTSTRAP-001  
**Version:** 2.0  
**Status:** Version 2.0.0 procedure  
**Baseline date:** 3 August 2026

---

## Prerequisites

- a SQL Server instance;
- `sqlcmd`, SSMS, Azure Data Studio or equivalent;
- an administrator identity for database/login creation; and
- a locally chosen password for login `OpenData`.

## Installation order

Run scripts `001` through `009` in the order documented in
[`sql/README.md`](../../sql/README.md). Script `010` contains optional read-only
verification queries.

## PowerShell example

```powershell
sqlcmd -S localhost -E -b `
  -i .\sql\001-create-database-and-login.sql `
  -v OpenDataPassword="YOUR_LOCAL_PASSWORD"

$scripts = @(
    '.\sql\002-create-core-schema.sql',
    '.\sql\003-create-configuration-store.sql',
    '.\sql\004-create-ofgem-schema.sql',
    '.\sql\005-seed-reference-data.sql',
    '.\sql\006-create-plugin-run-audit.sql',
    '.\sql\007-create-openmeteo-schema.sql',
    '.\sql\007a-create-octopus-schema.sql',
    '.\sql\008-grant-application-permissions.sql',
    '.\sql\009-grant-shared-schema-permissions.sql'
)
$scripts | ForEach-Object {
    sqlcmd -S localhost -E -d OpenData -b -i $_
}

sqlcmd -S localhost -E -d OpenData -b -i .\sql\010-verification-queries.sql
```

## Verification

Confirm schemas `core`, `ofgem`, `openmeteo` and `octopus`; configuration tables;
`core.PluginRun`; plugin business tables; role `opendata_app`; and the user-role
membership created by the bootstrap scripts.

Rerun the numbered schema scripts against a disposable environment to confirm
idempotency. Do not place the SQL login password in a committed script,
documentation file or persistent shell history.

After SQL deployment, prepare the certificate pair and run configuration
registration as described in
[Configure database access securely](database-configuration-and-security.md).
