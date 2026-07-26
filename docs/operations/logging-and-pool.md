# Logging and Connection-Pool Operations

**Document ID:** OPS-LOG-POOL-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026

## Logging

The framework uses only `java.util.logging`. One console handler and one rotating file handler are shared by all workers. Every formatted record contains:

```text
2026-07-24T19:30:00+01:00 [INFO] [thread=opendata-plugin-1] [plugin=openmeteo] [run=<uuid>] logger - message
```

`PluginLogContext` is a `ThreadLocal` scope opened and closed by the coordinator. It must never be opened by a plugin without try-with-resources because pooled worker threads are reused.

Useful settings:

```properties
logging.directory=logs
logging.file-limit-bytes=10485760
logging.file-count=10
logging.append=true
```

`--verbose` changes the root level to `FINE`. Passwords and complete override maps must not be logged.

## Pool

The supplied pool uses the existing singleton pattern:

```java
SQLServerResource.initialise(configuration);
SQLServerResource.getInstance().getConnection();
```

Closing a pooled connection returns it to the pool. Closing the singleton shuts down the registered DBCP pool and clears the singleton reference.

The supplied JDBC URL trusts the server certificate for local development. Use a certificate trusted by the JVM and set `trustServerCertificate=false` for a production deployment.

Default settings:

```properties
database.pool.max-total=8
database.pool.max-idle=8
database.pool.min-idle=1
database.pool.max-wait-seconds=30
database.pool.validation-query=SELECT 1
```

Monitor active/idle counts and SQL Server waits before increasing concurrency. A pool-exhaustion wait normally indicates that transactions are too long, connections are not being closed, or parallelism is too high.
