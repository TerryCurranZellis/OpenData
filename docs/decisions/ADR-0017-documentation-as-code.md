# ADR-0017: Use Markdown, PlantUML, Javadoc, and Pandoc for documentation as code

- Status: Accepted
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

The project requires substantial architecture, package, usage, and contributor documentation that must evolve with the source. Binary-only documents are difficult to review and merge in Git.

## Decision

Keep documentation source in the repository. Use Markdown for manuals and ADRs, PlantUML for maintainable diagrams, Javadoc and `package-info.java` for Java API/package documentation, and Pandoc-compatible tooling for generated Word or PDF outputs.

## Consequences

### Positive

- Documentation changes can be reviewed alongside code.
- Plain-text sources work well with Git.
- Diagrams remain reproducible.
- Multiple publication formats can be generated from one source.

### Negative or limiting

- Generated outputs require a build toolchain.
- PlantUML and Pandoc availability must be documented.
- Generated documents may differ slightly across tool versions.

## Alternatives considered

### Maintain Word documents as the primary source

Rejected because binary review and merge workflows are poor.

### Use README files inside every Java package

Not selected where `package-info.java` can provide Javadoc-native package documentation.

## Implementation notes

Generated files should not replace their source documents. Build scripts should pin or report tool versions and fail clearly when required tools are missing.
