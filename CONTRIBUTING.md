# Contributing to OpenData

Thank you for considering a contribution to OpenData. Contributions may include
code, tests, documentation, SQL scripts, diagrams and new data-source plugins.

## Before starting

For a substantial change, open an issue first so the intended behaviour,
architecture and documentation impact can be agreed. Security vulnerabilities
must not be reported in public issues; follow [SECURITY.md](SECURITY.md).

By submitting a contribution, you agree that it may be distributed under the
[Apache License 2.0](LICENSE.md).

## Development environment

| Component | Minimum or recommended version |
|---|---|
| Java | 17 LTS |
| Maven | 3.9 or later |
| Git | Current supported version |
| SQL Server | Required for persistence integration tests |
| PowerShell | 5.1 or later for project scripts |
| Pandoc | Required only for generated manuals |
| PlantUML | Required only for rendered diagrams |

Apache NetBeans is the primary development IDE, but no IDE-specific workflow is
required.

## Workflow

1. Fork or branch from the current `main` branch.
2. Use a focused branch name such as `feature/example-plugin` or
   `fix/command-line-parsing`.
3. Make the smallest coherent change that solves the issue.
4. Add or update tests.
5. Update affected documentation, examples, ADRs and diagrams.
6. Run the validation commands below.
7. Submit a pull request describing the problem, solution and verification.

Do not commit credentials, database passwords, downloaded source datasets,
generated build output or local IDE state.

## Build and test

```powershell
mvn clean test
mvn package
```

Where SQL Server is available, also run the relevant database bootstrap and
plugin integration checks. A pull request must state which checks were run and
which could not be run.

## Coding expectations

- Target Java 17.
- Use four spaces, UTF-8 and no tab indentation.
- Keep public types focused and document public APIs with JavaDoc.
- Prefer immutable values and constructor injection.
- Use `java.util.logging` for application logging.
- Do not log credentials, access tokens, full connection strings or sensitive
  source data.
- Translate low-level failures at package boundaries using the project exception
  hierarchy.
- Keep plugin-specific code below `com.towermarsh.opendata.plugin.<id>`.

Detailed requirements are maintained in
[`docs/standards/`](docs/standards/README.md).

## Tests

Tests should be deterministic and should not depend on live internet services
unless explicitly marked as integration tests. Cover normal behaviour, boundary
conditions and expected failures. Bug fixes should normally include a regression
test that fails before the fix and passes afterwards.

## Documentation and diagrams

Documentation is part of the change, not a later activity. Follow
[`docs/Documentation-Standards.md`](docs/Documentation-Standards.md).

PlantUML source files belong in `docs/diagrams/source`. Markdown must reference
the corresponding generated SVG under `docs/diagrams/generated`, not the
`.puml` source. Do not hand-edit generated SVG files.

## Commit and pull-request guidance

Write imperative commit subjects, for example:

```text
Fix parsing of single-element command lines
Add example plugin configuration template
```

A pull request should include:

- a concise description of the problem and solution;
- linked issues or ADRs;
- compatibility or migration implications;
- tests and validation performed;
- screenshots only when they clarify documentation or UI output;
- a checklist of documentation and licence-header changes.

Keep unrelated refactoring out of a functional pull request unless it is needed
to implement the change safely.

## Review and acceptance

Reviewers may request changes for correctness, security, maintainability,
architecture, tests, documentation or licensing. A contribution is complete only
when required checks pass, review comments are resolved and the documentation
matches the implemented behaviour.

## Community conduct

All participation is governed by the
[Code of Conduct](CODE_OF_CONDUCT.md).
