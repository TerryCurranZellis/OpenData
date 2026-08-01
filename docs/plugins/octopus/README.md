# Octopus Energy Plugin Documentation

**Document ID:** PLUGIN-OCTOPUS-INDEX-001  
**Version:** 2.0  
**Status:** Partial implementation — transform step is typed and validated; extract, load, and finalise are placeholders  
**Baseline date:** 01 Aug 2026

The Octopus plugin imports personal Octopus Energy electricity and gas billing
records from statement PDF files into the OpenData database schema.

## Overview

Unlike the Ofgem and OpenMeteo plugins, which download data from a public HTTP
endpoint, the Octopus plugin processes locally stored PDF files. Octopus Energy
bills arrive as email attachments named
`octopus-energy-statement-YYYY-MM-DD.pdf`; these must be saved to the
configured input directory before the plugin is run.

## Pipeline steps

The plugin follows the five-step ETL pattern:

| Step | Package | Class | Status |
|------|---------|-------|--------|
| Initialise | `initialise` | `OctopusInitialise` | Implemented — orchestrates pipeline |
| Extract | `extract` | `OctopusExtract` | **Placeholder** — lists existing PDFs; download not yet implemented |
| Transform | `transform` | `OctopusTransform` → `OctopusStatementParser` | Implemented |
| Load | `load` | `OctopusLoad` | **Placeholder** — dry-run pass-through; database write not yet implemented |
| Finalise | `finalise` | `OctopusFinalise` | **Placeholder** — logs statistics; file archiving not yet implemented |

## Configuration

The plugin is registered in `config/plugins/octopus.properties`. Three
directory paths must be configured before the plugin can run:

| Property | Description |
|----------|-------------|
| `input.directory` | Directory containing `octopus-energy-statement-YYYY-MM-DD.pdf` files |
| `working.directory` | Temporary directory used during processing |
| `archive.directory` | Directory for archiving processed PDFs after a write run |

All three properties are now declared as `PATH` values and are validated before
the plugin runs.

## Data model

The transform step produces two record types:

- **`ElectricityRecord`** — one row per electricity tariff period per bill,
  using `LocalDate` and `BigDecimal` fields for bill dates, meter readings,
  rates, and totals.

- **`GasRecord`** — one row per gas tariff period per bill, also using typed
  `LocalDate` and `BigDecimal` values, with the addition of consumption in
  cubic metres (m³) and MPRN in place of MPAN.

## PDF parsing

Octopus Energy bills use a two-column page layout. When text is extracted from
the PDF by Apache PDFBox 3.x (`PdfTextExtractor`), the two columns are
interleaved on the same lines. `OctopusStatementParser` normalises this by
joining all lines into a single string and collapsing whitespace runs before
applying regex patterns to extract each field.

The parser handles and validates:
- Ordinal date suffixes (1st, 2nd, 3rd, 4th, …)
- Abbreviated month names with optional trailing dot (Jan., Feb., …)
- Multiple tariff periods per bill
- Catch-up and adjustment bills with non-standard filenames
- Required bill dates, tariff dates, meter readings, rates, and totals

## Exception handling

All exceptions raised within the plugin steps are wrapped in
`com.towermarsh.opendata.exception.PluginException` with plugin name
`"octopus"`. Plugin-specific exception types are not used; all error conditions
are expressed through the global exception hierarchy.

## Code location

All provider-specific code is under
`com.towermarsh.opendata.plugin.octopus`:

```
plugin/octopus/
├── OctopusPlugin.java          — main plugin entry point
├── initialise/
│   ├── OctopusConfiguration.java  — typed configuration record
│   └── OctopusInitialise.java     — pipeline orchestrator
├── extract/
│   ├── OctopusExtract.java        — extract step (placeholder)
│   └── PdfTextExtractor.java      — Apache PDFBox text extraction utility
├── transform/
│   ├── OctopusStatementParser.java — regex-based PDF text parser
│   ├── OctopusTransform.java       — transform step orchestrator
│   ├── OctopusParseResult.java     — combined result holder
│   └── model/
│       ├── ElectricityRecord.java
│       └── GasRecord.java
├── load/
│   └── OctopusLoad.java           — load step (placeholder)
└── finalise/
    └── OctopusFinalise.java       — finalise step (placeholder)
```

## Outstanding work

1. **Extract step**: implement download of new PDFs from an email account or
   cloud storage provider.
2. **Load step**: implement database MERGE operations for electricity and gas
   records.
3. **Finalise step**: implement PDF archiving and working-directory cleanup.
4. **Configuration**: populate the three directory paths in `octopus.properties`
   for each environment.
5. **Database schema**: define target tables (`electric_data`, `gas_data`) and
   the SQL MERGE scripts.
