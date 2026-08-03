# Database Configuration Reference

**Document ID:** REF-DB-CONFIG-001  
**Version:** 2.1  
**Status:** OpenData 2.0.0 implementation reference  
**Baseline date:** 3 August 2026

---

## Bootstrap connection

The minimal local bootstrap supplies the SQL Server URL, user and encrypted
password needed to reach the configuration store and persistent plugin registry.
The database password cannot be supplied through the plugin `--file` option.

## Configuration and registry tables

| Table | Purpose |
|---|---|
| `core.application_property` | Runtime application settings and encrypted-value marker |
| `core.plugin_registry` | Registered plugin metadata and enabled status |
| `core.plugin_property` | Flattened complete definition for each registered plugin |

Install `sql/003a-create-plugin-registry.sql` after the existing configuration
store migration. Apply the updated application permission scripts so the
application role can select, insert, update and delete registry rows.

## Registration behaviour

```text
opendata --plugin all --register
```

Registration:

1. connects with bootstrap database settings;
2. refreshes application properties;
3. encrypts the database password before database storage;
4. validates each selected definition;
5. upserts registry metadata;
6. atomically replaces each selected plugin's property rows; and
7. rewrites the bootstrap file with database-backed configuration enabled.

An external plugin definition is supplied only as:

```text
opendata --plugin example --register --file C:\OpenData\example.properties
```

## Runtime database use

A dry-run still reads configuration and registry tables before plugin execution.
The plugin execution context then receives an unavailable database resource and
no-op run audit. A write run reinitialises the pool from resolved runtime database
settings.

## Administration SQL effects

| Command | Registry effect | Property effect | Provider data effect |
|---|---|---|---|
| `--register` | Insert/update metadata | Replace selected rows | None |
| `--enable` | Set `is_enabled=1` | None | None |
| `--disable` | Set `is_enabled=0` | None | None |
| `--unregister` | Delete metadata | Delete selected rows | None |
| `--list-plugins` | Read | None | None |

Use `sql/010-verification-queries.sql` to inspect the resulting registry and
configuration rows.
