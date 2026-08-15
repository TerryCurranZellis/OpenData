# Contributing to OpenData

Thank you for contributing code, tests, documentation, SQL, diagrams or data-source
plugins.

## Before starting

Use an issue or design discussion for substantial changes. Report vulnerabilities
privately through `SECURITY.md`, not through a public issue. By submitting work,
you confirm that you have the right to contribute it under Apache 2.0.

## Development baseline

- Java 24 or later; JDK 26 is the current development JDK.
- Maven 3.9 or later.
- SQL Server for registration and persistence integration testing.
- PowerShell 5.1 or later for repository scripts.
- Pandoc and PlantUML only when rebuilding generated manuals/diagrams.

Apache NetBeans 31 is the current maintainer IDE but is not required.

## Change workflow

1. Branch from the current `main` baseline.
2. Make the smallest coherent change.
3. Add deterministic tests and synthetic fixtures.
4. Update affected architecture, user, operations, reference, ADR, licence and
   data-source documentation.
5. Run available build, quality and documentation checks.
6. Submit a pull request listing compatibility impact, evidence and unavailable
   environment-dependent tests.

## Mandatory safety rules

Never commit live credentials, private deployment keys, PFX passwords, customer
statements, extracted personal data, database backups or unredacted logs. The
current development certificate/bootstrap artifacts are known release blockers
and must not be copied into a production distribution.

New or updated dependencies require a licence/security review and an update to
`THIRD-PARTY-NOTICES.md`. New or changed providers/endpoints require a review of
`DATA-SOURCE-NOTICES.md`.

## Plugin structure

Plugins reside below `com.towermarsh.opendata.plugin.<id>` and use the common
lifecycle packages `initialise`, `extract`, `transform`, `load` and `finalise`.
The plugin root remains a thin framework entry point. Use shared exception and
logging boundaries and transactional persistence where file completion depends
on a successful database commit.

## Verification

Run `mvn clean verify` and the documented documentation validation/build steps.
Where SQL Server is available also verify clean schema installation,
registration, encrypted restart, valid dry runs, representative write runs,
rollback and idempotency. Do not claim an environment-dependent check passed when
it was not run.

Detailed standards are under `docs/standards/` and the release evidence model is
under `docs/governance/`.
