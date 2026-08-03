# Octopus Plugin Reference

**Document ID:** REF-OCTOPUS-001  
**Version:** 2.0  
**Status:** Current implementation reference  
**Baseline date:** 3 August 2026

---

| Item | Value |
|---|---|
| Plugin id | `octopus` |
| Implementation | `com.towermarsh.opendata.plugin.octopus.OctopusPlugin` |
| Source | Local PDFs named `octopus-energy-statement-YYYY-MM-DD.pdf` |
| Registration source | Packaged definition or one complete external properties file |
| Runtime status | Must be registered and enabled in `core.plugin_registry` |

## Commands

```text
opendata --plugin octopus --register
opendata --plugin octopus --register --file C:\OpenData\octopus.properties
opendata --plugin octopus --dry-run
opendata --plugin octopus
opendata --plugin octopus --disable
opendata --plugin octopus --enable
opendata --plugin octopus --unregister
```

## Dry run

The extractor deliberately bypasses the processed-file repository during dry
run. Every matching file is hashed, read and parsed; no provider data, completion
ledger, generic run audit or archive movement occurs.

## Write run

Write mode queries completed `(file_name, sha256)` pairs, excludes exact matches,
persists the entire statement batch and completion ledger in one transaction,
and archives source PDFs after commit.

## Configuration properties

- `property.input.directory.value`
- `property.working.directory.value` (currently unused)
- `property.archive.directory.value`

Use an external file only to register a complete definition. Runtime invocation
files are not supported.
