# Command-Line Reference

**Document ID:** REF-CLI-001  
**Version:** 2.0  
**Status:** Updated  
**Baseline date:** 01 August 2026  
**Minimum Java version:** 17

---

## Syntax

```text
opendata --plugin <id|all> [--plugin <id>] [--file <settings>] [options]
opendata --register [--file <bootstrap-settings>]
```

| Option | Purpose |
|---|---|
| `-p`, `--plugin` | Select an id, repeat the option, use comma-separated ids, or use `all` |
| `-f`, `--file` | Apply invocation overrides or provide bootstrap database credentials for `--register` |
| `-j`, `--parallelism` | Maximum concurrent plugins, from 1 to 64 |
| `--dry-run` | Download and validate without database, audit or archive writes |
| `-v`, `--verbose` | Enable `FINE` JUL output |
| `-h`, `--help` | Help |
| `--about` | About dialog |
| `--list-plugins` | Registry listing |
| `--register` | Register application and plugin properties in SQL Server |

`--plugin` is required for execution, not informational commands and not
`--register`. `--register` cannot be combined with `--plugin`, `--parallelism`,
or `--dry-run`.

## Examples

```text
opendata --list-plugins
opendata --plugin ofgem --dry-run
opendata --plugin openmeteo --plugin ofgem --parallelism 2
opendata --plugin all --file C:\OpenData\run.properties
opendata --register --file C:\OpenData\bootstrap.properties
```

Create the certificate files before the first registration run:

```powershell
. .\scripts\New-ConfigurationCertificate.ps1
New-ConfigurationCertificate
```

## Outcomes

The process logs one of `SUCCESS`, `PLUGIN_FAILURE`, `COMMAND_LINE_ERROR`,
`CONFIGURATION_ERROR`, `INTERRUPTED` or `APPLICATION_FAILURE`.
