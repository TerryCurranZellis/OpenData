# Component Interactions

**Document ID:** ARCH-010  
**Version:** 3.0.0  
**Status:** Version 3.0.0 implementation baseline  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Graphical interface

The JavaFX main window loads registry/run-audit data through GUI-specific gateway
and loader classes rather than issuing SQL from the controller. Register,
Register from File, Enable, Disable, Unregister, Plugin Detail and Settings use
background JavaFX tasks and existing OpenData registry/configuration services.

GUI Execute and Dry-run snapshot checked plugin ids, obtain confirmation, then
call `PluginExecutionGateway`, which delegates to the same selection and
execution coordinator used by the CLI. `JavaFxLogHandler` streams scoped JUL
output to the modal execution window while normal file/console logging remains
active. The main table is refreshed after completion.

## Informational commands

`--help` and `--about` do not initialise SQL Server. `--list-plugins` reads
`core.plugin_registry` and therefore requires a valid bootstrap connection and
the registry migration.

## Administration

`--register`, `--unregister`, `--enable` and `--disable` all require plugin
selection. `all` expands against the packaged catalogue for registration and the
persistent registry for the other administration operations.

Registration stores application configuration, plugin metadata and complete
plugin properties, then rewrites the bootstrap with an encrypted password and
database-properties mode enabled. Unregister deletes registry metadata and
configuration but not provider data or historical audit.

## Execution

The application opens SQL Server to resolve the persistent registry and active
configuration, selects only enabled registered plugins and builds validated
`PluginDefinition` values. The coordinator executes fresh plugin instances with
bounded concurrency.

Write runs use the DBCP pool and `JdbcPluginRunAudit`. Dry runs use an unavailable
plugin data resource and no-op audit after registry/configuration reads. Ofgem,
OpenMeteo and Octopus respect that boundary; Octopus skips its completion-ledger
query in dry-run mode.

::: {.landscape}
![Plugin execution sequence](../diagrams/generated/plugin-execution-sequence.svg){width=22.5cm}

![Configuration loading sequence](../diagrams/generated/configuration-loading-sequence.svg){width=22.5cm}

![Configuration registration sequence](../diagrams/generated/configuration-registration-sequence.svg){width=22.5cm}
:::
