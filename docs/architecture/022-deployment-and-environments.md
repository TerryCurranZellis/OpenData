# Deployment and Environments

**Document ID:** ARCH-022  
**Version:** 2.0  
**Status:** Local source-tree execution implemented; deployable packaging unresolved  
**Baseline date:** 3 August 2026

---

## Current topology

OpenData is a Java 17 command-line modular monolith. One process loads the persistent SQL Server plugin registry, resolves
classpath registration sources or database-backed runtime configuration,
uses outbound HTTPS or local source files, writes work/archive/log files and
connects to SQL Server through a bounded JDBC pool.

## Environment separation

| Concern | Development | Production expectation |
|---|---|---|
| SQL Server | local instance allowed | managed and network-restricted instance |
| SQL transport | local certificate trust may be tolerated | trusted server certificate and hostname validation |
| bootstrap | writable source-tree resource | external, writable and access-controlled configuration path |
| private key | local development key outside Git | protected service-owned key store or managed secret facility |
| staging/input/archive | developer directories | dedicated restricted directories with retention rules |
| logging | console and local file | retained, monitored and access-controlled logs |
| execution | interactive CLI | scheduler or job runner outside the application |

Internal scheduling remains deferred. External scheduling also requires an
installed launch command and reliable process exit-code mapping.

## Process isolation

Each process owns its own connection pool. Multiple OpenData processes multiply
the possible SQL Server connection count. Combined pool limits must remain within
server capacity, and concurrent writes rely on provider transaction design and
SQL Server locking.

## Filesystem locations

The current implementation resolves bootstrap and certificate files relative to
`user.dir` below `src/main/resources`. Provider defaults are also relative or
machine-specific. That is acceptable only for repository-local development.
A deployable package must externalise bootstrap, key-store, input, working,
archive and log locations.

Octopus statement input and archive directories contain customer billing data
and require stricter access and retention controls than ordinary public-source
work files.

## Shutdown and process status

The application boundary records final status and duration and closes owned
resources. `OpenData` does not currently call `System.exit`, so a non-success
`ExecutionStatus` is not mapped to a non-zero process exit code. Scheduled
operation must not be described as reliable until that boundary is resolved.
