# Octopus Energy Statement Plugin

**Document ID:** PLUGIN-OCTOPUS-INDEX-001  
**Version:** 2.0  
**Status:** Runtime and dry-run implemented; live acceptance pending  
**Baseline date:** 3 August 2026

---

**Plugin id:** `octopus`  
**Implementation:** `com.towermarsh.opendata.plugin.octopus.OctopusPlugin`  
**Dataset id:** `octopus-energy-billing`

## Purpose and source boundary

The plugin imports personal Octopus Energy electricity and gas billing records
from PDF statements already present in a local directory. Version 2.0.0 does not
connect to an Octopus account, call an Octopus API, read email or download
attachments.

Candidate names use:

```text
octopus-energy-statement-YYYY-MM-DD.pdf
```

## Active pipeline

| Stage | Active class | Implemented behaviour |
|---|---|---|
| Initialise | `initialise.OctopusInitialise` | Controls extract, transform, load and finalise |
| Extract | `extract.OctopusExtract` | Lists matching PDFs, hashes files, checks the completion ledger in write mode, and extracts text |
| Transform | `transform.OctopusTransform` / `OctopusStatementParser` | Parses electricity and gas tariff-period records |
| Load | `load.OctopusLoad` / `OctopusPersistenceRepository` | Transactionally inserts/updates facts and marks source files completed |
| Finalise | `finalise.OctopusFinalise` | Reports metrics and moves successfully committed PDFs in write mode |

## Dry-run behaviour

```text
opendata --plugin octopus --dry-run
```

Dry run does not access `octopus.statement_file`, write provider rows, write the
generic audit table or archive files. It processes every matching PDF currently
in the input directory so extraction and parsing can be validated without a
plugin data connection.

## Write-mode duplicate handling

`octopus.statement_file` has a unique `(file_name, sha256)` key. Write-mode
extraction reads all `COMPLETED` keys and skips only a file whose lower-cased
name and SHA-256 both match a completed row. A file with the same name but
different content is selected again.

The ledger is marked `COMPLETED` in the same transaction as electricity and gas
records. Files move after commit; an archive failure therefore does not roll back
committed data.

## Configuration

Copy the full packaged definition, set explicit paths and register it:

```properties
property.input.directory.value=C:\Attachments\octopus\incoming
property.working.directory.value=C:\Attachments\octopus\working
property.archive.directory.value=C:\Attachments\octopus\archive
```

```text
opendata --plugin octopus --register --file C:\OpenData\octopus.properties
```

The file must remain a complete plugin definition, not only the three property
lines. `working.directory` is currently parsed but unused. A write run must not
rely on a blank archive path.

## Write-run prerequisites

1. Apply `sql/007a-create-octopus-schema.sql`, plugin-registry migration and grants.
2. Register and enable the plugin.
3. Protect input/archive directories as personal financial data.
4. Run dry-run validation, then a controlled write-mode acceptance.
5. Inspect `core.PluginRun`, `octopus.statement_file`, provider tables and archive.

See [plugin reference](../../reference/octopus-plugin.md),
[schema reference](../../reference/octopus-schema.md) and
[architecture](../../architecture/027-octopus-energy-statement-architecture.md).
