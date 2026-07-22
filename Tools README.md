# OpenData Documentation Automation

This package provides a PowerShell 5.1-compatible documentation toolchain for the OpenData Framework.

## Capabilities

- validates Markdown links and required document metadata;
- generates a combined Markdown manual;
- renders PlantUML diagrams;
- builds HTML, DOCX and PDF outputs with Pandoc;
- supports an optional corporate Word reference document;
- creates a generated table of contents and document inventory;
- cleans generated output;
- provides one orchestration command for local use and CI.

## Prerequisites

Required:

- PowerShell 5.1 or later;
- Pandoc available on `PATH`.

Optional:

- Java 17 or later and `plantuml.jar` for diagram rendering;
- a Pandoc-compatible PDF engine such as MiKTeX, TeX Live or wkhtmltopdf;
- a Word reference document for corporate DOCX styling.

## Quick start

```powershell
.\scripts\documentation\Build-Documentation.ps1 -Format All
```

Build only HTML:

```powershell
.\scripts\documentation\Build-Documentation.ps1 -Format Html
```

Use a Word reference document:

```powershell
.\scripts\documentation\Build-Documentation.ps1 `
    -Format Docx `
    -ReferenceDoc .\config\OpenData-Reference.docx
```

Render diagrams and validate without producing manuals:

```powershell
.\scripts\documentation\Build-Documentation.ps1 `
    -Format None `
    -RenderDiagrams `
    -Validate
```

## Generated output

Generated files are written below `docs/build/` and `docs/diagrams/generated/`. These folders should normally be excluded from source control except for `.gitkeep` files.
