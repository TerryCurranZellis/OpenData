# OpenData 3.1.0 — Octopus Adjustment Plugin

**Document ID:** RELEASE-3.1.0-OCTOPUS-ADJUSTMENT  
**Version:** 3.1.0  
**Status:** Feature implementation specification  
**Baseline date:** 15 August 2026  

---

## Scope

Version 3.1.0 introduces the `octopus-adjustment` plugin for loading Octopus
Energy recalculation/adjustment bills.

Existing Version 3.0.0 code and documentation remain the baseline for existing
features and are not relabelled as Version 3.1.0 by this change.

## New capability

- new plugin id `octopus-adjustment`;
- local PDF discovery by Octopus account-number prefix;
- initial account prefix `A-5F191685`;
- reuse of compatible Octopus PDF extraction;
- reuse of compatible Octopus electricity/gas transformation;
- adjustment-specific database load;
- separate processed-file ledger;
- side-effect-free dry-run;
- post-commit source archive;
- Version 3.1.0 plugin documentation.

## Example source

```text
C:\Attachments\octopus\A-5F191685-419015087-1.pdf
```

## New database objects

```text
octopus.adjustment_electric_data
octopus.adjustment_gas_data
octopus.adjustment_file
```

## Required acceptance tests

Before considering the feature complete:

- configuration rejects missing/blank account number and required paths;
- file discovery accepts the account-prefix PDF pattern;
- unrelated PDFs are ignored;
- a representative adjustment PDF parses expected electricity data;
- a representative adjustment PDF parses expected gas data where present;
- dry run creates no plugin-specific persistent changes;
- first write inserts expected records;
- repeat write skips the completed identical PDF;
- changed-content/same-name input remains eligible;
- electricity load failure rolls back gas and ledger work;
- gas load failure rolls back electricity and ledger work;
- ledger failure rolls back business rows;
- successful commit archives the PDF;
- archive failure preserves committed database data and is clearly logged;
- ordinary Octopus tables are unchanged by adjustment execution.

## Documentation boundary

All documents supplied with this feature are explicitly Version 3.1.0.

No existing Version 3.0.0 manual, ADR, release note or manifest is changed by
this documentation pack.

## Registration

The new `octopus-adjustment` plugin is registered through the OpenData **GUI
only**. CLI registration is not supported. This release therefore does not
require modification of existing main-code registration logic.
