# Configuration Architecture

**Document ID:** ARCH-011  
**Version:** 3.0.0  
**Status:** Persistent registry and database-backed registration implemented  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Bootstrap

`src/main/resources/config/application.properties` is read before database
lookup and contains version, database-backed switch, JDBC URL, user and plain
(first registration) or `{enc}` encrypted password.

## Packaged catalogue

`config/plugins/index.properties` and `config/plugins/<id>.properties` describe
plugins available for registration. They are not the authoritative installed
state.

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
