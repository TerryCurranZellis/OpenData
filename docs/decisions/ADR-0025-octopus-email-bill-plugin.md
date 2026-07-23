# ADR-0025: Introduce the Octopus email bill plugin

- Status: Pending
- Date: 2026-07-23
- Decision owners: OpenData maintainers

## Context

Octopus Energy sends energy bills as PDF attachments to email messages.

Existing code already parses each Octopus PDF and creates records representing:

- gas;
- electricity;
- adjustments.

The parsed records are retained in lists for subsequent processing.

This functionality should be integrated into OpenData without making the generic email framework dependent on Octopus-specific document formats or business rules.

## Proposed decision

Create a plugin named `octopus`.

The plugin will coordinate:

1. locating matching email messages through the reusable email source;
2. saving each PDF attachment;
3. invoking the existing Octopus PDF parser;
4. receiving lists of gas, electricity, and adjustment records;
5. validating the complete parsed result;
6. passing those lists to the persistence layer;
7. marking the email as processed only after successful completion;
8. archiving or quarantining the source PDF.

## Proposed package structure

```text
com.towermarsh.opendata.plugin.octopus
├── OctopusPlugin
├── OctopusConfiguration
├── OctopusEmailImportService
├── OctopusPdfParser
├── OctopusImportResult
├── OctopusProcessingSummary
├── OctopusRepository
├── OctopusImportException
└── model
    ├── GasRecord
    ├── ElectricityRecord
    └── AdjustmentRecord
```

The final package names should be aligned with the plugin package conventions adopted by the current codebase.

## Parsed result model

The preferred boundary object is an immutable result containing:

```text
source PDF
list of Gas records
list of Electricity records
list of Adjustment records
```

The lists should be defensively copied before leaving the parser boundary.

## Consequences

### Positive

- Existing parsing code can be reused.
- The plugin is named after the data supplier rather than the transport mechanism.
- Email retrieval and Octopus parsing remain independently testable.
- Gas, electricity, and adjustment records can evolve independently.
- Other email-sourced plugins can reuse the IMAP component.

### Negative or limiting

- The plugin depends on the current Octopus bill layout.
- Bill-layout changes may require parser changes.
- Sender addresses and subject wording may change.
- A bill may legitimately contain only some record categories, requiring explicit validation rules.
- The existing parser may require an adapter to fit the plugin interface.

## Alternatives considered

### Name the plugin `energy-bill`

Rejected because the current parser and data model are supplier-specific.

### Put IMAP logic inside the Octopus plugin

Rejected because mailbox access is reusable framework infrastructure.

### Rewrite the existing PDF parser

Rejected unless later analysis shows that an adapter cannot provide a clean integration boundary.

## Implementation status

Pending.

This ADR records intended future work only. Existing Octopus parsing code remains outside the current OpenData processing flow until the present implementation phase is concluded.
