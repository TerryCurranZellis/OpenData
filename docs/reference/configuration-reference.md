# Configuration Reference

**Document ID:** REF-CONFIG-001  
**Version:** 2.0  
**Status:** Version 2.0.0 implementation reference  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## Sources and precedence

| Order | Source | Purpose |
|---|---|---|
| 1 | built-in defaults | JDBC pool, execution and logging defaults |
| 2 | bootstrap file | database reachability and database-backed switch |
| 3 | classpath or SQL Server | runtime and plugin property sets |
| 4 | `--file` | invocation-only overrides |

When `application.use-database-properties=false`, runtime/plugin values come from
classpath resources. When true, they come from `core.application_property` and
`core.plugin_property`.

## Bootstrap keys

```properties
application.version=2.0.0
application.use-database-properties=true
database.url=<jdbc-url>
database.user=<user>
database.password={enc}<base64-ciphertext>
```

The writable file path is resolved from `user.dir` as
`src/main/resources/config/application.properties`; this is a source-tree
coupling, not a portable packaged-runtime location.

## Built-in application defaults

| Key | Default |
|---|---|
| `database.driver-class` | `com.microsoft.sqlserver.jdbc.SQLServerDriver` |
| `database.pool.name` | `OpenData` |
| `database.pool.max-total` | `8` |
| `database.pool.max-idle` | `8` |
| `database.pool.min-idle` | `1` |
| `database.pool.max-wait-seconds` | `30` |
| `database.pool.validation-query` | `SELECT 1` |
| `execution.max-parallel-plugins` | `4` |
| `execution.shutdown-timeout-seconds` | `30` |
| `logging.directory` | `logs` |
| `logging.file-limit-bytes` | `10485760` |
| `logging.file-count` | `10` |
| `logging.append` | `true` |

Boolean values accept `true/yes/1/on` and `false/no/0/off`.

## Override scopes

Application overrides use `application.<key>`. Single-plugin files may use
unscoped plugin keys. Multi-plugin files must use
`plugin.<id>.<property-key>`.

## Registration behaviour

`--register` upserts application properties, deletes and replaces each plugin's
property rows, and finally rewrites the local bootstrap file. Those steps are
not wrapped in one cross-resource transaction.
