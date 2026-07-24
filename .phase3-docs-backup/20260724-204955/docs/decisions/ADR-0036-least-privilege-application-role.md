# ADR-0036: Use a least-privilege SQL Server application role

- **Status:** Accepted
- **Date:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

The runtime application requires data operations but should not create logins,
alter schemas or own the database. Using an administrator credential would make a
configuration leak much more damaging.

## Decision

Create login and user `OpenData`, add the user to role `opendata_app`, and grant
only the permissions required for normal ingestion on `core` and plugin schemas.
Run bootstrap and schema scripts through a separate privileged identity.

## Consequences

### Positive

- runtime compromise has a smaller database impact;
- deployment privileges remain distinct from execution privileges;
- permission requirements are reviewable in one grant script.

### Negative or limiting

- new tables/procedures may require grant-script updates;
- integration tests need both administrative and application identities;
- ownership chaining and future stored procedures require careful review.

## Alternatives considered

`db_owner` and use of the administrator login by the application were rejected.
