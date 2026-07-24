# Deployment and Environments

**Document ID:** ARCH-022  
**Version:** 1.0  
**Status:** Implemented for local command-line deployment  
**Baseline date:** 24 July 2026

---

## Current topology

OpenData is a Java 17 command-line modular monolith. One process loads framework
and plugin configuration, uses outbound HTTPS to obtain public datasets, stores
staged files locally and connects to SQL Server through a bounded JDBC pool.

## Environment separation

| Concern | Development | Production expectation |
|---|---|---|
| SQL Server | local instance allowed | managed/restricted instance |
| certificate | trust local certificate allowed | trusted chain required |
| credentials | protected local file acceptable | secret provider required |
| staging | developer directory | dedicated restricted directory |
| logging | console/file | retained and monitored logs |
| execution | interactive CLI | scheduler or job runner outside application |

Internal scheduling remains deferred. Windows Task Scheduler, SQL Agent, a CI
runner or another external orchestrator can invoke the CLI without adding a
scheduler dependency to the application.

## Process isolation

Each process owns its own connection pool. Running multiple OpenData processes
therefore multiplies the possible SQL Server connection count. The combined
`max-total` values must remain within server capacity.

## Filesystem locations

Configuration, staging, archive, reject and log directories must be explicit and
writable only by the execution identity. The application must not rely on the
working directory when executed by a scheduler.

## Shutdown

The application boundary records the final status and duration in `finally`, then
closes the database manager and other owned resources. A non-zero process exit
code can still be returned after cleanup; cleanup must not overwrite the original
failure classification.
