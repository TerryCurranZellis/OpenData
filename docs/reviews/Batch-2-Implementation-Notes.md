# Batch 2 Implementation Notes

## Scope

This batch introduces the documentation framework and prepares configuration-driven manual assembly.

## Included

- Documentation inventory manifest.
- Shared documentation sections.
- Reusable Markdown templates.
- Documentation standards.
- Generated-output directory policy.
- Manual directory placeholders.

## Migration approach

Existing documentation remains in place during initial integration. The generator reads the manifest and can include both legacy locations and new manual/shared locations. This avoids breaking current links while allowing gradual migration.

## Diagram approach

New diagrams are supplied as PlantUML source files in the main integrated repository. Reader-facing Markdown should link to generated SVG assets after rendering.

## Generator flow

![Documentation generation flow](../diagrams/generated/documentation-generation-flow.svg){width=16cm}
