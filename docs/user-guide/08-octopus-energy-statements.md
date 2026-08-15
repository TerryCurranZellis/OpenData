# 8. Octopus Energy Statements

**Document ID:** USER-008-OCTOPUS  
**Version:** 3.0.0  
**Status:** Runtime and dry-run implemented; live acceptance required  
**Baseline date:** 15 August 2026  

---

The Octopus plugin imports PDF billing statements that the operator has already
obtained legitimately. It does not download bills from the Octopus website or
API.

## Required file name

```text
octopus-energy-statement-YYYY-MM-DD.pdf
```

Only regular files matching this pattern, case-insensitively, are considered.
The date in the filename is used as the statement date and files are processed
in date/name order.

## Configuration

Copy the complete packaged Octopus definition and set explicit paths:

```properties
property.input.directory.value=C:\Attachments\octopus\incoming
property.working.directory.value=C:\Attachments\octopus\working
property.archive.directory.value=C:\Attachments\octopus\archive
```

```text
opendata --plugin octopus --register --file C:\OpenData\octopus.properties
```

`working.directory` is currently unused. Do not leave `archive.directory` blank
for a write run because a blank path resolves to the process working directory.

## Controlled acceptance

```text
opendata --plugin octopus --dry-run
opendata --plugin octopus --execute
```

Normal execution requires `--execute` or `-x`; dry-run uses `--dry-run` or `-n` as its own execution authorisation.

Dry run lists and parses candidate PDFs without reading/writing the completion
ledger, loading provider rows, writing `core.PluginRun`, or moving source files.
It therefore examines every matching candidate currently in the input directory.

Write mode calculates SHA-256, skips completed name/hash pairs, parses statements,
commits the complete batch and ledger transactionally, then moves committed files
to the archive directory. If archiving fails, the database commit remains
successful and a warning is logged.

Protect statement files, logs and backups as personal financial data.
