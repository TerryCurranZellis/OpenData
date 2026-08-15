# 13. Octopus Energy Adjustment Bills

**Document ID:** USER-013-OCTOPUS-ADJUSTMENT  
**Version:** 3.1.0  
**Status:** Version 3.1.0 implementation specification  
**Baseline date:** 15 August 2026  

---

The `octopus-adjustment` plugin imports Octopus Energy adjustment bills that
have already been obtained by the operator.

It is separate from the ordinary `octopus` plugin.

## When to use this plugin

Use `octopus-adjustment` for a recalculated or corrective Octopus bill, for
example after:

- a meter fault or meter replacement;
- corrected meter readings;
- a billing-period recalculation;
- a tariff correction; or
- another historic billing adjustment.

Do not process these PDFs with the ordinary `octopus` plugin.

## Input filename

Example source file:

```text
C:\Attachments\octopus\A-5F191685-419015087-1.pdf
```

The initial account number is:

```text
A-5F191685
```

The plugin accepts PDF files whose names begin with the configured account
number followed by a hyphen.

The date is not taken from the filename. Dates are parsed from the PDF.

## Suggested Version 3.1.0 configuration

```properties
plugin.id=octopus-adjustment
plugin.display-name=Octopus Energy Adjustments
plugin.description=Parses Octopus Energy adjustment PDF files and stores recalculated electricity and gas billing records separately.
plugin.implementation-class=com.towermarsh.opendata.plugin.octopusadjustment.OctopusAdjustmentPlugin
plugin.enabled=true
plugin.configuration-version=1

dataset.id=octopus-energy-adjustments

property.account.number.value=A-5F191685
property.input.directory.value=C:\\Attachments\\octopus
property.working.directory.value=work\\octopus-adjustment
property.archive.directory.value=archive\\octopus-adjustment
```

## Registration

Register the Version 3.1.0 plugin through the OpenData **GUI**. Registration of
new plugins is not available from the CLI.

Place the plugin `.properties` definition in the normal plugin configuration
location, start the GUI and use the **Register** action. The GUI discovers the
new plugin definition and registers it in the OpenData plugin registry.

Do **not** use a `--register` CLI command for `octopus-adjustment`.

After GUI registration, the CLI may still be used for normal plugin operations.
Check the registered configuration with:

```text
opendata --plugin octopus-adjustment --detail
```

## Dry run

Run a dry run before the first write execution and whenever Octopus changes the
adjustment-bill layout:

```text
opendata --plugin octopus-adjustment --dry-run
```

Dry run should:

- find matching PDF files;
- calculate source hashes;
- extract PDF text;
- parse electricity and/or gas rows;
- validate the parsed data; and
- report counts and failures.

It does not load data or archive PDFs.

## Write run

After successful validation:

```text
opendata --plugin octopus-adjustment --execute
```

The write path:

1. discovers matching files;
2. calculates SHA-256;
3. skips previously completed name/hash pairs;
4. extracts PDF text;
5. parses electricity and gas records;
6. loads adjustment-specific database tables;
7. records successful completion in the adjustment-file ledger; and
8. archives committed source PDFs.

## Stored data

Adjustment rows are kept separately from ordinary Octopus data:

```text
octopus.adjustment_electric_data
octopus.adjustment_gas_data
```

This separation is intentional. The adjustment rows describe recalculated
billing and must remain distinguishable from the original statements.

## Reprocessing

If an identical adjustment file has already completed successfully, write mode
skips it.

If a file with the same name has changed content, its SHA-256 changes and it can
be processed as a new source version.

## Failed files

A PDF that cannot be parsed must not be marked completed or archived as a
successful source.

Correct the source/configuration or update the parser if the layout is genuinely
new, then rerun dry-run before write execution.

## Data protection

Adjustment bills can contain account, meter and financial information. Restrict
access to:

- source PDFs;
- archive directories;
- OpenData logs;
- database tables; and
- backups.
