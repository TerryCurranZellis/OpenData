# Command-Line Use

**Document ID:** USER-CLI-005  
**Version:** 2.1  
**Status:** OpenData 2.0.0 implementation guidance  
**Baseline date:** 3 August 2026

---

OpenData uses one or more `--plugin` selections followed by either an
administration operation or normal execution.

## Common commands

```text
opendata --help
opendata --about
opendata --list-plugins
opendata --plugin all --register
opendata --plugin example --register --file C:\OpenData\example.properties
opendata --plugin octopus --disable
opendata --plugin octopus --enable
opendata --plugin octopus --unregister
opendata --plugin ofgem --dry-run
opendata --plugin all --dry-run --parallelism 3
```

## Option summary

| Option | Meaning |
|---|---|
| `-p`, `--plugin` | Plugin id, repeated/comma-separated ids, or `all` |
| `-r`, `--register` | Register selected packaged plugins or one plugin supplied by `--file` |
| `-u`, `--unregister` | Remove selected registered plugins and stored plugin properties |
| `--remove` | Alias for `--unregister` |
| `-e`, `--enable` | Enable selected registered plugins |
| `-d`, `--disable` | Disable selected registered plugins |
| `-f`, `--file` | Plugin definition file; registration only, one named plugin only |
| `-j`, `--parallelism` | Maximum concurrent plugin tasks, integer 1–64 |
| `-n`, `--dry-run` | Run without plugin data writes or run-audit rows |
| `-v`, `--verbose` | Detailed `FINE` logging |
| `-h`, `--help` | Command help |
| `-a`, `--about` | Graphical version/about information |
| `-l`, `--list-plugins` | Registered plugin ids and enabled/disabled status |

The original requested option list used `-d` twice. OpenData uses `-d` for
**disable** and `-n` for **dry-run**. The long form `--dry-run` is unchanged.

## Rules that prevent ambiguous commands

- Plugin execution and every administration operation require `--plugin`.
- `--plugin all` cannot be mixed with named plugins.
- Register, unregister, enable and disable cannot be combined with each other.
- `--dry-run` cannot be combined with an administration operation.
- `--file` requires `--register`, exactly one named plugin, and cannot be used
  with `all`.
- Help, About and plugin listing cannot be mixed with operational options.

## Repeated plugin selection

```text
opendata --plugin ofgem --plugin openmeteo --parallelism 2
```

Comma-separated selection remains supported:

```text
opendata --plugin ofgem,openmeteo --parallelism 2
```

A named plugin runs only when it is registered and enabled. `--plugin all` runs
all registered enabled plugins.

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

See the complete [Command-Line Reference](../reference/command-line-reference.md).
