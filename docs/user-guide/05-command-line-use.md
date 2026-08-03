# 5. Command-Line Use

**Document ID:** USER-005
**Version:** 2.0
**Status:** Updated
**Baseline date:** 3 August 2026

---

## Common commands

```text
opendata --help
opendata --list-plugins
opendata --register --file C:\OpenData\bootstrap.properties
opendata --plugin ofgem --dry-run
opendata --plugin openmeteo --file C:\OpenData\weather.properties
```

`opendata` means the classpath-aware launcher configured for
`com.towermarsh.opendata.OpenData`.

## Options

| Option | Meaning |
|---|---|
| `-p`, `--plugin` | Plugin id, repeated/comma-separated ids, or `all` |
| `-f`, `--file` | External override file |
| `-j`, `--parallelism` | Maximum concurrent plugins, 1–64 |
| `--dry-run` | Acquisition and validation without persistent writes |
| `-v`, `--verbose` | Detailed `FINE` logging |
| `--register` | Copy packaged application and plugin properties into SQL Server |

`--register` is a standalone command and must not be combined with `--plugin`,
`--parallelism`, or `--dry-run`.
