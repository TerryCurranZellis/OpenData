# OpenData 2.0.0 Release Record

**Status:** Development and release-candidate baseline
**Documentation baseline:** 4 August 2026
**Release date:** Not assigned
**Licence:** Apache License 2.0

## Candidate scope

Version 2.0.0 delivers persistent plugin lifecycle administration,
database-backed configuration registration, certificate-protected bootstrap
credentials, the staged plugin lifecycle, Ofgem/OpenMeteo/Octopus execution and
a provider-neutral validation/JDBC processing foundation for additional
plugins.

![Version evolution](../diagrams/generated/version-evolution.svg)

## Processing-framework completion

The Version 2.0.0 code baseline now includes:

- `PluginPropertyValues`, `ValueParser`, `ValidationRules` and `SqlIdentifiers`;
- `JdbcTransactionTemplate` with optional pooled-session cleanup;
- `JdbcBatchExecutor` and typed statement binders;
- `JdbcUpsertExecutor`, adapters and aggregate results;
- Ofgem migration to shared parsing, transactions and level batching;
- OpenMeteo migration to shared parsing, safe identifiers, staging batches and
  pooled-session cleanup; and
- Octopus migration to shared path parsing, transactions and typed
  electricity/gas upsert adapters.

The provider public workflows and dry-run behaviour are unchanged.

## Documentation completion

Documentation Batches 1–2 add the shared architecture/ADR/API reference,
provider-specific architecture and reference updates, refreshed diagrams,
plugin-template changes, traceability and a final processing-refactor audit.

## Release gates

The following remain mandatory before declaring the release production-ready:

- clean `mvn clean verify` evidence for the merged branch;
- clean documentation validation and generated DOCX/PDF review;
- fresh/repeat SQL Server install and least-privilege evidence;
- registration and encrypted-restart evidence with deployment-specific keys;
- representative Ofgem, OpenMeteo and Octopus write/rollback/idempotency tests;
- provider archive and pooled-session acceptance;
- removal or rotation of tracked secrets and private keys;
- validated SQL Server certificate trust;
- preview JDBC dependency decision; and
- distribution/archive compliance review.

## Historical boundary

Version 1.0.0 records remain under `docs/release/Release-1.0.0.md` and are not
Version 2.0.0 evidence.
