# Running plugins

## Commands

```text
opendata --list-plugins
opendata --plugin openmeteo
opendata --plugin openmeteo --parallelism 1
# Multi-plugin syntax becomes usable when two or more executable plugin classes are installed.
opendata --plugin all
opendata --plugin all --dry-run
```

`--parallelism` accepts 1 to 64. The actual worker count never exceeds the number of selected plugins.

## Override files

A single-plugin run accepts unprefixed plugin keys:

```properties
application.database.password=...
property.start-date.value=2024-01-01
```

A multi-plugin or `all` run requires scoped keys:

```properties
application.database.password=...
application.execution.max-parallel-plugins=4
plugin.openmeteo.property.start-date.value=2024-01-01
plugin.ofgem.property.download.request-timeout.value=PT180S
```

Unscoped plugin entries are rejected in a multi-plugin run to prevent a property intended for one plugin being applied to every plugin. A database-writing run also rejects a blank `application.database.password`; `--dry-run` may omit it because the pool is not initialised.

## Dry run

`--dry-run` allows API and parsing validation but does not initialise the database pool, create `core.PluginRun` rows or write plugin tables.

## Outcome

The application does not call `System.exit`. The `finally` block logs an `ExecutionStatus` enum name and elapsed milliseconds. An individual plugin failure does not stop another selected plugin, but the aggregate status becomes `PLUGIN_FAILURE`.

## Current plugin availability

`openmeteo` is executable in this source baseline. The registry also contains an `ofgem` descriptor, but its configured `com.towermarsh.opendata.plugin.ofgem.OfgemPlugin` class is absent; selecting it will fail during reflective plugin creation until that implementation is added.
