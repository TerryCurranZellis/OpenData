# 4. Configuration

**Document ID:** USER-004  
**Checked-in source:** OpenData Maven 4.2.0  
**First-run feature baseline:** 4.0.0

## Configuration sources

OpenData reads writable deployment bootstrap properties from `config/application.properties` (relative to the working directory) or the packaged first-run defaults. When `init=true`, the JavaFX wizard runs **before** CLI or plugin processing. On successful setup, the wizard writes `init=false`, enables database-backed configuration and retains the SQL application password encrypted using AES-256-GCM.

The bootstrap stores database connection values and the external key **file path** under `security.aes-key-file`. No JVM option or environment variable is required. After initialisation, application settings, plugin definitions and enabled status are stored in SQL Server:

| SQL Server table | Meaning |
|---|---|
| `core.application_property` | Application settings and encrypted secret values |
| `core.plugin_registry` | Plugin metadata and authoritative enabled status |
| `core.plugin_property` | Registered plugin configuration properties |

Use the normal properties loader; never paste the AES key itself into `application.properties`. Protect both the file and the key path with appropriate filesystem permissions.

## Register and inspect providers

The first-run wizard discovers packaged or external plugin definitions and registers them disabled. From the GUI, **Register** scans the normal configuration folders and **Register from File** opens a JavaFX file chooser for a specific `.properties` definition.

For an existing initialised installation, the CLI supports explicit registration:

```text
opendata --plugin all --register
opendata --plugin ofgem --register
opendata --plugin ofgem --detail
opendata --plugin openmeteo --register --file C:\OpenData\openmeteo.properties
```

The external definition must include a matching `plugin.id` and a loadable implementation class. Re-registration updates stored definitions without forcing an existing registered plugin to become enabled.

## Manage plugin state

```text
opendata --list-plugins
opendata --plugin octopus --enable
opendata --plugin octopus --disable
opendata --plugin octopus --detail
opendata --plugin octopus --unregister
```

After registration, `core.plugin_registry.is_enabled` is authoritative; changing `plugin.enabled` in a file alone does not change existing registry status. Unregistering removes the plugin registry entry and its stored properties, not imported provider data.

## Security and migrations

Do not expose the database password, AES key, SQL administrator credential, API tokens or customer billing data in source control or screenshots. The historical `{enc}`/RSA ciphertext format is not supported by the AES reader. Follow [Database access security](../guides/database-configuration-and-security.md) and [First-run migration guidance](../guides/first-run-initialisation.md) for upgrades; do not reset `init=true` on a functioning production database.

See the [full configuration reference](../reference/configuration-reference.md) and [plugin properties reference](../reference/plugin-properties-reference.md).
