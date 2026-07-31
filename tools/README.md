# Tools

Place the approved PlantUML distribution at `tools/plantuml.jar` when diagram
rendering is required. The JAR is intentionally ignored by Git.

Render diagrams without building manuals:

```powershell
. .\scripts\Convert-PlantUml.ps1
Convert-PlantUml -ProjectRoot $PWD -Format svg -Clean
```

The same renderer can be requested during a documentation build with
`-RenderDiagrams`.
