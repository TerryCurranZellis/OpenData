# Command-Line Reference

**Document ID:** REF-CLI-001  
**Version:** 3.0.0  
**Status:** OpenData 3.0.0 implementation reference  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Syntax

```text
opendata --plugin <id|all> [--plugin <id>] --execute [run options]
opendata --plugin <id|all> [--plugin <id>] --dry-run [run options]
opendata --plugin <id> --detail
opendata --plugin <id|all> [--plugin <id>] <administration operation> [options]
opendata --help | --about | --list-plugins
```

Plugin execution is deliberately explicit. Selecting a plugin does **not** run it by itself. Normal write-mode execution requires `--execute` or `-x`; non-writing execution uses `--dry-run` or `-n` as the execution authorisation.

Named `--plugin` options may be repeated and comma-separated ids remain supported
for execution and administration. `--detail` is different: it requires exactly
one named plugin because it displays the stored configuration for one plugin.

## Options

| Short | Long | Purpose |
|---|---|---|
| `-p` | `--plugin <id\|all>` | Select one plugin, repeated plugin ids, comma-separated ids, or `all` |
| `-x` | `--execute` | Explicitly authorise normal write-mode plugin execution |
| — | `--detail` | Display the stored configuration for exactly one named registered plugin |
| `-r` | `--register` | Register or replace selected plugin definitions and configuration |
| `-u` | `--unregister` | Remove selected plugins and their stored plugin configuration |
| — | `--remove` | Alias for `--unregister` |
| `-e` | `--enable` | Enable selected registered plugins |
| `-d` | `--disable` | Disable selected registered plugins |
| `-f` | `--file <plugin.properties>` | External plugin definition used only with registration of one named plugin |
| `-j` | `--parallelism <1-64>` | Maximum concurrent plugin tasks; effective only for runs and dry-runs |
| `-n` | `--dry-run` | Explicitly authorise non-writing execution without plugin data writes or generic run-audit rows |
| `-v` | `--verbose` | Enable `FINE` `java.util.logging` output |
| `-h` | `--help` | Display command help |
| `-a` | `--about` | Display the graphical About/version window |
| `-l` | `--list-plugins` | List registered plugins and enabled/disabled status |

### Execution gate

`--execute` is an execution-authorisation switch, not an administration or
information operation. It has no value argument.

The following command is invalid because selecting a plugin no longer implicitly
runs it:

```text
opendata --plugin ofgem
```

Use:

```text
opendata --plugin ofgem --execute
```

or:

```text
opendata --plugin ofgem -x
```

Dry-run uses its own execution gate:

```text
opendata --plugin ofgem --dry-run
```

`--execute` cannot be combined with `--detail`, `--register`,
`--unregister`/`--remove`, `--enable`, or `--disable`.

### Plugin configuration detail

Use `--detail` to display the configuration currently stored for one registered
plugin:

```text
opendata --plugin ofgem --detail
```

The command displays the plugin id, display name and enabled/disabled status,
followed by the stored configuration properties read from
`core.plugin_property`.

`--detail`:

- requires exactly one named plugin;
- does not use or require `--execute`;
- cannot be used with `--plugin all`;
- cannot be used with repeated or comma-separated plugin selections;
- cannot be combined with `--dry-run`, `--file`, or a plugin administration
  operation; and
- fails if the named plugin is not registered or has no stored configuration.

The output is written to standard output rather than formatted as normal log
records so that the property names and values are easy to inspect.

### Short-option collision resolution

The requested specification assigned `-d` to both `--disable` and `--dry-run`.
A command-line token cannot have two meanings, so OpenData assigns:

- `-d` to `--disable`; and
- `-n` to `--dry-run`.

The long option `--dry-run` remains unchanged. `--detail` intentionally has no
short form.

## Selection rules

- Every normal run requires `--plugin` plus `--execute`; every dry-run requires `--plugin` plus `--dry-run`.
- `--detail` requires exactly one named `--plugin` and does not use `--execute`.
- Every register, unregister, enable or disable request requires `--plugin`, but
  does not use `--execute`.
- `--plugin all` cannot be combined with a named plugin id.
- Repeating the same plugin id is rejected.
- `--plugin all` during execution selects all **registered and enabled** plugins.
- A named run fails when the plugin is not registered or is disabled.
- `--plugin` may be repeated for execution and administration, for example:

```text
opendata --plugin ofgem --plugin openmeteo --plugin octopus --execute
```

## Operation rules

Exactly one non-run plugin operation may be present. `--detail`, `--register`,
`--unregister`/`--remove`, `--enable` and `--disable` are mutually exclusive.

| Operation | Named plugins | `all` | `--execute` | `--file` | `--dry-run` |
|---|---:|---:|---:|---:|---:|
| Normal run | Yes | Yes | Required | No | No |
| Dry-run | Yes | Yes | No | No | Required |
| Detail | Exactly one | No | No | No | No |
| Register | Yes | Yes | No | Optional for exactly one named plugin | No |
| Unregister/remove | Yes | Yes | No | No | No |
| Enable | Yes | Yes | No | No | No |
| Disable | Yes | Yes | No | No | No |

`--parallelism` affects normal and dry-run execution. It has no effect on detail or administration operations.

## Registration

Register every packaged plugin:

```text
opendata --plugin all --register
```

Register selected packaged definitions:

```text
opendata --plugin ofgem --plugin openmeteo --register
```

Register one plugin from an external UTF-8 Java properties file:

```text
opendata --plugin example --register --file C:\OpenData\example.properties
```

The external file uses the same unprefixed format as
`src/main/resources/config/plugins/<id>.properties`. Its `plugin.id` must match
the command-line id, the implementation class must be present on the runtime
classpath, and the class must implement `OpenDataPlugin`.

Registration writes metadata to `core.plugin_registry`, replaces the selected
plugin's rows in `core.plugin_property`, refreshes application configuration,
and rewrites the bootstrap file for database-backed configuration. Re-registering
an existing plugin updates metadata and properties but preserves its current
enabled/disabled registry status.

After registration, use `--detail` to inspect the stored configuration:

```text
opendata --plugin example --detail
```

## Enable, disable and unregister

```text
opendata --plugin octopus --disable
opendata --plugin octopus --enable
opendata --plugin octopus --unregister
opendata --plugin all --disable
opendata --plugin all --enable
opendata --plugin all --remove
```

Enable and disable update both `core.plugin_registry.is_enabled` and the stored
`plugin.enabled` property so registry metadata and reconstructed definitions stay
consistent. Unregistering deletes both the registry row and the plugin's stored
property rows. It does not delete provider business data or historical run-audit
rows.

## Execution and dry-run

```text
opendata --plugin ofgem --execute
opendata --plugin openmeteo --plugin octopus --execute --parallelism 2
opendata --plugin all --dry-run
opendata --plugin ofgem,openmeteo --dry-run --parallelism 2
```

Dry-run still opens SQL Server long enough to read the persistent plugin
registry and database-backed configuration. During plugin execution it supplies
an unavailable database resource and a no-op audit implementation, so plugins
cannot persist provider data. Octopus dry-run deliberately skips its processed
file ledger and validates every matching input statement without archiving it.

## Informational commands

`--help`, `--about` and `--list-plugins` are mutually exclusive and do not
require `--plugin` or `--execute`. They cannot be combined with plugin selection
or operational options. `--list-plugins` reads `core.plugin_registry`, so SQL
Server bootstrap credentials and the plugin-registry migration must be available.

`--detail` is plugin-specific information rather than a global informational
command, so it requires `--plugin <id>`.

## Unix manual page

A Unix manual-page source is supplied as
[`opendata.1`](opendata.1).

From the repository root it can be viewed directly on a Unix-like system with:

```sh
man ./docs/reference/opendata.1
```

It can also be formatted with `groff` where installed:

```sh
groff -man -Tascii docs/reference/opendata.1
```

## Launcher normalisation

OpenData accepts both a normal argument array and the common IDE/wrapper case in
which the entire command line is supplied as one string. Single or double quotes
preserve spaces in a plugin definition filename.

---
