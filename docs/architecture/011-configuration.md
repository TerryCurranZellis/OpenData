# Configuration Architecture

> **OpenData 4.0.0:** This document contains legacy version 3 examples.
> For the current first-run workflow, read
> [First-run configuration wizard](../guides/first-run-initialisation.md)
> and [First-run architecture](../architecture/030-first-run-initialisation.md).
> With `init=true`, the GUI wizard runs before CLI commands.


**Document ID:** ARCH-011  
**Version:** 4.0.0  
**Status:** Current 4.0.0 baseline; 4.1.0 refactoring review  
**Baseline date:** 10 November 2026  
**Minimum Java version:** 24

---

## Bootstrap

`config/application.properties` is read before database access, including
its `init` switch. It holds the SQL Server JDBC URL and login, a `{aes}`
encrypted database password after setup, the runtime property-source setting,
and the external AES key path `security.aes-key-file`. These settings are
loaded by `ApplicationBootstrapPropertiesLoader`; no environment or JVM property
is required for the key location. Legacy `{enc}` values are not the default for
new Version 4.0.0 installations.

## First-run setup

With `init=true`, the JavaFX wizard validates SQL Server and application
credentials, selects or creates the AES key, provisions core and plugin tables,
registers discovered plugins disabled and finally writes `init=false`.
Administrator credentials are temporary and must not be retained.
See the [initialisation guide](../guides/first-run-initialisation.md).

## Packaged catalogue

`config/plugins/index.properties` and `config/plugins/<id>.properties` describe
plugins available for registration. In the source tree they are packaged from
the corresponding provider project under
`opendata-plugins/opendata-plugin-<id>/src/main/resources/config/plugins`.
Every provider project contributes an index containing its own plugin id.
They are not the authoritative installed state.

## Persistent registry and configuration

- `core.plugin_registry`: id, display metadata, implementation class,
  configuration version and enabled status.
- `core.plugin_property`: complete flattened definition for each registered id.
- `core.application_property`: active runtime/application configuration.

Normal execution uses `JdbcPluginRegistry` for selection. Named disabled plugins
are rejected; `all` includes only enabled rows.

## Registration sources

```text
--plugin all --register
--plugin ofgem --plugin openmeteo --register
--plugin example --register --file C:\OpenData\example.properties
```

Without `--file`, selected definitions come from the packaged catalogue. With
`--file`, exactly one named complete definition comes from an external UTF-8
properties file. The external `plugin.id` must match the selected id. `--file`
is not an invocation override.

Re-registration replaces stored properties and metadata but preserves the
existing enabled/disabled status. A first registration uses `plugin.enabled`.

## Failure boundary

Application-property writes, per-plugin registry transactions and bootstrap-file
rewrite are not one distributed transaction. Operators must inspect both SQL
Server and the bootstrap file after interrupted registration.
