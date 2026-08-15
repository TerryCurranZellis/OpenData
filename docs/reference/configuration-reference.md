# Configuration Reference

**Document ID:** REF-CONFIG-001  
**Version:** 3.0.0  
**Status:** OpenData 3.0.0 implementation reference  
**Baseline date:** 15 August 2026  

---

## Configuration sources

| Source | Purpose |
|---|---|
| Writable/classpath `config/application.properties` | Minimal database bootstrap and first-registration application defaults |
| Classpath `config/plugins/index.properties` | Packaged plugin catalogue used by `--plugin all --register` |
| Classpath `config/plugins/<id>.properties` | Packaged definition used when registering a named plugin without `--file` |
| External `--file <plugin.properties>` | Complete definition for one named plugin during CLI registration only |
| GUI `config/plugins/*.properties` | Filesystem definitions discovered by the JavaFX **Register** command |
| GUI **Register from File** selection | Complete definition selected from any accessible filesystem location |
| `core.application_property` | Runtime application settings after registration |
| `core.plugin_registry` | Registered plugin metadata and enabled/disabled status |
| `core.plugin_property` | Complete registered plugin definitions |

`--file` is no longer an invocation override. It is reserved for registering one
plugin and must contain the same unprefixed keys as a packaged plugin definition.

## Bootstrap configuration

The writable bootstrap contains:

```properties
application.version=2.0.0
application.use-database-properties=true
database.url=jdbc:sqlserver://...
database.user=OpenData
database.password={enc}...
```

Help and About do not need SQL Server. Plugin listing, administration and
execution require bootstrap database access because the persistent registry is
the system of record.

## Application configuration

After registration, `JdbcConfigurationPropertiesSource` loads
`core.application_property`. The registration command refreshes application
values and marks the encrypted database-password row with `is_encrypted=1`.

## Plugin registration sources

### Packaged definition

```text
opendata --plugin ofgem --register
opendata --plugin all --register
```

The classpath catalogue determines which packaged definitions are available.

### External definition

```text
opendata --plugin example --register --file C:\OpenData\example.properties
```

The file must include at least:

```properties
plugin.id=example
plugin.display-name=Example Plugin
plugin.implementation-class=com.example.ExamplePlugin
plugin.enabled=true
plugin.configuration-version=1
dataset.id=example-data
```

It must also define at least one endpoint or typed plugin property. The id must
match the command line, and the implementation class must be loadable and
implement `OpenDataPlugin`.


### JavaFX configuration-folder registration

The JavaFX **Register** command scans `<working directory>/config/plugins` and
then the development fallback `<working directory>/src/main/resources/config/plugins`.
It validates complete `*.properties` definitions, ignores `index.properties`,
and offers only plugin ids not already present in `core.plugin_registry`.

The JavaFX **Register from File** command uses a file chooser and reads the id
from the selected file's `plugin.id`; unlike normal GUI Register, it may be used
to replace the stored definition of an already registered plugin.

## Registered status versus definition value

`plugin.enabled` supplies the initial status when a plugin is first registered.
Thereafter `core.plugin_registry.is_enabled` is authoritative. Re-registration
preserves the existing status; use `--enable` or `--disable` to change it.

## Removal

`--unregister` removes the registry row and every `core.plugin_property` row for
the plugin. It does not remove application configuration or provider data.

## Key normalisation

Property keys and plugin ids are trimmed and lower-cased for lookup. Values are
trimmed. Plugin ids must satisfy the framework id validation and SQL check
constraint.

See [Plugin Properties Reference](plugin-properties-reference.md) and
[Plugin Registry Reference](plugin-registry-reference.md).
