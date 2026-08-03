# Logging and Connection-Pool Operations

**Document ID:** OPS-LOG-POOL-001  
**Version:** 2.0  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 3 August 2026

---

## Logging

OpenData uses `java.util.logging`. Startup first configures the default `logs`
directory and then reconfigures handlers after runtime properties are loaded.
The final runtime defaults are:

```properties
logging.directory=logs
logging.file-limit-bytes=10485760
logging.file-count=10
logging.append=true
```

Each configuration creates one console handler and one rotating file handler
using pattern `opendata-%g.log`. `--verbose` changes the root level from `INFO` to
`FINE` after runtime configuration is loaded.

Concurrent plugin records include thread, plugin and run UUID. Passwords,
private-key material, complete override maps and unredacted Octopus statement
text must never be logged.

## Connection pool

The application uses a singleton Apache Commons DBCP pool. Each plugin repository
borrows its own JDBC connection; connections are not shared between plugin
threads. Closing a connection returns it to the pool. Closing the database
resource closes the registered pool and clears the singleton.

Built-in defaults:

```properties
database.pool.name=OpenData
database.pool.max-total=8
database.pool.max-idle=8
database.pool.min-idle=1
database.pool.max-wait-seconds=30
database.pool.validation-query=SELECT 1
```

The pool tests connections on borrow and while idle, blocks when exhausted and
uses the configured maximum wait.

Monitor active/idle counts, SQL waits and task duration before increasing
parallelism. Pool exhaustion usually indicates long transactions, unclosed
resources, SQL blocking, server unavailability or excessive concurrency.

For production, use a certificate trusted by the JVM and set
`trustServerCertificate=false`.
