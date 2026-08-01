# Bootstrap the OpenData SQL Server Database

**Document ID:** GUIDE-DB-BOOTSTRAP-001  
**Version:** 2.0  
**Status:** Current  
**Baseline date:** 26 July 2026

---

## Prerequisites

- SQL Server instance;
- `sqlcmd`, SSMS, Azure Data Studio or equivalent;
- an administrator identity for database/login creation;
- a password chosen locally for login `OpenData`.

## Installation order

1. `sql/001-create-database-and-login.sql`
2. `sql/002-create-core-schema.sql`
3. `sql/003-create-configuration-store.sql`
4. `sql/004-create-ofgem-schema.sql`
5. `sql/005-seed-reference-data.sql`
6. `sql/006-create-plugin-run-audit.sql`
7. `sql/007-create-openmeteo-schema.sql`
8. `sql/008-grant-application-permissions.sql`
9. `sql/009-grant-shared-schema-permissions.sql`

## PowerShell example

```powershell
. .\scripts\New-ConfigurationCertificate.ps1
New-ConfigurationCertificate

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
    '.\sql\008-grant-application-permissions.sql',
    '.\sql\009-grant-shared-schema-permissions.sql'
)
$scripts | ForEach-Object {
    sqlcmd -S localhost -E -d OpenData -b -i $_
}
```

## Verification queries

```sql
USE OpenData;
SELECT * FROM core.schema_version ORDER BY version;
SELECT name FROM sys.schemas WHERE name IN ('core', 'ofgem', 'openmeteo');
SELECT name FROM sys.database_principals WHERE name IN ('OpenData', 'opendata_app');
SELECT dataset_code, plugin_id, enabled FROM core.dataset;
SELECT OBJECT_ID(N'core.application_property') AS application_property_object;
SELECT OBJECT_ID(N'core.plugin_property') AS plugin_property_object;
SELECT OBJECT_ID(N'core.PluginRun') AS plugin_run_object;
SELECT OBJECT_ID(N'openmeteo.DailyWeather') AS daily_weather_object;
```

Rerun the numbered schema scripts once to confirm they are idempotent. Do not
place the password in a committed script, shell history file or documentation.
