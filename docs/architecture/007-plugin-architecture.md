# Plugin Architecture

**Document ID:** ARCH-007  
**Version:** 2.0  
**Status:** Implemented  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## Contract

A plugin represents one dataset family and implements
`OpenDataPlugin.execute(PluginExecutionContext)`. A `PluginDescriptor` supplies
identity, display name, implementation class and enabled state. A resolved
`PluginDefinition` supplies endpoint and typed property values. The execution
context supplies run identity, database access, clock and dry-run state.

## Registry and definitions

Installed plugin ids are listed explicitly in
`config/plugins/index.properties`. `ClasspathPluginRegistry` uses this index to
create descriptors; it does not scan the classpath or query the database for
installed implementation classes.

Plugin property values are loaded by `PropertiesPluginDefinitionLoader` through
a `ConfigurationPropertiesSource`:

- `ClasspathConfigurationPropertiesSource` reads packaged properties before
  registration or when database-backed mode is disabled;
- `JdbcConfigurationPropertiesSource` reads `core.plugin_property` after
  registration;
- invocation overrides are applied last.

This separates implementation registration from configuration storage. Moving
property values to SQL Server does not make the plugin registry dynamic.

## Construction and execution

`ReflectionPluginFactory` creates a fresh plugin for each selected task. It uses
a constructor accepting `PluginDefinition` when available and otherwise a
no-argument constructor. `PluginExecutionCoordinator` gives each instance its
own context and runs selected plugins in a bounded executor.

## Rules

- ids are lowercase, stable and unique;
- index id, plugin definition id and implementation id must agree;
- disabled plugins may be listed but cannot run;
- plugin instances are task-confined and must not share JDBC connections;
- plugins are required to honour `context.dryRun()` and return
  `PluginMetrics`; the current Octopus extract stage violates the no-database
  dry-run rule and is a recorded implementation defect;
- plugins do not implement their own CLI or read global bootstrap files;
- provider SQL and source parsing remain inside the provider package;
- finalisation must distinguish dry run, successful completion and failure.

Ofgem is the HTML-to-XLSX reference, OpenMeteo is the parameterised JSON API
reference, and Octopus is the local PDF and idempotent file-ledger reference.

The maintained [example plugin](../examples/example-plugin/README.md) is the
starting point for new implementations.

![Plugin registry](../diagrams/generated/plugin-registry.svg){width=16cm}
