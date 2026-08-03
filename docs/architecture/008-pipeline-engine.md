# Pipeline Engine

**Document ID:** ARCH-008  
**Version:** 2.0  
**Status:** Implemented coordinator and provider-owned pipelines  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## Application sequence

The application-level sequence is:

1. parse arguments and execute immediate control commands;
2. load an optional override file;
3. load and decrypt the bootstrap database password;
4. select the classpath or SQL Server property source;
5. load runtime settings, resolve enabled plugins and validate definitions;
6. initialise execution database access or switch to an unavailable resource for
   plugin dry-run execution;
7. submit one task per plugin to a bounded executor;
8. create audit rows for write runs, execute plugins and aggregate metrics;
9. close the executor, database resource and logging system.

When database-backed configuration is enabled, step 4 opens SQL Server even for
a dry run. The connection is closed before plugin execution and the coordinator
then receives `UnavailableDatabaseResourceManager` with `NoOpPluginRunAudit`.
This prevents plugin persistence but does not make configuration loading
independent of the database. It also exposes the current Octopus dry-run defect:
that extractor reads the processed-file ledger before its load-stage dry-run
branch and therefore fails against the unavailable database resource.

## Provider sequence

All three current plugins use an explicit provider-owned sequence for normal
write execution:

1. initialise typed configuration and stage objects;
2. extract or acquire source data;
3. transform and validate typed records;
4. load transactionally, or calculate read/skipped metrics in dry-run mode;
5. finalise by reporting metrics and, where configured, archiving successful
   source files.

Ofgem and OpenMeteo archive or clean up provider work as part of finalisation.
Octopus archives successfully loaded PDF statements only after the database
transaction completes. Its load and finalise stages avoid writes during dry run,
but its extract-stage processed-ledger lookup currently prevents an end-to-end
Octopus dry run. A plugin failure stops that task but does not cancel other
selected plugins automatically.

`ExtractService`, `TransformService` and `LoadService` remain reusable contracts.
There is no generic `PipelineEngine` composing them in the active runtime.

::: {.landscape}
![Plugin pipeline sequence](../diagrams/generated/pipeline-sequence.svg){width=22.5cm}
:::
