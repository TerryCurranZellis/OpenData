# ADR-0027: Make email attachment processing idempotent

- Status: Pending
- Date: 2026-07-23
- Decision owners: OpenData maintainers

## Context

Email messages and attachments may be encountered more than once because:

- the processing job may be restarted;
- a message may remain in the inbox after a failure;
- Outlook may alter read/unread state;
- the supplier may resend the same bill;
- folder movement may fail after database persistence;
- the same attachment may arrive in more than one message.

Relying only on the email read flag is therefore insufficient.

## Proposed decision

Track each processed attachment using a combination of mailbox and content identifiers.

Candidate identifiers include:

- mailbox name;
- IMAP folder;
- IMAP UID;
- `Message-ID` header;
- attachment filename;
- attachment sequence or part identifier;
- received timestamp;
- SHA-256 hash of the saved PDF.

Before parsing or persistence, the plugin will check whether the message or attachment has already been processed successfully.

Processing status should distinguish at least:

- discovered;
- downloaded;
- parsed;
- persisted;
- completed;
- failed.

The definitive database keys will be selected during implementation.

## Consequences

### Positive

- Scheduled polling can safely re-examine the mailbox.
- Restarting after failure does not normally duplicate records.
- Supplier resends can be identified.
- Failures between database commit and email movement can be recovered.
- Import history provides an operational audit trail.

### Negative or limiting

- Additional metadata tables are required.
- Hashing adds a small amount of processing.
- Identical PDFs may sometimes be legitimate separate messages.
- IMAP UIDs are folder-specific and may change when a message is copied or moved.
- Retention and cleanup rules will be needed.

## Alternatives considered

### Use the unread flag

Rejected because Outlook or another client may change it independently.

### Use only the attachment filename

Rejected because filenames are frequently reused.

### Use only the IMAP UID

Rejected because UIDs are tied to a folder and do not identify duplicate content across messages.

### Use only the PDF hash

Not sufficient by itself because identical documents may occasionally need separate audit records.

## Proposed recovery behaviour

- A completed attachment is skipped.
- A failed attachment can be retried according to configuration.
- A persisted attachment whose email was not moved should resume from the mailbox-update stage.
- A newly received attachment with an existing hash should be flagged for duplicate-policy evaluation.

## Implementation status

Pending.

This ADR records intended future work only. The metadata schema and exact duplicate policy will be agreed after the remaining current-code work is concluded.
