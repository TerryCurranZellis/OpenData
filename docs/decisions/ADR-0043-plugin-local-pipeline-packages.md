# ADR-0043: Organise provider code as plugin-local pipeline packages

- **Status:** Accepted
- **Date:** 26 July 2026
- **Decision owners:** OpenData maintainers

## Context

Ofgem provider classes were split between a top-level `opendata.ofgem` package
and `plugin.ofgem`; OpenMeteo provider classes were flat beneath
`plugin.openmeteo`. The layout obscured which code was active, allowed an older
Ofgem repository generation to remain disconnected from execution, and made
new-plugin structure ambiguous.

The shared `plugin` package should own registry, workflow execution, audit and
metrics. Provider packages should own only provider-specific stages and data.

## Decision

Every provider implementation lives beneath
`com.towermarsh.opendata.plugin.<id>`.

The provider root contains the `OpenDataPlugin` workflow facade. Typed
configuration, download, extraction, transformation, domain model, validation,
load and provider exception code live in dedicated subpackages:

```text
plugin.<id>
|-- config
|-- download
|-- extract
|-- transform
|   |-- model
|   `-- validate
|-- load
`-- exception (when required)
```

Shared components remain provider-neutral. A maintained set of Java template
files uses the same package skeleton for new plugins.

## Consequences

### Positive

- active provider code has one unambiguous owner;
- root plugin classes read as workflow/pipeline code;
- persistence cannot be confused with shared database infrastructure;
- tests can mirror production package boundaries;
- obsolete parallel implementations are easier to identify and remove;
- a new provider starts from a consistent template.

### Negative or limiting

- package moves change Java imports and test package names;
- downstream callers importing provider implementation types must migrate;
- package structure alone does not enforce dependencies; an automated
  architecture test remains desirable.

## Implementation notes

Implemented by the Ofgem and OpenMeteo package refactoring, removal of the
disconnected Ofgem import/repository stack, stage-specific tests and
`docs/templates/plugin-java`.
