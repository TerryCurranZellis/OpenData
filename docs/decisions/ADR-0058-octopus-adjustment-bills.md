# ADR-0058: Store Octopus adjustment bills separately from ordinary statements

**Version:** 3.1.0  
**Status:** Accepted for Version 3.1.0 implementation  
**Date:** 2026-08-15  
**Decision owners:** OpenData maintainers  

---

## Context

Octopus Energy can issue adjustment bills when earlier charges are recalculated,
including after meter failures or corrected billing information.

The adjustment documents contain electricity and gas records with the same
business structure as ordinary Octopus statements.

Loading them into the ordinary Octopus tables would lose the distinction between
the originally billed data and the later recalculation.

The existing Octopus PDF extract and transform implementation already contains
substantial logic that is applicable to adjustment bills.

## Decision

Create a separate plugin:

```text
octopus-adjustment
```

The plugin will:

- discover local PDFs by configured Octopus account-number prefix;
- reuse compatible Octopus PDF extraction and statement transformation logic;
- retain the existing electricity/gas record structure;
- load adjustment rows into separate adjustment tables;
- maintain a separate processed-file ledger;
- use the standard OpenData dry-run contract; and
- archive successfully committed adjustment PDFs after database commit.

The initial expected filename prefix is:

```text
A-5F191685-
```

## Database decision

Use:

```text
octopus.adjustment_electric_data
octopus.adjustment_gas_data
octopus.adjustment_file
```

The two business tables mirror the ordinary Octopus table structure and natural
keys.

## Consequences

### Positive

- original and recalculated billing remain distinguishable;
- existing parsing logic can be reused;
- adjustment processing gains the same idempotency principles as ordinary
  Octopus statements;
- the plugin remains compatible with the standard staged pipeline;
- consumers can compare original and adjusted billing explicitly.

### Negative or limiting

- duplicate physical table structures must be maintained consistently;
- changes to the Octopus record schema may require matching adjustment schema
  changes;
- PDF parsing remains dependent on Octopus document layout;
- archive movement remains outside the SQL transaction.

## Rejected alternatives

### Load adjustment rows into ordinary Octopus tables

Rejected because provenance would be lost and an adjustment upsert could replace
or become indistinguishable from original billing data.

### Create a completely independent parser

Rejected because the source business structure is sufficiently similar to the
ordinary Octopus statements and maintaining two parser implementations would
increase divergence risk.

### Derive dates from the adjustment filename

Rejected because filenames such as:

```text
A-5F191685-419015087-1.pdf
```

do not encode a reliable bill date.

## Version boundary

This decision is introduced in Version 3.1.0 and does not alter the existing
Version 3.0.0 Octopus decision/documentation baseline.
