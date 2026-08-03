# 5. Command-Line Use

**Document ID:** USER-005  
**Version:** 2.0  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 3 August 2026

---

## Syntax

```text
opendata --plugin <id|all> [--plugin <id>] [--file <settings>] [options]
opendata --register [--file <bootstrap-settings>]
opendata --help
opendata --list-plugins
opendata --about
```

| Option | Meaning |
|---|---|
| `-p`, `--plugin` | Plugin id, repeated ids, comma-separated ids, or `all` |
| `-f`, `--file` | UTF-8 Java properties override file |
| `-j`, `--parallelism` | Maximum concurrent plugins, 1–64 |
| `--dry-run` | Parse and validate without plugin database writes or run-audit rows |
| `-v`, `--verbose` | Enable `FINE` `java.util.logging` output |
| `-h`, `--help` | Print command help |
| `--about` | Show the graphical About dialog |
| `--list-plugins` | List installed plugin descriptors |
| `--register` | Register application and plugin properties in SQL Server |

`--register` cannot be combined with `--plugin`, `--parallelism` or `--dry-run`.
`--file` is the intended companion for registration credentials.

## Examples

```text
opendata --list-plugins
opendata --plugin ofgem --dry-run
opendata --plugin openmeteo --file C:\OpenData\openmeteo.properties
opendata --plugin ofgem,openmeteo --parallelism 2
opendata --plugin octopus --file C:\OpenData\octopus.properties
opendata --register --file C:\OpenData\bootstrap.properties
```

Do not use `--plugin all --dry-run` in this baseline because `all` includes the
Octopus plugin, whose extraction phase currently requires a usable database.

## Final status and shell exit code

The application logs a final status such as `Successful`, `Configuration error`
or `One or more plugins failed`. Although `ExecutionStatus` defines numeric
codes, the main method does not call `System.exit`. The operating-system process
code therefore cannot currently be used as a reliable scheduler result. Parse
and retain the final application and plugin-summary log records instead.

The `--about` option requires a graphical desktop and should not be used in a
headless service or scheduler.
