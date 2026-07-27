# 4. Configuration

**Document ID:** USER-004  
**Version:** 1.1  
**Status:** Updated  
**Baseline date:** 27 July 2026

---

Built-in runtime defaults are packaged in `src/main/resources/config/application.properties`.
Do not edit packaged defaults to store a secret. Create an external properties file
and pass it with `--file`.

## Application runtime properties

The current runtime reads these application-level keys from the packaged resource
and then overlays any matching `application.<key>` entries from the external
properties file. Database write runs must supply `application.database.password`
in the external file.

| Property key | Required | Description | Default/resource value |
|---|---|---|---|
| `application.database.driver-class` | Yes | JDBC driver class | `com.microsoft.sqlserver.jdbc.SQLServerDriver` |
| `application.database.url` | Yes | SQL Server JDBC URL | `jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true` |
| `application.database.user` | Yes | SQL Server login | `OpenData` |
| `application.database.password` | Write runs only | SQL Server password supplied externally | none |
| `application.database.pool.name` | Yes | Apache DBCP pool name | `OpenData` |
| `application.database.pool.max-total` | No | Maximum pooled connections | `8` |
| `application.database.pool.max-idle` | No | Maximum idle pooled connections | `8` |
| `application.database.pool.min-idle` | No | Minimum idle pooled connections | `1` |
| `application.database.pool.max-wait-seconds` | No | Wait time for a pooled connection | `30` |
| `application.database.pool.validation-query` | No | Connection validation SQL | `SELECT 1` |
| `application.execution.max-parallel-plugins` | No | Maximum concurrent plugin tasks | `4` |
| `application.execution.shutdown-timeout-seconds` | No | Executor shutdown wait time | `30` |
| `application.logging.directory` | No | Log directory | `logs` |
| `application.logging.file-limit-bytes` | No | Size of each rotating log file | `10485760` |
| `application.logging.file-count` | No | Number of retained log files | `10` |
| `application.logging.append` | No | Append to existing log files | `true` |

## External override examples

### Database, execution, and logging overrides

```properties
application.database.******;databaseName=OpenData;encrypt=true;trustServerCertificate=true
application.execution.max-parallel-plugins=2
application.execution.shutdown-timeout-seconds=45
application.logging.directory=C:\OpenData\logs
application.logging.file-limit-bytes=20971520
```

### Single-plugin override file

For a one-plugin run, plugin properties may be left unscoped:

```properties
application.database.****** Multi-plugin override file

For a multi-plugin run, scope every plugin setting with `plugin.<id>.<key>`:

```properties
application.database.****** `--file C:\OpenData\run.properties` to load the file. Restrict its file
permissions and keep it out of Git.

The separate `src/main/resources/application.properties` is a legacy resource and
is not read by the current runtime.
