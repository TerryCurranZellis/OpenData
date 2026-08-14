# 4. Configuration

**Document ID:** USER-004  
**Version:** 2.1  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 8 August 2026

---

## Configuration sources

OpenData uses:

1. built-in runtime defaults;
2. the repository-local bootstrap file for the SQL Server connection;
3. packaged application/plugin properties during initial registration; and
4. SQL Server application/plugin properties after registration.

The writable bootstrap file is:

```text
src/main/resources/config/application.properties
```

It contains the version marker, database-backed switch, database URL, database
user and encrypted database password. The initial registration requires a valid
plain database password in this file; successful registration encrypts and
rewrites it.

## Register packaged plugins

```text
opendata --plugin all --register
opendata --plugin ofgem --register
opendata --plugin ofgem --plugin openmeteo --register
```

Registration refreshes application properties, inserts or updates selected
plugin metadata in `core.plugin_registry`, replaces each selected plugin's
complete `core.plugin_property` set and enables database-backed configuration.
Re-registering an existing plugin preserves its current enabled/disabled status.


## Register plugins from the graphical interface

In Version 3.1.0, **Register** scans the OpenData plugin configuration folder for
new complete `.properties` definitions. The deployment-style location is
`config/plugins`; a development checkout also scans
`src/main/resources/config/plugins`. Existing registered ids are excluded and
new definitions are shown for confirmation before they are written.

Use **Register from File** when the definition is elsewhere. The JavaFX file
chooser selects one complete `.properties` file and OpenData reads its
`plugin.id` from the file before validation and registration.

## Inspect stored plugin configuration

Use `--detail` to display the configuration currently stored for one registered
plugin:

```text
opendata --plugin ofgem --detail
```

The command displays the plugin id, display name and current enabled/disabled
status, followed by the stored property names and values read from
`core.plugin_property`.

`--detail` requires exactly one named plugin. It cannot be used with `all`,
multiple plugin selections, `--Execute`, `--dry-run`, `--file`, or another
plugin administration operation.

This is useful after registration or re-registration to confirm which
configuration OpenData will read for that plugin.

## Register one plugin from a file

Copy a complete packaged definition, edit it outside the repository, then run:

```text
opendata --plugin openmeteo --register --file C:\OpenData\openmeteo.properties
```

The file must contain the full unprefixed plugin definition, including
`plugin.id`, implementation class, dataset, endpoints and typed properties. Its
`plugin.id` must match the selected command-line id. `--file` cannot be used with
`all`, multiple plugin ids, enable, disable, unregister, detail, or a normal run.

`plugin.enabled` supplies the initial status only when the plugin is first
registered. Use `--enable` or `--disable` for subsequent lifecycle changes.

After registration, the stored values can be checked with:

```text
opendata --plugin openmeteo --detail
```

## Administration

```text
opendata --list-plugins
opendata --plugin octopus --detail
opendata --plugin octopus --disable
opendata --plugin octopus --enable
opendata --plugin octopus --unregister
```

Unregistering removes both metadata and stored plugin properties. Provider data
and historical audit rows are not deleted.

## Security warning

The uploaded baseline contains development credential/private-key material.
Remove it from source control, replace the certificate pair and rotate any
exposed database password before release or production use.

Plugin configuration should contain secret references rather than actual secret
values. Because `--detail` writes stored plugin configuration to standard
output, do not redirect or share its output where configured values should not
be disclosed.

---
