# Batch 5 Implementation Notes

> **Historical implementation note:** The later command-line and persistent plugin-registry update supersedes statements here about standalone registration, invocation override files, or Octopus/`all` dry-run limitations. Current CLI and release documents take precedence.

**Completed:** 3 August 2026  
**Scope:** Developer, quality, plugin-extension and API-reference documentation

## Updated

- Developer documentation index, repository layout, local build procedure,
  dependency policy, build/CI guidance and Java quality guide.
- Coding, testing, security, documentation and plugin-package standards.
- Plugin authoring guides for CSV, JSON, Excel, static HTML discovery,
  credentials and Version 2.0.0 contract migration.
- Plugin API, plugin properties, download/discovery and generic parser
  references.
- Developer Guide and API Reference manifests.
- Architecture testing-and-quality chapter.
- Compact example plugin and structural Java plugin template.

## Added

- JSON plugin guide and JSON parser reference.
- Plugin API reference.
- Download/discovery reference.
- Plugin development lifecycle PlantUML source and SVG.

## Important implementation variances retained

- Static analysis and dependency findings are advisory by default because
  `quality.failOnViolation=false`.
- Maven Enforcer is present only as commented POM configuration.
- JaCoCo has no minimum coverage gate.
- The current JAR is not self-contained or directly executable.
- Classpath property changes require registration before database-backed runtime
  use.
- Credential references are modelled, but runtime secret resolution is absent.
- Several format/strategy enum values are modelled without executable shared
  implementations.
- Octopus dry run remains invalid because extract accesses the unavailable
  dry-run database resource.
- Tracked bootstrap credentials and private-key material remain release blockers.

## Exclusions

No production Java, SQL, PowerShell, Maven, workflow or runtime configuration
files were changed. Java files in this archive exist only below `docs` as
maintained documentation examples and templates.
