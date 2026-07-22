# ADR-0020: Defer the internal scheduler and advanced orchestration

- Status: Shelved
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

The package structure anticipates scheduling, and recurring unattended imports are a likely future requirement. However, the initial application must first provide reliable command-line execution, idempotent processing, run metadata, and clear exit codes. Operating-system schedulers and CI tools can invoke the CLI meanwhile.

## Decision

Do not implement a full internal scheduler in the initial phase. Keep scheduling as a future extension point and design command execution so Windows Task Scheduler, cron, CI/CD, or another external orchestrator can run plugins safely.

## Consequences

### Positive

- Keeps Stage 1 focused on core ETL reliability.
- Avoids premature concurrency, locking, calendar, and persistence complexity.
- External schedulers can be used immediately.
- Allows scheduling requirements to be learned from operational use.

### Negative or limiting

- The application will not initially manage calendars or missed runs itself.
- External scheduler configuration is deployment-specific.
- Future scheduler work may require execution-lock and retry changes.

## Alternatives considered

### Embed Quartz or another scheduler now

Shelved because operational and concurrency requirements are not mature.

### Create a background service immediately

Rejected for the initial command-line delivery because it adds service lifecycle and deployment complexity.

## Implementation notes

Before revisiting this decision, define single-instance locking, overlapping-run policy, retry/back-off rules, calendars, time zones, misfire behaviour, credentials, monitoring, and shutdown handling.
