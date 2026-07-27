# 5. Command-Line Use

**Document ID:** USER-005  
**Version:** 1.1  
**Status:** Updated  
**Baseline date:** 27 July 2026

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

`ExecutionStatus` defines numeric status codes internally, but `Main` currently
logs the final status instead of calling `System.exit(...)`. The shell therefore
does not yet receive those application-specific exit codes; inspect the final
application status and per-plugin summaries.
