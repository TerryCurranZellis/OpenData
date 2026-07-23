# Command-Line Reference

**Document ID:** REF-CLI-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## Syntax
`opendata --plugin <id> [--file <override.properties>] [options]`

| Option | Purpose |
|---|---|
| `-p`, `--plugin` | Select installed plugin |
| `-f`, `--file` | Apply invocation overrides |
| `--dry-run` | Prepare without committing data |
| `-v`, `--verbose` | Detailed logging |
| `-h`, `--help` | Help |
| `--version` | Version |
| `--list-plugins` | Registry listing |

`--plugin` is required for execution, not informational commands. `--file`
requires `--plugin`. Lower layers do not call `System.exit`.
