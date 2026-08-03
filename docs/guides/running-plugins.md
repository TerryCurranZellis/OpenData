# Running plugins

**Document ID:** GUIDE-PLUGIN-RUN-001
**Version:** 2.0
**Status:** Current with Octopus dry-run limitation
**Baseline date:** 3 August 2026
**Minimum Java version:** 17

---

## Commands

```text
opendata --list-plugins
opendata --plugin openmeteo
opendata --plugin openmeteo --plugin ofgem
opendata --plugin openmeteo,ofgem --parallelism 2
opendata --plugin all
opendata --plugin openmeteo,ofgem --dry-run --parallelism 2
```

`--parallelism` accepts 1 to 64. The actual worker count never exceeds the
number of selected plugins.

## Override files

A single-plugin run accepts unprefixed plugin keys and application values remain
under `application.` in an external override file:

```properties
application.database.password=...
property.start-date.value=2024-01-01
```

A multi-plugin or `all` run requires scoped plugin keys:

```properties
application.database.password=...
application.execution.max-parallel-plugins=4
plugin.openmeteo.property.start-date.value=2024-01-01
plugin.ofgem.property.download.request-timeout.value=PT180S
```

The override loader strips `application.` before applying application values, so
`application.database.password` maps to the runtime key `database.password`.
Unscoped plugin entries are rejected in a multi-plugin run to prevent a property
intended for one plugin being applied to every plugin.

A database-writing run rejects a blank database password. In database-backed
configuration mode, startup must still decrypt the bootstrap password and query
SQL Server before plugin execution, even when the selected plugins later run in
dry-run mode.

## Dry run

Ofgem and OpenMeteo dry runs perform acquisition, parsing and validation without
creating plugin audit rows or writing plugin tables. The plugin execution layer
uses an unavailable database resource instead of the normal pool.

The current Octopus extract stage nevertheless queries
`octopus.statement_file` to obtain completed filename/hash keys. Consequently:

```text
opendata --plugin octopus --dry-run
opendata --plugin all --dry-run
```

are not valid acceptance commands in this baseline. Both fail when Octopus tries
to use the unavailable dry-run database resource. This requires a Java fix.

## Outcome

The application does not call `System.exit`. The `finally` block logs an
operator-facing `ExecutionStatus.displayName()` and elapsed milliseconds. An
individual plugin failure does not stop another selected plugin, but the
aggregate status becomes `PLUGIN_FAILURE`.
