# OpenData Documentation

OpenData documentation is maintained as Markdown and PlantUML and assembled into
manifest-defined manuals.

Use the [complete documentation index](DOCUMENTATION-INDEX.md) for navigation.
The active baseline is Version 3.0.0. Earlier release records and dated GUI implementation reviews are retained as historical evidence only.

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
[OpenData 3.0.0 release readiness](review/RELEASE-READINESS-STATUS-3.0.0.md).

## Validation and generation

Use the repository's maintained documentation wrappers. Version 3.0.0 restores the missing build wrapper and makes dot-sourcing the underlying function side-effect free:

```powershell
.\scripts\Validate-Documentation.ps1 -FailOnWarning
.\scripts\Build-Documentation.ps1 -Document All -Format All -RenderDiagrams
```

Every generated document is defined by a JSON file under `docs/manifests`.
Changing composition should normally require Markdown/manifest changes rather
than PowerShell changes.
