# Plugin API Reference

**Document ID:** REF-PLUGIN-API-001  
**Version:** 2.0  
**Status:** Version 2.0.0 implementation reference  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## `OpenDataPlugin`

Every executable plugin implements:

```java
PluginMetrics execute(PluginExecutionContext context) throws Exception;
```

The instance is created for one resolved plugin execution and must not expose
shared mutable run state.

## `PluginExecutionContext`

| Component | Meaning |
|---|---|
| `runId` | UUID for this plugin task |
| `descriptor` | Persistent registered plugin descriptor |
| `definition` | Resolved database-backed plugin definition |
| `database` | `DatabaseResourceManager` for write mode, unavailable manager in dry run |
| `clock` | Time source for deterministic date/time logic |
| `dryRun` | Whether persistent side effects must be skipped |

A plugin must not assume that `database` is usable during dry run.

## `PluginMetrics`

`PluginMetrics(read, inserted, updated, skipped)` accepts only non-negative
values. `PluginMetrics.ZERO` represents no processed rows. Metrics are used in
logs and run audit, so they must represent actual outcomes rather than intended
SQL operations.

## Construction

`ReflectionPluginFactory` loads the class named by
`plugin.implementation-class`. It first tries:

```java
public ExamplePlugin(PluginDefinition definition)
```

and falls back to a public no-argument constructor. The created object must
implement `OpenDataPlugin`.

## `PluginDefinition`

A resolved definition exposes identity, dataset ID, endpoints, typed property
metadata and credential references. Useful methods are:

```java
definition.requireEndpoint("source");
definition.findProperty("name");
definition.requireProperty("name");
```

`requireProperty` returns the textual value. The provider's typed configuration
class is responsible for parsing and validating integers, booleans, durations,
paths, URIs and domain-specific values.

## Registry and selection

`ClasspathPluginRegistry` reads the packaged index/definitions as a registration catalogue. `JdbcPluginRegistry` reads `core.plugin_registry` and is authoritative for installed metadata and status. `PluginSelectionResolver` selects enabled persistent descriptors before database-backed definitions are reconstructed.

## Execution boundary

`PluginExecutionCoordinator` schedules selected plugins using bounded
parallelism. `PluginExceptionHandler` adds plugin context and turns failures into
run results. Plugins should allow meaningful failures to propagate rather than
calling `System.exit`.

## Stability

This is an internal framework extension API, not a separately versioned public
binary compatibility guarantee. Breaking contract changes require migration
notes and an appropriate project version increment.
