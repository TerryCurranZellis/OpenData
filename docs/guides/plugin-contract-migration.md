# Migrating a Plugin to the Version 2.0.0 Contract

**Document ID:** GUIDE-PLUGIN-MIGRATION-001  
**Version:** 3.0.0  
**Status:** Current migration guide  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Required contract

Every executable plugin implements:

```java
PluginMetrics execute(PluginExecutionContext context) throws Exception;
```

`ReflectionPluginFactory` constructs the implementation class using a public
`PluginDefinition` constructor when available, otherwise a public no-argument
constructor.

## Migration steps

1. Move provider code below
   `com.towermarsh.opendata.plugin.<plugin-id>`.
2. Create a thin root `OpenDataPlugin` facade.
3. Move typed configuration and orchestration into `initialise`.
4. Separate source acquisition/decoding, transformation/validation, load and
   finalise responsibilities.
5. Replace shared mutable run state with method-local or execution-confined
   state.
6. Receive database, run ID, clock and dry-run state from
   `PluginExecutionContext`.
7. Borrow a JDBC connection inside load/repository work; never retain it.
8. Return accurate `PluginMetrics`.
9. Add classpath properties and registry index entry.
10. Register the definition into SQL Server and test ordinary runtime loading.

## Dry-run migration

A dry run must complete extract, transform and validation while skipping load,
archive movement and all persistent side effects. Do not read a database ledger
through `context.database()` in dry run unless the framework contract has first
been changed to provide a supported read-only resource.

## Concurrency

Do not add JVM-wide synchronisation merely because plugins run in parallel.
Use database uniqueness, isolation and a narrow transaction-owned application
lock when two tasks can modify the same logical dataset. Restore the interrupt
flag after `InterruptedException`.

## Compatibility cleanup

Remove obsolete download-only entry points once no tests or callers require
them. Remove duplicate provider `config` or `download` packages after the active
five-stage implementation and tests have been confirmed.
