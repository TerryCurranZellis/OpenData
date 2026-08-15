# Octopus Energy Adjustment Plugin

**Document ID:** PLUGIN-OCTOPUS-ADJUSTMENT-001  
**Version:** 3.1.0  
**Status:** Version 3.1.0 implementation specification  
**Baseline date:** 15 August 2026  

---

**Plugin id:** `octopus-adjustment`  
**Proposed implementation:** `com.towermarsh.opendata.plugin.octopusadjustment.OctopusAdjustmentPlugin`  
**Dataset id:** `octopus-energy-adjustments`

## Purpose

The `octopus-adjustment` plugin imports Octopus Energy adjustment bills.

Adjustment bills arise when Octopus Energy recalculates charges following events
such as meter failure, corrected meter readings, tariff correction, or another
billing adjustment.

The electricity and gas record structures are compatible with the ordinary
Octopus billing records, but adjustment data is persisted separately so that
historic recalculations cannot be confused with or overwrite the ordinary
statement data.

## Source files

Version 3.1.0 reads locally supplied PDF files.

Example:

```text
C:\Attachments\octopus\A-5F191685-419015087-1.pdf
```

The filename always begins with the Octopus account number. For the initial
configuration:

```text
A-5F191685
```

candidate files therefore match:

```text
A-5F191685-*.pdf
```

case-insensitively for the `.pdf` extension.

Unlike the ordinary Octopus statement convention, the adjustment filename does
not contain a statement date. Bill dates and tariff periods are obtained from
the PDF contents during transformation.


## Registration

The Version 3.1.0 plugin must be registered through the OpenData **GUI**.
Registration of new plugins through the CLI is not supported.

The plugin definition is supplied as a new `.properties` file in the normal
plugin configuration location. The GUI **Register** action discovers and
registers that definition. After registration, CLI commands may be used for
normal operations such as `--detail`, `--dry-run` and `--execute`.

## Pipeline

| Stage | Version 3.1.0 responsibility |
|---|---|
| Initialise | Resolve and validate plugin paths, account number and execution context |
| Extract | Discover account-prefixed PDFs, calculate hashes and extract text |
| Transform | Parse electricity/gas records using the compatible Octopus statement structure |
| Load | Persist to adjustment-specific electricity/gas tables and file ledger |
| Finalise | Archive successfully committed source files and report completion |

## Reuse of ordinary Octopus processing

The plugin should reuse the established Octopus extract and transform logic
where the adjustment layout is compatible.

Preferred reuse includes:

- PDF text extraction behaviour;
- SHA-256 source identity;
- extracted-statement representation;
- electricity and gas parsing rules;
- electricity and gas model records;
- validation rules.

The adjustment plugin must not write to the ordinary Octopus business tables.

## Persistence boundary

Version 3.1.0 uses separate tables:

```text
octopus.adjustment_electric_data
octopus.adjustment_gas_data
octopus.adjustment_file
```

The two business tables use the same business columns, data types and natural
keys as the corresponding ordinary Octopus electricity and gas tables.

The separate ledger records processed adjustment PDFs.

## Transaction contract

A write run should persist:

1. adjustment electricity records;
2. adjustment gas records; and
3. adjustment-file completion rows

inside one database transaction.

If any database operation fails, the adjustment business rows and completion
ledger must roll back together.

Filesystem archive movement occurs only after successful commit.

## Duplicate handling

Write mode identifies a completed source by:

```text
lower-case filename + SHA-256
```

A completed name/hash pair is skipped. If the same filename is later supplied
with different content, the new hash makes it eligible for processing.

## Dry run

Dry run performs discovery, hashing, extraction, parsing and reporting but must
not:

- insert or update adjustment business rows;
- mark an adjustment file completed;
- move source files; or
- otherwise create plugin-specific persistent side effects.

## Security

Adjustment bills contain personal financial and energy-consumption information.
Input, archive, log, backup and database access should therefore follow the same
protection requirements as ordinary Octopus billing data.

## Related Version 3.1.0 documents

- [User guide](../../user-guide/13-octopus-adjustment-bills.md)
- [Architecture](../../architecture/029-octopus-adjustment-architecture.md)
- [Plugin reference](../../reference/octopus-adjustment-plugin.md)
- [Configuration](../../reference/octopus-adjustment-configuration.md)
- [Schema](../../reference/octopus-adjustment-schema.md)
- [Data dictionary](../../reference/octopus-adjustment-data-dictionary.md)
- [Operations](../../operations/octopus-adjustment-operations.md)
