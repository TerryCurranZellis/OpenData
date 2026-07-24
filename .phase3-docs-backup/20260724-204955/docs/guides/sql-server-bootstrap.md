# Bootstrap the OpenData SQL Server Database

**Document ID:** GUIDE-DB-BOOTSTRAP-001  
**Version:** 1.0  
**Baseline date:** 24 July 2026

## Prerequisites

- SQL Server instance;
- `sqlcmd`, SSMS, Azure Data Studio or equivalent;
- an administrator identity for database/login creation;
- a password chosen locally for login `OpenData`.

## Installation order

1. `001-create-database-and-login.sql`
2. `010-create-core-schema.sql`
3. `020-create-ofgem-schema.sql`
4. `030-seed-reference-data.sql`
5. `090-grant-application-permissions.sql`

## PowerShell example

```powershell
sqlcmd -S localhost -E -b `
  -i .\sql\sqlserver\001-create-database-and-login.sql `
  -v OpenDataPassword="YOUR_LOCAL_PASSWORD"

Get-ChildItem .\sql\sqlserver\0[1-9][0-9]-*.sql |
    Sort-Object Name |
    ForEach-Object {
        sqlcmd -S localhost -E -d OpenData -b -i $_.FullName
    }
```

## Verification queries

```sql
USE OpenData;
SELECT * FROM core.schema_version ORDER BY version;
SELECT name FROM sys.schemas WHERE name IN ('core', 'ofgem');
SELECT name FROM sys.database_principals WHERE name IN ('OpenData', 'opendata_app');
SELECT dataset_code, plugin_id, enabled FROM core.dataset;
```

Rerun the numbered schema scripts once to confirm they are idempotent. Do not
place the password in a committed script, shell history file or documentation.
