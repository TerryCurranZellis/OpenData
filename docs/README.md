# OpenData Documentation

OpenData documentation is maintained as Markdown and PlantUML and assembled into
manifest-defined manuals.

Use the [complete documentation index](DOCUMENTATION-INDEX.md) for navigation.
The active baseline is Version 2.0.0. Version 1.0.0 release material and dated
reviews are historical evidence only.

## Structure

| Directory | Purpose |
|---|---|
| `architecture/` | System design and code-aligned architecture |
| `user-guide/` and `guides/` | Installation and operation |
| `operations/` | Administration, monitoring and recovery |
| `development/` and `standards/` | Maintainer/plugin development guidance |
| `reference/` | CLI, configuration, schema and plugin API reference |
| `governance/` | Licensing, attribution, privacy and release compliance |
| `decisions/` | Architecture decision records and register |
| `release/` | Release process, checklist and evidence templates |
| `review/` | Current and historical assessments |
| `reviews/` | Documentation batch implementation notes |
| `diagrams/source/` | PlantUML sources |
| `diagrams/generated/` | Rendered SVG files referenced by Markdown |
| `manifests/` | One JSON composition manifest per generated manual |

## Current status

The documentation describes a release-candidate baseline, not a completed
production release. See
[OpenData 2.0.0 release readiness](review/RELEASE-READINESS-STATUS-2.0.0.md).

## Validation and generation

Use the repository's existing documentation scripts; no script changes are part
of the content update:

```powershell
.\scripts\Validate-Documentation.ps1 -FailOnWarning
.\scripts\Convert-PlantUml.ps1
.\scripts\Build-Documentation.ps1 -Document All -Format All
```

Every generated document is defined by a JSON file under `docs/manifests`.
Changing composition should normally require Markdown/manifest changes rather
than PowerShell changes.
