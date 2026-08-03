# 4. Configuration

**Document ID:** USER-004  
**Version:** 2.0  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 3 August 2026

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

## Register one plugin from a file

Copy a complete packaged definition, edit it outside the repository, then run:

```text
opendata --plugin openmeteo --register --file C:\OpenData\openmeteo.properties
```

The file must contain the full unprefixed plugin definition, including
`plugin.id`, implementation class, dataset, endpoints and typed properties. Its
`plugin.id` must match the selected command-line id. `--file` cannot be used with
`all`, multiple plugin ids, enable, disable, unregister, or a normal run.

`plugin.enabled` supplies the initial status only when the plugin is first
registered. Use `--enable` or `--disable` for subsequent lifecycle changes.

## Administration

```text
opendata --list-plugins
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
