# Documentation scripts

## Unified script

`Invoke-Documentation.ps1` is the single entry point for all documentation tasks.
It inlines every helper script and exposes three actions via the `-Action` parameter.

```powershell
# Build all output formats (HTML, DOCX, PDF)
.\Invoke-Documentation.ps1 -Action Build -ProjectRoot C:\repos\OpenData

# Build HTML only, rendering PlantUML diagrams first
.\Invoke-Documentation.ps1 -Action Build -Format Html -RenderDiagrams

# Validate Markdown headings and relative links
.\Invoke-Documentation.ps1 -Action Test

# Treat warnings as errors during validation
.\Invoke-Documentation.ps1 -Action Test -FailOnWarning

# Remove all generated output
.\Invoke-Documentation.ps1 -Action Clean
```

### Parameters

| Parameter | Default | Description |
|---|---|---|
| `-Action` | *(required)* | `Build`, `Test`, or `Clean` |
| `-ProjectRoot` | auto-detected | Root of the project tree (must contain `config\documentation.json`) |
| `-Format` | `All` | Output format for Build: `All`, `Html`, `Docx`, `Pdf`, or `None` (merge only) |
| `-ReferenceDoc` | *(config)* | Path to a `.docx` reference document for Docx output |
| `-RenderDiagrams` | `$false` | Render PlantUML diagrams before building (Build action only) |
| `-DiagramFormat` | `svg` | PlantUML output format: `svg` or `png` |
| `-FailOnWarning` | `$false` | Treat warnings as errors (Test action only) |

## Individual helper scripts

The scripts below are retained as composable building blocks but are no longer
required to run the documentation pipeline.

| Script | Purpose |
|---|---|
| `Build-Documentation.ps1` | Original orchestration command (superseded by `Invoke-Documentation.ps1`). |
| `Merge-Documentation.ps1` | Combines ordered Markdown files into one manual source. |
| `Render-PlantUml.ps1` | Renders `.puml` sources to SVG or PNG. |
| `Test-Documentation.ps1` | Checks headings and relative links. |
| `New-DocumentInventory.ps1` | Creates a generated document inventory. |
| `Get-DocumentationFiles.ps1` | Returns documentation files in manual order. |
| `Clean-Documentation.ps1` | Removes generated output. |
| `New-DocumentationStructure.ps1` | Standalone app — creates the standard folder structure. |

All scripts target **Windows PowerShell 5.1**.  `Invoke-Documentation.ps1` additionally requires Pandoc to be installed via its Windows installer so the executable is present at `%LOCALAPPDATA%\pandoc\pandoc.exe`.
