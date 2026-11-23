# Documentation Scripts

## Manifest-driven builder

`Invoke-Documentation.ps1` defines the maintained documentation function. Load it into the current PowerShell session and invoke it from the repository root:

```powershell
. .\scripts\Invoke-Documentation.ps1

# Build every manifest in every format
Invoke-Documentation -Action All

# Build every manifest as DOCX
Invoke-Documentation -ProjectRoot $PWD -Action Build -Document All -Format Docx

# Build selected manifests
Invoke-Documentation -ProjectRoot $PWD -Action Build `
  -Document TechnicalUserGuide,AdministratorGuide `
  -Format Docx

# Validate Markdown, manifests, templates and diagrams
Invoke-Documentation -ProjectRoot $PWD -Action Test -FailOnWarning

# Remove generated manuals and non-SVG diagram intermediates
Invoke-Documentation -ProjectRoot $PWD -Action Clean
```

`-Action All` is an alias for `-Action Build -Document All`. All document manifests matching the configured pattern are discovered at runtime. No document id is hard-coded in the script.

### Parameters

| Parameter | Default | Description |
|---|---|---|
| `-Action` | `Build` | `Build`, `Test`, `Clean`, or `All` |
| `-ProjectRoot` | auto-detected | Optional repository root containing `config/documentation.json` |
| `-Document` | `All` | One or more manifest ids, manifest base names, output base names, or `All` |
| `-Format` | `All` | `Html`, `Docx`, `Pdf`, `Chm`, `All` (all four), or `None` |
| `-ReferenceDoc` | manifest/default | Optional DOCX reference document override |
| `-RenderDiagrams` | false | Render PlantUML SVG files before building |
| `-FailOnWarning` | false | Treat validation warnings as errors |

`Build-Documentation.ps1` and `Validate-Documentation.ps1` are maintained wrappers around the manifest-driven `Invoke-Documentation` function. Running `Invoke-Documentation.ps1` directly now builds all manifests in all four formats, rather than DOCX only. A format failure is reported, and remaining formats/manuals are still attempted; a build with any failure ends with a non-success result.

```powershell
.\scripts\Validate-Documentation.ps1 -FailOnWarning
.\scripts\Build-Documentation.ps1 -Document All -Format All -RenderDiagrams
# Preview without optional PDF/CHM dependencies:
.\scripts\Build-Documentation.ps1 -Document All -Format Docx
.\scripts\Test-DocumentationContent.ps1 -CsvPath 'docs/build/documentation-content-audit.csv'
```

The output directory is `docs/build`. Four manifests should yield four HTML, four DOCX, four PDF and four CHM files when all toolchains are present. The real GUI screenshots must replace the [temporary placeholders](../docs/screenshots/README.md) before release.

## Document discovery and composition

Global settings and inherited defaults are stored in `config/documentation.json`. One JSON manifest per output document is stored in `docs/manifests`.

The assembly order is fixed by the generic engine:

1. Cover page.
2. Copyright page.
3. Revision history.
4. Format-aware table of contents.
5. Ordered Markdown sections.

`docs/_filters/document-toc.lua` inserts the TOC at the generated marker. DOCX receives a native Word TOC field with field refresh enabled, PDF receives a LaTeX TOC, and HTML receives linked contents. Automatic HTML/PDF title blocks are suppressed so the cover remains first. The Pandoc `--toc` option is intentionally not used because it would place the TOC before body-based front matter.

## PlantUML

`Convert-PlantUml.ps1` defines the maintained rendering function and contains no local-machine invocation:

```powershell
. .\scripts\Convert-PlantUml.ps1
Convert-PlantUml -ProjectRoot $PWD -Format svg -Clean
```

Canonical `.puml` files belong in `docs/diagrams/source`; rendered SVG files belong in `docs/diagrams/generated`.

## Page orientation and image size

The `landscape.lua` filter creates A4 landscape sections for explicitly marked figures and tables, converts Markdown page breaks to DOCX page breaks, and redirects SVG references to PDF intermediates for LaTeX output. DOCX image bounds are applied after Pandoc using the configured portrait and landscape dimensions.

Maintained scripts target Windows PowerShell 5.1. Pandoc must be on `PATH`; diagram rendering requires Java and the configured PlantUML JAR. PDF output additionally requires the configured LaTeX engine and either `rsvg-convert` or Inkscape.

## Configuration encryption key

`New-ConfigurationEncryptionKey.ps1` creates the random AES-256 key in a
**private location outside the project repository**:

```powershell
.\scripts\New-ConfigurationEncryptionKey.ps1 `
    -Path 'C:\ProgramData\OpenData\security\database.key'
```

Set `security.aes-key-file=C:/ProgramData/OpenData/security/database.key` in
`config/application.properties`, read by the normal configuration loader.
Do not require an environment variable or JVM property for this setting.
```

Protect the key file using NTFS ACLs; never commit it. For migration and
startup instructions see `docs/guides/database-configuration-and-security.md`.
