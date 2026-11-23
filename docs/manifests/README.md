# Document manifests

Each JSON file in this directory defines one generated document. The documentation engine discovers every file matching `manifestPattern` in `config/documentation.json`; no document name is hard-coded in PowerShell.

A manifest must define `id`, `title`, `output`, and an ordered `sections` array. It may override the common values under `defaultDocument`, including `template`, `coverPage`, `copyright`, `revisionHistory`, `generateToc`, `tocDepth`, `numberHeadings`, and `includeInventory`.

Paths in `sections` and front-matter settings are relative to `docs`. Exact paths and `*`, `?`, and `**` patterns are supported. The engine removes duplicate matches while preserving the first manifest occurrence.

To add a guide, add its Markdown sources, copy an existing manifest, assign a unique `id` and `output`, and run:

```powershell
. .\scripts\Invoke-Documentation.ps1
Invoke-Documentation -Action All
```

The maintained repository has four manifests. To build all four manuals in
every supported format, run from the repository root:

```powershell
.\scripts\Build-Documentation.ps1 -Document All -Format All -RenderDiagrams
```

This requires Pandoc, PlantUML/Java, XeLaTeX/SVG conversion tools (PDF) and
Microsoft HTML Help Workshop (CHM). Each failed format is reported and the
remaining documents are attempted. Images referenced by the manuals reside in
`docs/diagrams/generated` (PlantUML) or `docs/screenshots` (GUI captures).
