# OpenData Documentation

OpenData documentation is maintained as Markdown and PlantUML and assembled into
manifest-defined manuals.

Use the [complete documentation index](DOCUMENTATION-INDEX.md) for navigation.
The active implementation baseline is Version 3.1.0, including the split
`opendata-api`, `opendata-common`, `opendata-plugins`, and `opendata-core`
Maven modules.

## Structure

| Directory | Purpose |
|---|---|
| `architecture/` | System design, code inventory, and module/package ownership |
| `user-guide/` and `guides/` | Installation and operation |
| `operations/` | Administration, monitoring and recovery |
| `development/` and `standards/` | Maintainer, build, and plugin development guidance |
| `reference/` | CLI, configuration, schema, and plugin API reference |
| `decisions/` | Architecture decision records and register |
| `release/` | Release process, checklist and evidence templates |
| `review/` and `reviews/` | Current and historical assessments |
| `diagrams/source/` | PlantUML sources |
| `diagrams/generated/` | Rendered SVG files referenced by Markdown |
| `manifests/` | One JSON composition manifest per generated manual |

## Validation and generation

Use the repository's maintained documentation wrappers:

```powershell
.\scripts\Validate-Documentation.ps1 -FailOnWarning
.\scripts\Build-Documentation.ps1 -Document All -Format All -RenderDiagrams
```

Every generated document is defined by a JSON file under `docs/manifests`.
Changing composition should normally require Markdown and manifest updates rather
than PowerShell changes.
