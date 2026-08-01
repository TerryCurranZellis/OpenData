# 4. Configuration

**Document ID:** USER-004  
**Version:** 2.0  
**Status:** Updated  
**Baseline date:** 01 August 2026

---

`src/main/resources/config/application.properties` is now a bootstrap file.
After `--register`, it should contain only:

- `application.version`
- `application.use-database-properties`
- `database.url`
- `database.user`
- `database.password` (encrypted)

## Runtime sources

1. Bootstrap file `src/main/resources/config/application.properties`
2. SQL Server tables `[core].[application_property]` and `[core].[plugin_property]`
3. Optional `--file` overrides for one invocation

When `application.use-database-properties=true`, OpenData loads runtime and
plugin properties from SQL Server by default and ignores the packaged plugin
property files during normal execution.

## Bootstrap properties

| Property key | Required | Description |
|---|---|---|
| `application.version` | Yes | Bootstrap version marker |
| `application.use-database-properties` | Yes | Whether SQL Server is the default configuration source |
| `database.url` | Yes | SQL Server JDBC URL |
| `database.user` | Yes | SQL Server login |
| `database.password` | Yes | SQL Server password; encrypted after registration |

## Register configuration in SQL Server

Create the encryption certificate material first:

```powershell
. .\scripts\New-ConfigurationCertificate.ps1
New-ConfigurationCertificate
```

Use `--register` to copy packaged application and plugin properties into SQL
Server and then switch future runs to database-backed configuration:

```properties
application.database.url=jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true
application.database.user=OpenData
application.database.******
```

```text
opendata --register --file C:\OpenData\bootstrap.properties
```

After registration, keep the bootstrap file restricted and out of Git when it
contains environment-specific values.

## Override scopes

Application overrides always use `application.<key>`.

Single-plugin runs may use unscoped plugin values:

```properties
application.database.******
property.start-date.value=2025-01-01
```

Multi-plugin runs must scope plugin values:

```properties
application.database.******
plugin.openmeteo.property.start-date.value=2025-01-01
plugin.ofgem.property.download.request-timeout.value=PT180S
```
