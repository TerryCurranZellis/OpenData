# Plugin Registry Reference

**Document ID:** REF-REGISTRY-001  
**Version:** 3.0.0  
**Status:** OpenData 3.0.0 implementation reference  
**Baseline date:** 15 August 2026  

---

## Two registry roles

OpenData now distinguishes between a packaged plugin **catalogue** and the
persistent runtime **registry**.

| Source | Implementation | Purpose |
|---|---|---|
| `config/plugins/index.properties` and packaged plugin files | `ClasspathPluginRegistry` | Catalogue of definitions available to `--register` when no external file is supplied |
| `core.plugin_registry` | `JdbcPluginRegistry` | System of record for registered plugins and enabled/disabled status |

The classpath catalogue does not by itself make a plugin runnable. A plugin must
be registered in SQL Server.

## Persistent metadata

`core.plugin_registry` stores:

- stable plugin id;
- display name and description;
- implementation class;
- enabled/disabled status;
- configuration version;
- registration and update timestamps.

The complete flattened definition remains in `core.plugin_property`.

## Registration

```text
opendata --plugin all --register
opendata --plugin ofgem --register
opendata --plugin example --register --file C:\OpenData\example.properties
```

Packaged registration loads definitions through
`ClasspathConfigurationPropertiesSource`. External registration uses
`PropertiesFileConfigurationPropertiesSource`. In both cases
`PropertiesPluginDefinitionLoader` validates the file before
`JdbcPluginRegistry` stores metadata and properties.

For an existing registry row, registration refreshes metadata and configuration
but preserves the current enabled/disabled state. A new row uses the
`plugin.enabled` value from the definition.

## Runtime selection

```text
opendata --plugin all
opendata --plugin ofgem --plugin openmeteo
```

- `all` returns every registered enabled plugin in id order.
- A named plugin must exist in `core.plugin_registry` and be enabled.
- A missing plugin produces `Plugin is not registered`.
- A disabled plugin produces `Plugin is registered but disabled`.

## Status management

```text
opendata --plugin octopus --disable
opendata --plugin octopus --enable
opendata --plugin all --disable
opendata --plugin all --enable
```

Enable and disable update `core.plugin_registry.is_enabled`; they do not alter
stored definition properties or provider data.

## Unregistration

```text
opendata --plugin octopus --unregister
opendata --plugin all --remove
```

Unregister deletes the selected rows from both `core.plugin_registry` and
`core.plugin_property` in a transaction for each plugin. It deliberately leaves:

- provider business tables;
- archived source files;
- generic and provider-specific run history.

This prevents an administrative configuration action from destroying imported
data.

## Listing

```text
opendata --list-plugins
```

The output includes id, enabled/disabled status, display name and implementation
class. It reads SQL Server and therefore requires the `003a` registry migration,
permissions and valid bootstrap credentials.

## Database installation

Run `sql/003a-create-plugin-registry.sql` after
`003-create-configuration-store.sql`. Fresh and upgraded installations must then
apply the updated grant scripts.
