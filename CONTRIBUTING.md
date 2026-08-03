# Contributing to OpenData

Thank you for considering a contribution to OpenData. Contributions may include
code, tests, documentation, SQL scripts, diagrams and new data-source plugins.

## Before starting

For a substantial change, open an issue first so the intended behaviour,
architecture and documentation impact can be agreed. Security vulnerabilities
must not be reported publicly; follow [SECURITY.md](SECURITY.md).

By submitting a contribution, you agree that it may be distributed under the
[Apache License 2.0](LICENSE.md).

## Development environment

| Component | Minimum or recommended version |
|---|---|
| Java | 17 LTS compatibility |
| Maven | 3.9 or later |
| Git | Current supported version |
| SQL Server | Required for registration and persistence integration tests |
| PowerShell | 5.1 or later for project scripts |
| Pandoc | Required only for generated manuals |
| PlantUML | Required only for rendered diagrams |

Apache NetBeans is the maintainer's primary IDE, but no IDE-specific workflow is
required.

## Workflow

1. Branch from the current `main` baseline.
2. Make the smallest coherent change that solves the issue.
3. Add or update deterministic tests.
4. Update affected documentation, examples, ADRs, data-source notices and
   diagrams.
5. Run the relevant build and documentation checks.
6. Submit a pull request describing the problem, solution, compatibility impact
   and verification performed.

Do not commit credentials, PFX passwords, private keys, Octopus statement PDFs,
downloaded source datasets, database backups, generated build output or local IDE
state.

## Plugin architecture

A plugin belongs under `com.towermarsh.opendata.plugin.<id>` and uses these
standard packages:

```text
initialise
extract
transform
load
finalise
```

`transform` may contain additional packages such as `model` and `validate` where
source-specific complexity requires them. The root plugin class should remain a
thin framework entry point. Plugins must use the shared exception handling
boundary rather than introducing a plugin-local exception hierarchy.

## Build and test

```powershell
mvn clean verify
```

Where SQL Server is available, also test schema installation, `--register`, an
encrypted bootstrap restart, dry runs, write-mode processing and rollback or
idempotency behaviour. A pull request must state which checks were performed and
which environment-dependent checks were not available.

## Coding expectations

- Target Java 17.
- Use four spaces, UTF-8 and no tab indentation.
- Keep public types focused and document public APIs with JavaDoc.
- Prefer immutable values and constructor injection.
- Use `java.util.logging` for application logging.
- Never log credentials, private keys, PFX passwords, complete connection strings
  or unredacted customer statement data.
- Keep database writes transactional where file completion depends on persistence.

Detailed rules are maintained under [`docs/standards/`](docs/standards/README.md).

## Documentation and diagrams

Documentation is part of the change. Follow
[`docs/Documentation-Standards.md`](docs/Documentation-Standards.md).
PlantUML sources belong in `docs/diagrams/source`; Markdown references the
corresponding SVG under `docs/diagrams/generated`. Do not hand-edit generated SVG
files in the normal development workflow.

When a provider, endpoint, customer-document format or dependency changes, review
`DATA-SOURCE-NOTICES.md` and `THIRD-PARTY-NOTICES.md`.

## Review and acceptance

A contribution is complete only when required checks pass, review comments are
resolved, migrations are documented, and user, operational and architecture
documentation agree with the implemented behaviour.

## Community conduct

All participation is governed by the [Code of Conduct](CODE_OF_CONDUCT.md).
