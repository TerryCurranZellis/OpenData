# Tools

Place the approved PlantUML distribution at `tools/plantuml.jar` when diagram
rendering is required. The committed July 2026 SVG baseline was produced with
PlantUML 1.2026.1.

The JAR is intentionally ignored. Render diagrams without building manuals:

```powershell
.\scripts\documentation\Render-PlantUml.ps1 -Format svg -Clean
```
