# Command-Line Reference

**Document ID:** REF-CLI-001  
**Version:** 2.2  
**Status:** OpenData 2.0.0 implementation reference  
**Baseline date:** 7 August 2026  
**Minimum Java version:** 17

---

## Syntax

```text
opendata --plugin <id|all> [--plugin <id>] --Execute [run options]
opendata --plugin <id|all> [--plugin <id>] <administration operation> [options]
opendata --help | --about | --list-plugins
```

Plugin execution is deliberately explicit in version 2.0.0. Selecting a plugin
does **not** run it by itself. Every normal or dry-run execution must also include
`--Execute` or its short form `-x`.

Named `--plugin` options may be repeated and comma-separated ids remain supported
for compatibility.

## Options

| Short | Long | Purpose |
|---|---|---|
| `-p` | `--plugin <id\|all>` | Select one plugin, repeated plugin ids, comma-separated ids, or `all` |
| `-x` | `--Execute` | Explicitly authorise plugin execution; required for normal and dry-run execution |
| `-r` | `--register` | Register or replace selected plugin definitions and configuration |
| `-u` | `--unregister` | Remove selected plugins and their stored plugin configuration |
| — | `--remove` | Alias for `--unregister` |
| `-e` | `--enable` | Enable selected registered plugins |
| `-d` | `--disable` | Disable selected registered plugins |
| `-f` | `--file <plugin.properties>` | External plugin definition used only with registration of one named plugin |
| `-j` | `--parallelism <1-64>` | Maximum concurrent plugin tasks; effective only for runs and dry-runs |
| `-n` | `--dry-run` | Execute without plugin data writes or generic run-audit rows; requires `--Execute` |
| `-v` | `--verbose` | Enable `FINE` `java.util.logging` output |
| `-h` | `--help` | Display command help |
| `-a` | `--about` | Display the graphical About/version window |
| `-l` | `--list-plugins` | List registered plugins and enabled/disabled status |

### Execution gate

`--Execute` is an execution-authorisation switch, not an administration
operation. It has no value argument.

The following command is invalid because selecting a plugin no longer implicitly
runs it:

```text
opendata --plugin ofgem
```

Use:

```text
opendata --plugin ofgem --Execute
```

or:

```text
opendata --plugin ofgem -x
```

Dry-run execution is also gated:

```text
opendata --plugin ofgem --Execute --dry-run
```

`--Execute` cannot be combined with `--register`, `--unregister`/`--remove`,
`--enable`, or `--disable`.

### Short-option collision resolution

The requested specification assigned `-d` to both `--disable` and `--dry-run`.
A command-line token cannot have two meanings, so OpenData assigns:

- `-d` to `--disable`; and
- `-n` to `--dry-run`.

The long option `--dry-run` remains unchanged.

## Selection rules

- Every run and dry-run requires both `--plugin` and `--Execute`.
- Every register, unregister, enable or disable request requires `--plugin`, but
  does not use `--Execute`.
- `--plugin all` cannot be combined with a named plugin id.
- Repeating the same plugin id is rejected.
- `--plugin all` during execution selects all **registered and enabled** plugins.
- A named run fails when the plugin is not registered or is disabled.
- `--plugin` may be repeated, for example:

```text
opendata --plugin ofgem --plugin openmeteo --plugin octopus --Execute
```

## Operation rules

Exactly one administration operation may be present. `--register`,
`--unregister`/`--remove`, `--enable` and `--disable` are mutually exclusive.

| Operation | Named plugins | `all` | `--Execute` | `--file` | `--dry-run` |
|---|---:|---:|---:|---:|---:|
| Run | Yes | Yes | Required | No | Optional |
| Register | Yes | Yes | No | Optional for exactly one named plugin | No |
| Unregister/remove | Yes | Yes | No | No | No |
| Enable | Yes | Yes | No | No | No |
| Disable | Yes | Yes | No | No | No |

`--parallelism` is accepted with administration commands for a consistent
parser contract, but it is ignored because those operations are not concurrent
plugin executions.

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
opendata --plugin ofgem --Execute
opendata --plugin openmeteo --plugin octopus --Execute --parallelism 2
opendata --plugin all --Execute --dry-run
opendata --plugin ofgem,openmeteo --Execute --dry-run --parallelism 2
```

Dry-run still opens SQL Server long enough to read the persistent plugin
registry and database-backed configuration. During plugin execution it supplies
an unavailable database resource and a no-op audit implementation, so plugins
cannot persist provider data. Octopus dry-run deliberately skips its processed
file ledger and validates every matching input statement without archiving it.

## Informational commands

`--help`, `--about` and `--list-plugins` are mutually exclusive and do not
require `--plugin` or `--Execute`. They cannot be combined with plugin selection
or operational options. `--list-plugins` reads `core.plugin_registry`, so SQL
Server bootstrap credentials and the plugin-registry migration must be available.

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
