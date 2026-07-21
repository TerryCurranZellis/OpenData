# ADR-0006: Use package-info.java for package documentation

- Status: Accepted
- Date: 2026-07-21

## Context

The project requires package-level documentation. Keeping a separate README in every Java package would duplicate the package structure and risk becoming stale.

## Decision

Every production Java package will contain `package-info.java` with JavaDoc describing purpose, responsibilities, dependencies, and extension points. Markdown remains the format for repository-level documentation.

## Consequences

- Package documentation is generated with JavaDoc.
- Documentation remains close to the code.
- Package JavaDoc uses supported JavaDoc HTML and tags rather than assuming full Markdown support.
