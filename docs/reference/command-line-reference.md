# Command-Line Reference

**Document ID:** REF-CLI-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


## Syntax

```text
opendata --plugin <id|all> [--plugin <id>] [--file <settings>] [options]
```

| Option | Purpose |
|---|---|
| `-p`, `--plugin` | Select an id, repeat the option, use comma-separated ids, or use `all` |
| `-f`, `--file` | Apply invocation overrides |
| `-j`, `--parallelism` | Maximum concurrent plugins, from 1 to 64 |
| `--dry-run` | Download and validate without database, audit or archive writes |
| `-v`, `--verbose` | Enable `FINE` JUL output |
| `-h`, `--help` | Help |
| `--version` | Version |
| `--list-plugins` | Registry listing |

`--plugin` is required for execution, not informational commands. `--file`
requires `--plugin`. `all` cannot be combined with another id and a plugin may
not be selected more than once.

The default parallelism is `execution.max-parallel-plugins`; the actual worker
count cannot exceed the number of selected plugins. Results retain selection
order even when tasks complete in another order.

## Examples

```text
opendata --list-plugins
opendata --plugin ofgem --dry-run
opendata --plugin openmeteo --plugin ofgem --parallelism 2
opendata --plugin openmeteo,ofgem
opendata --plugin all --file C:\OpenData\run.properties
```

The current Maven artifact is not yet an executable JAR. `opendata` represents a
classpath-aware launcher for `com.towermarsh.opendata.Main`.

## Outcomes

The process logs one of `SUCCESS`, `PLUGIN_FAILURE`, `COMMAND_LINE_ERROR`,
`CONFIGURATION_ERROR`, `INTERRUPTED` or `APPLICATION_FAILURE`. Lower layers do
not call `System.exit`, and the current `main` method does not map these values
to operating-system exit codes.
