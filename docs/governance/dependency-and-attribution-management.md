# Dependency and Attribution Management

## On every dependency change

- Record the direct version and purpose.
- Review the upstream licence and `NOTICE` material.
- Inspect transitive dependencies from the resolved Maven graph.
- Review vulnerability and maintenance status.
- Update `THIRD-PARTY-NOTICES.md`.
- Decide whether the component is runtime, test, build or documentation-only.

Preview, milestone, release-candidate or snapshot dependencies require explicit
release approval. The current SQL Server JDBC dependency is a preview build and
must be resolved or waived before a formal production-ready release.

## On every data-source change

- Record official source and terms pages.
- Identify licence, attribution and usage-plan limits.
- Record underlying-dataset requirements where relevant.
- Update provenance fields and operator guidance.
- Update `DATA-SOURCE-NOTICES.md`.

## Release output

Retain a dependency tree, runtime list, licence report where available, notices,
secret scan, archive inventory and review decision. Do not infer compliance only
from a dependency's project homepage; inspect the exact resolved artifact.
