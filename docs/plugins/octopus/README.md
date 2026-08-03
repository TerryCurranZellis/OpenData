# Octopus Energy Statement Plugin

**Document ID:** PLUGIN-OCTOPUS-INDEX-001
**Version:** 2.0
**Status:** Write path implemented; dry-run defect and live acceptance pending
**Baseline date:** 3 August 2026

---

**Plugin id:** `octopus`
**Implementation:** `com.towermarsh.opendata.plugin.octopus.OctopusPlugin`
**Dataset id:** `octopus-energy-billing`

## Purpose and source boundary

The plugin imports personal Octopus Energy electricity and gas billing records
from PDF statements already present in a local directory. Version 2.0.0 does not
connect to an Octopus account, call an Octopus API, read email or download
attachments. Those remain separate future source-adapter options.

Only files named as follows are candidates:

```text
octopus-energy-statement-YYYY-MM-DD.pdf
```

The date in the filename is parsed as the statement date and candidates are
processed in date/name order.

## Active pipeline

| Stage | Active class | Implemented behaviour |
|---|---|---|
| Initialise | `initialise.OctopusInitialise` | Controls extract, transform, load and finalise with a completion flag |
| Extract | `extract.OctopusExtract` | Validates input directory, lists matching PDFs, calculates SHA-256, checks completed-file ledger and extracts PDF text |
| Transform | `transform.OctopusTransform` / `OctopusStatementParser` | Parses electricity and gas tariff-period records from statement text |
| Load | `load.OctopusLoad` / `OctopusPersistenceRepository` | Transactionally inserts/updates facts and marks source files completed |
| Finalise | `finalise.OctopusFinalise` | Reports metrics and moves successfully committed PDFs to the archive directory |

## Duplicate and changed-file handling

`octopus.statement_file` has a unique `(file_name, sha256)` key. Extraction reads
all `COMPLETED` keys and skips only a file whose lower-cased name and SHA-256 both
match a completed row. A file with the same name but different content is
selected again.

The ledger is marked `COMPLETED` in the same SQL transaction as the electricity
and gas records. Files are moved after commit. An archive failure therefore does
not roll back committed data; it is logged as a warning and requires operational
cleanup.

## PDF parsing

PDF text is extracted before the statement parser identifies:

- bill period and tariff periods;
- tariff name;
- MPAN or MPRN and meter id;
- opening and closing meter readings and reading types;
- electricity kWh or gas cubic metres/kWh;
- unit rate and standing-charge values;
- total electricity or gas charge.

The parser contains layout-specific regular expressions for Octopus's statement
text and two-column extraction effects. A materially changed statement layout
may require parser and fixture updates. It must fail rather than invent missing
financial values.

## Transaction and natural keys

One extraction batch is committed in one transaction. Existing records are
updated when their composite natural key is found; otherwise they are inserted.
The electricity key includes bill date, tariff period, tariff name, MPAN, meter,
and reading dates. The gas key uses the corresponding MPRN fields.

## Configuration

| Property | Packaged value | Requirement |
|---|---|---|
| `input.directory` | `C:\Attachments\octopus` | Must exist and contain matching PDFs |
| `working.directory` | blank | Present in typed configuration but currently unused by the pipeline |
| `archive.directory` | blank | Must be set explicitly for a write run; blank becomes the process working directory |

All three properties are present in the plugin definition, so the current typed
configuration accepts blank working/archive values as `Path.of("")`. Operators
must override the archive path rather than relying on that unsafe default.

Example single-plugin override:

```properties
property.input.directory.value=C:\Attachments\octopus
property.working.directory.value=C:\OpenData\work\octopus
property.archive.directory.value=C:\OpenData\archive\octopus
```

Database-backed configuration stores the same property keys in
`core.plugin_property`.

## Current dry-run defect

Do **not** use this as an acceptance command in the current source baseline:

```text
opendata --plugin octopus --dry-run
```

Although `OctopusLoad` and `OctopusFinalise` avoid writes during a dry run,
`OctopusExtract` still calls `OctopusProcessedFileRepository` to read the
completed-file ledger. The framework intentionally supplies
`UnavailableDatabaseResourceManager` during plugin dry-run execution, so the run
fails before parsing. This is a Java implementation defect, not a configuration
requirement. `--plugin all --dry-run` is affected for the same reason.

## Write-run prerequisites

1. Apply `sql/007a-create-octopus-schema.sql` and the relevant grants.
2. Configure explicit input and archive directories.
3. Protect both directories because statements contain personal billing data.
4. Ensure every candidate PDF follows the supported filename convention.
5. Run the plugin in normal mode and inspect `core.PluginRun`,
   `octopus.statement_file`, `octopus.electric_data`, `octopus.gas_data` and the
   archive directory.

See [plugin reference](../../reference/octopus-plugin.md),
[schema reference](../../reference/octopus-schema.md) and
[architecture](../../architecture/027-octopus-energy-statement-architecture.md).
