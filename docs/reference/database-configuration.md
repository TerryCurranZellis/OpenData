# Database Configuration Reference

**Document ID:** REF-DB-CONFIG-001  
**Version:** 2.0  
**Status:** Updated  
**Baseline date:** 01 August 2026  
**Minimum Java version:** 17

---

## Bootstrap settings

| Property | Example | Required | Description |
|---|---|---|---|
| `database.url` | `jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true` | yes | Microsoft JDBC connection URL |
| `database.user` | `OpenData` | yes | SQL Server login/database user |
| `database.password` | encrypted value after registration | yes | Password used to reach SQL Server |
| `application.use-database-properties` | `true` | yes | Enables database-backed configuration loading |

## Built-in defaults

These values are supplied by the runtime unless overridden through the database
or `--file`:

- `database.driver-class=com.microsoft.sqlserver.jdbc.SQLServerDriver`
- `database.pool.name=OpenData`
- `database.pool.max-total=8`
- `database.pool.max-idle=8`
- `database.pool.min-idle=1`
- `database.pool.max-wait-seconds=30`
- `database.pool.validation-query=SELECT 1`

## Configuration store tables

- `[core].[application_property]`
- `[core].[plugin_property]`

`--register` seeds both tables from the packaged property files.
Generate `opendata-config-public.cer` and `opendata-config-private.pfx` with
`scripts/New-ConfigurationCertificate.ps1` before the first registration run.

## Production differences

Use a trusted SQL Server certificate and set `trustServerCertificate=false`.
Keep the bootstrap file restricted because it still contains the encrypted
password needed to open the configuration database.
