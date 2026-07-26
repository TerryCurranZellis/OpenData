# Deployment and Environments

**Document ID:** ARCH-022  
**Version:** 1.1  
**Status:** Local classpath execution implemented; deployment packaging unresolved  
**Baseline date:** 26 July 2026

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

Internal scheduling remains deferred. An external scheduler is the target, but
production scheduling must wait for executable packaging and reliable process
exit-code mapping.

## Process isolation

Each process owns its own connection pool. Running multiple OpenData processes
therefore multiplies the possible SQL Server connection count. The combined
`max-total` values must remain within server capacity.

## Filesystem locations

Configuration, staging, archive, reject and log directories must be writable
only by the execution identity. Current defaults are relative paths, so an
interactive or scheduled run must set a known repository working directory or
override every relevant location with an absolute path.

## Shutdown

The application boundary records the final status and duration and closes owned
resources. `Main` does not currently map that status to a non-zero process exit
code; ownership of exit-code mapping is unresolved.
