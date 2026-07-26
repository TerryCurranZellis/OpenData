# Database Configuration Reference

**Document ID:** REF-DB-CONFIG-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

## Required settings

| Property | Example | Required | Description |
|---|---|---|---|
| `database.driver-class` | `com.microsoft.sqlserver.jdbc.SQLServerDriver` | yes | JDBC driver class |
| `database.url` | `jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true` | yes | Microsoft JDBC connection URL |
| `database.user` | `OpenData` | yes | SQL Server login/database user |
| `database.password` | external value | write mode | Must be non-blank for a database-writing run |
| `database.pool.name` | `OpenData` | yes | DBCP pool name |

## Pool settings

| Property | Default | Validation |
|---|---:|---|
| `database.pool.min-idle` | `1` | zero or greater; not above max idle |
| `database.pool.max-idle` | `8` | zero or greater; not above max total |
| `database.pool.max-total` | `8` | at least one |
| `database.pool.max-wait-seconds` | `30` | positive integer |
| `database.pool.validation-query` | `SELECT 1` | non-blank |

## External override example

```properties
# Store outside Git.
application.database.url=jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true
application.database.user=OpenData
application.database.password=REPLACE_LOCALLY
application.database.pool.max-total=8
application.database.pool.max-idle=8
application.database.pool.min-idle=1
application.database.pool.max-wait-seconds=30
application.database.pool.validation-query=SELECT 1
```

## Production differences

Use a trusted SQL Server certificate and set `trustServerCertificate=false`.
The current runtime has no secret-provider integration, so supply the password
through a restricted external override until that gap is implemented. Size the
pool against the number of application processes, plugin concurrency and SQL
Server connection capacity.
