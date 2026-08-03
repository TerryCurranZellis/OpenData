# Command-Line Reference

**Document ID:** REF-CLI-001  
**Version:** 2.0  
**Status:** Version 2.0.0 implementation reference  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## Syntax

```text
opendata --plugin <id|all> [--plugin <id>] [--file <settings>] [options]
opendata --register [--file <bootstrap-settings>]
opendata --help | --list-plugins | --about
```

| Option | Purpose |
|---|---|
| `-p`, `--plugin` | Select one id, repeated ids, comma-separated ids, or `all` |
| `-f`, `--file` | UTF-8 Java properties override file |
| `-j`, `--parallelism` | Maximum concurrent plugins, integer 1–64 |
| `--dry-run` | No plugin persistence or generic run-audit rows |
| `-v`, `--verbose` | Enable `FINE` JUL output after runtime configuration loads |
| `-h`, `--help` | Print help |
| `--about` | Display graphical About window |
| `--list-plugins` | List installed registry entries |
| `--register` | Register packaged application/plugin properties in SQL Server |

Rules enforced by the parser:

- execution requires `--plugin` unless the request is informational or
  `--register`;
- `all` cannot be combined with another plugin id;
- a plugin id cannot be selected twice;
- `--file` without a plugin is allowed only for `--register`;
- `--register` cannot be combined with plugin, parallelism or dry run; and
- multi-plugin override files cannot contain unscoped plugin keys.

The parser also expands a single launcher argument containing the complete
quoted command line, which supports IDE/wrapper configurations that pass all
options as one string.

## Current execution outcome limitation

`ExecutionStatus` defines codes 0, 1, 2, 3, 4, 5, 6 and 130, but the entry point
does not call `System.exit(statusCode)`. These codes are not currently returned
to the shell. Use the final application log and plugin summaries.

## Dry-run compatibility

Ofgem and OpenMeteo support dry run. Octopus does not, and `all --dry-run` also
fails because `all` selects Octopus.
