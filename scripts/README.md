# Documentation Scripts

## Unified script

`Invoke-Documentation.ps1` builds, tests and cleans the documentation.
`Render-PlantUml.ps1` renders diagrams independently.

```powershell
# Build technical documentation and the user guide
.\Invoke-Documentation.ps1 -Action Build -Document All -Format All

# Build only the user guide
.\Invoke-Documentation.ps1 -Action Build -Document User -Format Docx

# Build HTML only, rendering PlantUML diagrams first
.\Invoke-Documentation.ps1 -Action Build -Document Technical -Format Html -RenderDiagrams

# Validate Markdown headings and relative links
.\Invoke-Documentation.ps1 -Action Test -FailOnWarning

# Remove manual output and intermediate non-SVG diagram files
.\Invoke-Documentation.ps1 -Action Clean

# Render all PlantUML sources to SVG without building manuals
.\Render-PlantUml.ps1 -Format svg -Clean
```

### Parameters

| Parameter | Default | Description |
|---|---|---|
| `-Action` | *(required)* | `Build`, `Test`, or `Clean` |
| `-ProjectRoot` | auto-detected | Root of the project tree (must contain `config\documentation.json`) |
| `-Document` | `All` | `Technical`, `User`, or `All` |
| `-Format` | `All` | Output format for Build: `All`, `Html`, `Docx`, `Pdf`, or `None` (merge only) |
| `-ReferenceDoc` | *(config)* | Path to a `.docx` reference document for Docx output |
| `-RenderDiagrams` | `$false` | Render canonical SVG diagrams before building (Build action only) |
| `-FailOnWarning` | `$false` | Treat warnings as errors (Test action only) |

## Page orientation and image size

Portrait diagrams use a maximum width of `16cm`. Wide figures use a fenced
`landscape` block and a maximum width of `22.5cm`, leaving room for the figure
caption inside the A4 landscape section. The Pandoc filter
`docs/_filters/landscape.lua`:

- inserts A4 landscape section boundaries in DOCX and returns to A4 portrait;
- post-processes inline DOCX diagrams to fit a `16cm` by `24cm` portrait box or
  `22.5cm` by `14.5cm` landscape box, preserving aspect ratio;
- omits explicit DOCX chapter-break paragraphs, avoiding blank pages at
  section and table boundaries;
- wraps corresponding PDF figure and wide-table content with `pdflscape`;
- renders Markdown table rows as labelled entries in DOCX, avoiding
  multi-page and narrow-column table defects in Word-compatible renderers;
- leaves HTML as a normal figure block.

The DOCX target width/height boxes are configurable through
`portraitImageWidthCm`, `portraitImageHeightCm`, `landscapeImageWidthCm` and
`landscapeImageHeightCm` in `config/documentation.json`.

For PDF output, the build creates ignored PDF intermediates beside the SVGs
using `rsvg-convert` when available or Inkscape otherwise. Markdown and DOCX
continue to use the canonical SVG asset. `-Action Clean` removes manual output
and intermediate non-SVG diagram files while preserving committed SVGs.

The user guide build appends the complete root `LICENSE.md` as Appendix A.

## Legacy helper scripts

The older scripts under `OldScripts` are retained for reference and are not part
of the maintained pipeline.

| Script | Purpose |
|---|---|
| `Build-Documentation.ps1` | Original orchestration command (superseded by `Invoke-Documentation.ps1`). |
| `Merge-Documentation.ps1` | Combines ordered Markdown files into one manual source. |
| `Render-PlantUml.ps1` | Replaced by the maintained script in the parent folder. |
| `Test-Documentation.ps1` | Checks headings and relative links. |
| `New-DocumentInventory.ps1` | Creates a generated document inventory. |
| `Get-DocumentationFiles.ps1` | Returns documentation files in manual order. |
| `Clean-Documentation.ps1` | Removes generated output. |
| `New-DocumentationStructure.ps1` | Standalone app — creates the standard folder structure. |

Maintained scripts target **Windows PowerShell 5.1**. Pandoc must be on `PATH`;
`tools\plantuml.jar` and Java are required for diagram rendering. PDF output
also requires the configured LaTeX engine, `pdflscape`, and either
`rsvg-convert` or Inkscape.
## Configuration-driven documentation

`Invoke-Documentation.ps1` now reads manual composition from `docs/manifest.json`. Run the script by dot-sourcing it and then invoking the function, for example:

```powershell
. .\scripts\Invoke-Documentation.ps1
Invoke-Documentation -ProjectRoot $PWD -Action Test
Invoke-Documentation -ProjectRoot $PWD -Action Build -Document All -Format Docx -RenderDiagrams
```

Render only PlantUML diagrams with:

```powershell
. .\scripts\Convert-PlantUml.ps1
Convert-PlantUml -ProjectRoot $PWD -Format svg -Clean
```
