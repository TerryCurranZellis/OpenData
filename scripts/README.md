# OpenData Automation Scripts

The maintained documentation commands target Windows PowerShell 5.1.

## Entry points

| Script | Purpose |
|---|---|
| `documentation/Invoke-Documentation.ps1` | Validate, build or clean the technical manual and user guide |
| `documentation/Render-PlantUml.ps1` | Convert canonical PlantUML sources to SVG without building documentation |

```powershell
# Validate, refresh SVGs, then build HTML, DOCX and PDF manuals
.\scripts\documentation\Invoke-Documentation.ps1 `
    -Action Build -Document All -Format All -RenderDiagrams

# Refresh SVGs only
.\scripts\documentation\Render-PlantUml.ps1 -Format svg -Clean
```

The detailed prerequisites, parameters and A4 orientation rules are in the
[documentation script reference](documentation/README.md). Scripts under
`documentation/OldScripts` are retained only as historical helpers and are not
maintained entry points.

Generated manuals and PDF vector intermediates are ignored. Generated SVGs are
committed documentation assets and must remain synchronized one-to-one with
`docs/diagrams/source`.
