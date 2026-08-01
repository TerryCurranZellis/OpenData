# Configuration Reference

**Document ID:** REF-CONFIG-001  
**Version:** 2.0  
**Status:** Updated  
**Baseline date:** 01 August 2026  
**Minimum Java version:** 17

---

## Runtime sources

| Source | Purpose |
|---|---|
| `config/application.properties` | Minimal bootstrap file used to reach SQL Server |
| `config/plugins/index.properties` | Installed plugin list used by `--register` and plugin discovery |
| `config/plugins/<id>.properties` | File-backed plugin definitions used before registration and by `--register` |
| `[core].[application_property]` | Database-backed runtime properties used after registration |
| `[core].[plugin_property]` | Database-backed plugin properties used after registration |
| `--file <path>` | Invocation-only overrides |

## Bootstrap file

`config/application.properties` should contain only:

```properties
application.version=2.0.0
application.use-database-properties=true
database.url=jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true
database.user=OpenData
database.******
```

## Precedence

1. built-in runtime defaults for JDBC pool, execution, and logging;
2. classpath properties before registration, or SQL Server properties after registration;
3. entries from `--file`.

## Override scopes

Application values always use `application.<key>`:

```properties
application.database.******
application.execution.max-parallel-plugins=2
```

A single-plugin file may use unscoped plugin keys:

```properties
property.start-date.value=2025-01-01
```

A multi-plugin file must scope them:

```properties
plugin.openmeteo.property.start-date.value=2025-01-01
plugin.ofgem.property.download.request-timeout.value=PT180S
```

## Registration

Generate the certificate material first with `scripts/New-ConfigurationCertificate.ps1`.
Run `opendata --register` to copy the packaged application and plugin property
sets into SQL Server. After registration, normal execution loads configuration
from SQL Server when `application.use-database-properties=true`.
