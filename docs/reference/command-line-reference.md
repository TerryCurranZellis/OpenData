# Command-Line Reference

**Document ID:** REF-CLI-001  
**Version:** 1.0  
**Status:** Draft  
**Last Updated:** 22 July 2026

## Purpose

This document describes the command-line interface used to start the OpenData
Framework and select a dataset plugin.

## Syntax

```text
opendata --plugin <plugin-id> [--file <settings.properties>] [options]
```

## Options

| Short option | Long option | Argument | Description |
|---|---|---:|---|
| `-p` | `--plugin` | plugin id | Selects the dataset plugin to execute. |
| `-f` | `--file` | path | Loads an optional configuration override file. |
| | `--dry-run` | | Validates and prepares execution without committing data. |
| `-v` | `--verbose` | | Enables more detailed logging. |
| `-h` | `--help` | | Displays command-line help. |
| | `--version` | | Displays the application version. |
| | `--list-plugins` | | Lists all plugins discovered by the registry. |

## Examples

```powershell
java -jar opendata.jar --plugin ofgem
```

```powershell
java -jar opendata.jar `
    --plugin ofgem `
    --file C:\OpenData\Config\ofgem-local.properties `
    --dry-run
```

```powershell
java -jar opendata.jar --list-plugins
```

## Validation Rules

- A normal processing invocation requires `--plugin`.
- `--file` is valid only when `--plugin` is present.
- `--help`, `--version` and `--list-plugins` do not require a plugin.
- Plugin identifiers are normalised to lowercase for lookup.
- An unknown plugin causes the application to exit with a plugin-selection error.

## Exit Codes

| Code | Meaning |
|---:|---|
| `0` | Successful execution or informational command. |
| `1` | Unexpected application failure. |
| `2` | Invalid command-line arguments. |
| `3` | Invalid or missing configuration. |
| `4` | Plugin execution completed unsuccessfully. |
| `5` | Requested plugin was not found. |

## Related Documents

- `configuration-reference.md`
- `plugin-registry.md`
- `../architecture/007-plugin-architecture.md`
