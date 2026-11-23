# OpenData Documentation

OpenData documentation is maintained as Markdown and PlantUML and assembled into
manifest-defined manuals.

Use the [complete documentation index](DOCUMENTATION-INDEX.md) for navigation.
The current Maven reactor is Version 4.2.0 (the first-run GUI was introduced in 4.0.0), including the split
`opendata-api`, `opendata-common`, `opendata-core`, and the `opendata-plugins`
aggregator and `opendata-launcher`. Each provider plugin and shared provider-support project is a child
project of `opendata-plugins`.

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
| `screenshots/` | Clearly marked temporary PNG placeholders for 4.0 GUI views, to be replaced by real captures |

## Validation and generation

Use the repository's maintained documentation wrappers:

```powershell
.\scripts\Validate-Documentation.ps1 -FailOnWarning
.\scripts\Build-Documentation.ps1 -Document All -Format All -RenderDiagrams
```

Every generated document is defined by a JSON file under `docs/manifests`.
Changing composition should normally require Markdown and manifest updates rather
than PowerShell changes.


## Historic electricity plugins (NESO)

See [Historical electricity plugins](electricity-plugins.md) for independent GB demand and generation mix ingestion.

The build produces four manuals in each selected format. `-Format All` requests HTML,
DOCX, PDF and CHM; those formats have separate external-tool dependencies. A
failure in one format is reported without silently skipping other manuals.
The [screenshot plan](development/gui-screenshot-plan.md) lists required real
GUI captures and the [placeholder directory](screenshots/README.md) explains
how to replace the temporary figures.
