# Database Configuration Reference

**Document ID:** REF-DB-CONFIG-001  
**Version:** 1.0  
**Baseline date:** 24 July 2026

## Required settings

| Property | Example | Required | Description |
|---|---|---|---|
| `database.url` | `jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true` | yes | Microsoft JDBC connection URL |
| `database.user` | `OpenData` | yes | SQL Server login/database user |
| `database.password` | external value | yes | Never commit to source control |

## Pool settings

| Property | Default | Validation |
|---|---:|---|
| `database.pool.initial-size` | `1` | zero or greater; not above max total |
| `database.pool.min-idle` | `1` | zero or greater; not above max idle |
| `database.pool.max-idle` | `4` | at least one; not above max total |
| `database.pool.max-total` | `12` | at least one |
| `database.pool.max-wait-millis` | `30000` | positive |
| `database.pool.min-evictable-idle-millis` | `300000` | zero or greater |
| `database.pool.validation-query` | `SELECT 1` | non-blank |
| `database.pool.validation-query-timeout-seconds` | `5` | at least one |
| `database.pool.test-on-borrow` | `true` | boolean |

## Development example

```properties
# Store outside Git.
database.url=jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true
database.user=OpenData
database.password=REPLACE_LOCALLY

database.pool.initial-size=1
database.pool.min-idle=1
database.pool.max-idle=4
database.pool.max-total=12
database.pool.max-wait-millis=30000
database.pool.min-evictable-idle-millis=300000
database.pool.validation-query=SELECT 1
database.pool.validation-query-timeout-seconds=5
database.pool.test-on-borrow=true
```

## Production differences

Use a trusted SQL Server certificate and set `trustServerCertificate=false`.
Resolve the password from an approved secret provider and size the pool against
the total number of application processes and SQL Server connection capacity.
