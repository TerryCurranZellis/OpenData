# Command-Line Use

**Document ID:** USER-CLI-005  
**Version:** 2.3  
**Status:** OpenData 2.0.0 implementation guidance  
**Baseline date:** 8 August 2026

---

OpenData uses one or more `--plugin` selections followed by either an
administration operation or explicitly authorised execution. A single named
plugin can also be inspected with `--detail`.

Selecting a plugin no longer starts it. Normal execution and dry-run execution
must include `--Execute` or the short form `-x`.

## Common commands

```text
opendata --help
opendata --about
opendata --list-plugins
opendata --plugin all --register
opendata --plugin ofgem --detail
opendata --plugin example --register --file C:\OpenData\example.properties
opendata --plugin octopus --disable
opendata --plugin octopus --enable
opendata --plugin octopus --unregister
opendata --plugin ofgem --Execute
opendata --plugin ofgem --Execute --dry-run
opendata --plugin all --Execute --dry-run --parallelism 3
```

## Option summary

| Option | Meaning |
|---|---|
| `-p`, `--plugin` | Plugin id, repeated/comma-separated ids, or `all` |
| `-x`, `--Execute` | Explicitly authorise normal or dry-run plugin execution |
| `--detail` | Show stored configuration for exactly one named registered plugin |
| `-r`, `--register` | Register selected packaged plugins or one plugin supplied by `--file` |
| `-u`, `--unregister` | Remove selected registered plugins and stored plugin properties |
| `--remove` | Alias for `--unregister` |
| `-e`, `--enable` | Enable selected registered plugins |
| `-d`, `--disable` | Disable selected registered plugins |
| `-f`, `--file` | Plugin definition file; registration only, one named plugin only |
| `-j`, `--parallelism` | Maximum concurrent plugin tasks, integer 1–64 |
| `-n`, `--dry-run` | Run without plugin data writes or run-audit rows; requires `--Execute` |
| `-v`, `--verbose` | Detailed `FINE` logging |
| `-h`, `--help` | Command help |
| `-a`, `--about` | Graphical version/about information |
| `-l`, `--list-plugins` | Registered plugin ids and enabled/disabled status |

The original requested option list used `-d` twice. OpenData uses `-d` for
**disable** and `-n` for **dry-run**. The long form `--dry-run` is unchanged.
`--detail` has no short option.

## Explicit execution

The following command is intentionally incomplete and is rejected:

```text
opendata --plugin ofgem
```

Use either form:

```text
opendata --plugin ofgem --Execute
opendata --plugin ofgem -x
```

A dry run is still an execution request:

```text
opendata --plugin ofgem --Execute --dry-run
```

`--Execute` cannot be combined with detail, register, unregister/remove, enable,
or disable.

## Inspecting one plugin

To see the configuration currently stored for one registered plugin:

```text
opendata --plugin ofgem --detail
```

The output shows the plugin id, name and enabled/disabled status followed by its
stored configuration properties.

`--detail` is deliberately limited to one plugin. These forms are rejected:

```text
opendata --plugin all --detail
opendata --plugin ofgem --plugin openmeteo --detail
opendata --plugin ofgem --detail --Execute
```

Use `--list-plugins` first when you need to discover the available plugin ids,
then use `--detail` for the individual plugin you want to inspect.

## Rules that prevent ambiguous commands

- Plugin execution requires both `--plugin` and `--Execute`.
- `--detail` requires exactly one named plugin and does not use `--Execute`.
- Every administration operation requires `--plugin` but does not use
  `--Execute`.
- `--plugin all` cannot be mixed with named plugins.
- Detail, register, unregister, enable and disable cannot be combined with each
  other.
- `--dry-run` cannot be combined with detail or an administration operation.
- `--file` requires `--register`, exactly one named plugin, and cannot be used
  with `all`.
- Help, About and plugin listing cannot be mixed with operational options.

## Repeated plugin selection

```text
opendata --plugin ofgem --plugin openmeteo --Execute --parallelism 2
```

Comma-separated selection remains supported:

```text
opendata --plugin ofgem,openmeteo --Execute --parallelism 2
```

A named plugin runs only when it is registered and enabled. `--plugin all` with
`--Execute` runs all registered enabled plugins.

Repeated and comma-separated selections are not valid with `--detail`.

## Plugin administration

Registration copies plugin metadata and the complete definition into SQL Server.
Re-registration replaces the definition while preserving the current registry
enabled status.

```text
opendata --plugin all --register
opendata --plugin ofgem --plugin openmeteo --register
```

Use an external definition only for one plugin:

```text
opendata --plugin example --register --file C:\OpenData\example.properties
```

Inspect the stored result:

```text
opendata --plugin example --detail
```

Disable and re-enable without deleting configuration:

```text
opendata --plugin example --disable
opendata --plugin example --enable
```

Remove the registration and stored properties:

```text
opendata --plugin example --unregister
```

Unregister does not delete imported provider data or run history.

See the complete [Command-Line Reference](../reference/command-line-reference.md)
or the Unix-style [`opendata(1)` manual page](../reference/opendata.1).

---
