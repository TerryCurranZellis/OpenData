# Documentation Review — 23 November 2026

**Status:** Changes proposed in draft pull request #3; screenshots and PDF/CHM acceptance outstanding.  
**Repository:** `TerryCurranZellis/OpenData`  
**Checked-in Maven baseline:** `4.2.0`  
**Build toolchain:** JDK 26+ enforced by Maven Enforcer, `maven.compiler.release=24`, JavaFX 27 in Core.

## Review scope and authoritative sources

Compared the current documentation entry points, manifest configuration, all four generated-document manifests, current user/administrator/developer reference sections, Maven POMs, SQL resource ownership, GUI screenshot capture plan, PowerShell documentation scripts and GitHub workflows. The source of truth for current build requirements is the checked-in Maven POM. Historical 3.x/4.0.0 release records and accepted architectural decisions deliberately retain their historical labels.

This is an editorial/structural and CI-backed documentation review, **not** a claim that all historical documents or every Java feature has been manually revalidated on a running Windows/SQL Server environment.

## Confirmed findings and remediations

| Finding | Remediation |
|---|---|
| Direct execution of `Invoke-Documentation.ps1` built DOCX only despite the generic builder supporting four formats | Direct execution now defaults to `Format All`; `ProjectRoot` is optional as documented |
| A failure in one manual/format stopped generation of the rest | Per-format failures are collected, remaining outputs attempted, and an aggregate failure raised at the end |
| GitHub documentation workflow published only HTML | Workflow now builds both HTML and DOCX, verifies one non-empty file of each format for every discovered manifest, and uploads both |
| Current top-level documentation reported 3.0.0/3.1.0/4.0.0 as the current Maven release | Current documentation entry points and generation metadata now identify Maven 4.2.0; original 4.0.0 first-run feature history is preserved |
| Some full-build instructions stated Java 17 or 24 despite the checked-in JDK 26 Enforcer minimum | Current build instructions updated for JDK 26+; noted JavaFX 27 and Java 24 bytecode target are separate constraints |
| Quick start, SQL setup and security guides included obsolete RSA/PFX procedures, invalid or corrupted SQL paths, and an environment/JVM AES setting | Current guides now use the first-run GUI, `security.aes-key-file`, AES-256-GCM and the real ordered SQL scripts |
| SQL resource paths incorrectly placed provider scripts in Core | Corrected provider-owned resource paths beneath `opendata-plugins/opendata-plugin-*/src/main/resources/sql/plugins` |
| GUI screenshot references were described in prose only, without image assets | Added 20 mandatory PNG placeholder targets and one optional dry-run placeholder, with links and replacement instructions |
| Documentation content audit required a specific user's Windows profile and interactive Out-GridView | Removed hard-coded personal paths and made the audit use command-line arguments |
| Java CI used JDK 24 while the POM required JDK 26 | Updated GitHub Java workflow to JDK 26 and aligned Maven Enforcer's error text |

## Generated manuals

Each manifest under `docs/manifests/` has an existing ordered Markdown source list. The four current manifests are `TechnicalUserGuide`, `AdministratorGuide`, `DeveloperGuide`, and `APIReference`. The manifest section files were confirmed to exist during the review.

Run from the repository root on Windows PowerShell 5.1:

```powershell
.\scripts\Validate-Documentation.ps1 -FailOnWarning
.\scripts\Build-Documentation.ps1 -Document All -Format All -RenderDiagrams
```

If all external toolchains are installed and operational, expect 16 delivered manuals (four manifests × HTML, DOCX, PDF and CHM) in `docs/build`, plus intermediate Markdown/inventory/help files.

**External dependencies:** Pandoc; Java + `tools/plantuml.jar` for regenerated SVGs; XeLaTeX and `rsvg-convert` or Inkscape for PDF; Microsoft HTML Help Workshop (`hhc.exe`) for CHM. For an HTML/DOCX-only preview:

```powershell
.\scripts\Build-Documentation.ps1 -Document All -Format Html
.\scripts\Build-Documentation.ps1 -Document All -Format Docx
```

GitHub documentation validation succeeded for the branch: all four HTML and all four DOCX manuals were built and checked for non-empty outputs. The JDK 26 GitHub **Build and quality** workflow also completed successfully after alignment with Maven Enforcer, including Maven `clean verify`, module Javadocs, unified Javadocs and their coverage checks (run [45152629569](https://github.com/TerryCurranZellis/OpenData/actions/runs/45152629569)). **PDF and CHM have not been built or verified by that workflow** and require local Windows acceptance. A passing documentation build is not a substitute for testing each manual's embedded images, cross-references, page layout, TOC and CHM Help navigation.

## Pending GUI screenshot capture

The `docs/screenshots/` PNG files are visibly labelled **placeholders** and must **not** be presented as application screenshots. Replace all 20 mandatory PNGs in place, maintaining the filenames; `gui-dry-run-confirm.png` is optional if it duplicates the normal execution confirmation. See [GUI screenshot capture plan](../development/gui-screenshot-plan.md) and [placeholder inventory](../screenshots/README.md).

Capture real JavaFX states on a disposable SQL Server installation. Verify sensitive values, paths, keys and customer data are not visible. Capture the first-run wizard, normal main view, registration and administration, execute/dry-run, completion logs, Settings, About and Help.

## Open acceptance items

- Replace placeholders with reviewed real application screenshots before release.
- Test the full `-Format All -RenderDiagrams` invocation on the Windows workstation using all required external tools; inspect four PDF and four CHM outputs, TOC, screenshot resolution and page breaks.
- Preserve the successful JDK 26 GitHub Java build evidence alongside the successful documentation workflow; rerun on the final reviewed commit before merge.
- Review and update any provider/CLI/GUI documentation if a subsequent Java implementation change alters behaviour; do not automatically rewrite historical ADRs and release records.
- Keep the pull request draft until the above acceptance evidence is recorded.
