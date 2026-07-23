# ADR-0024: Use IMAP as a reusable email attachment source

- Status: Pending
- Date: 2026-07-23
- Decision owners: OpenData maintainers

## Context

Some datasets are delivered as attachments to email messages rather than being downloaded from a public HTTP endpoint.

The initial use case is receipt of Octopus Energy bill PDFs. The mailbox is accessed through IMAP and is also used by Outlook 2024. The OpenData application should not depend on Outlook being installed, running, or logged in.

Email retrieval is not specific to Octopus Energy and may later be useful for other suppliers and datasets.

## Proposed decision

Introduce a reusable email-source abstraction within the OpenData framework and provide an IMAP implementation.

The IMAP component will:

- connect directly to a configured IMAP mailbox;
- search a configured folder;
- filter messages by sender address and optional subject text;
- locate PDF attachments;
- save attachments into a controlled staging directory;
- expose saved attachment metadata to a plugin;
- mark, move, or otherwise identify messages after processing;
- distinguish successful and failed processing.

The generic IMAP implementation will remain outside any supplier-specific plugin.

## Proposed package structure

```text
com.towermarsh.opendata.email
├── EmailAttachmentSource
├── EmailSearchCriteria
├── EmailAttachment
├── SavedEmailAttachment
├── EmailProcessingResult
├── EmailProcessingException
└── imap
    ├── ImapEmailAttachmentSource
    └── ImapEmailConfiguration
```

## Consequences

### Positive

- Outlook desktop automation is not required.
- The process can run unattended.
- Email ingestion can be reused by other plugins.
- Supplier-specific logic remains separated from mailbox access.
- IMAP allows deployment independently of a particular desktop email client.

### Negative or limiting

- IMAP authentication and server configuration vary by provider.
- Some providers require application passwords or OAuth authentication.
- Mail folder names and separator rules can vary.
- Message state must be coordinated with ordinary email-client use.
- Additional mail-library dependencies will be required.

## Alternatives considered

### Automate Outlook 2024

Not preferred because it would couple processing to Windows, Outlook, and an interactive desktop session.

### Manually save PDF attachments

Not preferred because it prevents unattended processing and increases the likelihood of missed or duplicate bills.

### Forward attachments to a watched filesystem folder

Possible, but it introduces another external process and does not retain complete mailbox metadata.

## Security considerations

- Passwords and authentication tokens must not be committed to source control.
- Attachment filenames must be sanitised.
- Saved files must remain within configured directories.
- The PDF file signature should be checked before parsing.
- Maximum attachment sizes should be configurable.
- Sender filtering must not be treated as proof that an attachment is safe.

## Implementation status

Pending.

This ADR records intended future work only. Implementation should not begin until the remaining current-code processes have been concluded.
