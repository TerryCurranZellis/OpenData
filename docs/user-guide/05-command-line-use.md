# 5. Command-Line Use

**Document ID:** USER-005  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

## Common commands

```text
opendata --help
opendata --version
opendata --list-plugins
opendata --plugin ofgem --dry-run
opendata --plugin openmeteo --file C:\OpenData\weather.properties
```

`opendata` means the classpath-aware launcher configured for
`com.towermarsh.opendata.Main`.

## Options

| Option | Meaning |
|---|---|
| `-p`, `--plugin` | Plugin id, repeated/comma-separated ids, or `all` |
| `-f`, `--file` | External override file |
| `-j`, `--parallelism` | Maximum concurrent plugins, 1–64 |
| `--dry-run` | Acquisition and validation without persistent writes |
| `-v`, `--verbose` | Detailed `FINE` logging |

The final log records an application status. The current program does not map
that status to the shell exit code, so check the log and per-plugin summaries.
