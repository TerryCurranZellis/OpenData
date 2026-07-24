<#
.SYNOPSIS
    Unified documentation tooling for the OpenData project.

.DESCRIPTION
    Provides Build, Test and Clean actions for the OpenData technical documentation.
    All helper logic is contained in this single script so no dot-sourcing of
    sibling files is required.

.PARAMETER Action
    The action to perform.
      Build  - Merge sources, optionally render diagrams, and convert to the
               requested output format(s) via Pandoc.
      Test   - Validate Markdown headings and relative links.
      Clean  - Remove all generated output from the build and diagram output
               directories.

.PARAMETER ProjectRoot
    Root of the project tree. Must contain config\documentation.json.
    Defaults to the nearest ancestor directory that contains that file.

.PARAMETER Format
    Output format(s) to produce when Action is Build.
    All (default), Html, Docx, Pdf, or None (merge only, no Pandoc conversion).

.PARAMETER ReferenceDoc
    Path to a .docx reference document used when building Docx output.
    Falls back to the path configured in documentation.json when omitted.

.PARAMETER RenderDiagrams
    When specified with Action Build, renders PlantUML diagrams to SVG before
    building.

.PARAMETER DiagramFormat
    Image format for PlantUML output: svg (default) or png.

.PARAMETER FailOnWarning
    When specified with Action Test, treat warnings as errors.

.EXAMPLE
    .\Invoke-Documentation.ps1 -Action Build -ProjectRoot C:\repos\OpenData

.EXAMPLE
    .\Invoke-Documentation.ps1 -Action Build -Format Html -RenderDiagrams

.EXAMPLE
    .\Invoke-Documentation.ps1 -Action Test -FailOnWarning

.EXAMPLE
    .\Invoke-Documentation.ps1 -Action Clean -ProjectRoot C:\repos\OpenData
#>
[CmdletBinding(SupportsShouldProcess = $true)]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('Build', 'Test', 'Clean')]
    [string]$Action,

    [string]$ProjectRoot,

    [ValidateSet('All', 'Html', 'Docx', 'Pdf', 'None')]
    [string]$Format = 'All',

    [AllowNull()]
    [string]$ReferenceDoc,

    [switch]$RenderDiagrams,

    [ValidateSet('svg', 'png')]
    [string]$DiagramFormat = 'svg',

    [switch]$FailOnWarning
)

Set-StrictMode -Version 2.0
$ErrorActionPreference = 'Stop'

# ---------------------------------------------------------------------------
# Helper functions (inlined from Common.ps1)
# ---------------------------------------------------------------------------

function Resolve-ProjectRoot {
    [CmdletBinding()]
    param(
        [string]$StartPath = $PSScriptRoot
    )

    $current = Resolve-Path -LiteralPath $StartPath
    while ($null -ne $current) {
        if (Test-Path -LiteralPath (Join-Path -Path $current -ChildPath 'config\documentation.json')) {
            return $current.Path
        }
        $parent = Split-Path -Parent -Path $current
        if ([string]::IsNullOrWhiteSpace($parent) -or $parent -eq $current.Path) {
            break
        }
        $current = Get-Item -LiteralPath $parent
    }
    throw 'Unable to locate project root containing config\documentation.json.'
}

function Read-DocumentationConfig {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$ProjectRoot
    )

    $path = Join-Path -Path $ProjectRoot -ChildPath 'config\documentation.json'
    if (-not (Test-Path -LiteralPath $path)) {
        throw ('Configuration file not found: {0}' -f $path)
    }
    return Get-Content -LiteralPath $path -Raw -Encoding UTF8 | ConvertFrom-Json
}

function Assert-CommandAvailable {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$Name
    )
    if (-not (Get-Command -Name $Name -ErrorAction SilentlyContinue)) {
        throw ("Required command '{0}' was not found on PATH." -f $Name)
    }
}

function Ensure-Directory {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )
    if (-not (Test-Path -LiteralPath $Path)) {
        New-Item -ItemType Directory -Path $Path -Force | Out-Null
    }
}

# ---------------------------------------------------------------------------
# Get-DocumentationFiles (inlined from Get-DocumentationFiles.ps1)
# ---------------------------------------------------------------------------

function Get-DocumentationFiles {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$ProjectRoot
    )

    $config = Read-DocumentationConfig -ProjectRoot $ProjectRoot
    $docsRoot = Join-Path -Path $ProjectRoot -ChildPath $config.sourceDirectory

    $orderedDirectories = @('architecture', 'development', 'standards', 'guides', 'reference', 'roadmap', 'decisions')
    $files = New-Object -TypeName System.Collections.Generic.List[System.IO.FileInfo]

    $rootReadme = Join-Path -Path $docsRoot -ChildPath 'README.md'
    if (Test-Path -LiteralPath $rootReadme) {
        $files.Add((Get-Item -LiteralPath $rootReadme))
    }

    foreach ($directory in $orderedDirectories) {
        $path = Join-Path -Path $docsRoot -ChildPath $directory
        if (Test-Path -LiteralPath $path) {
            Get-ChildItem -LiteralPath $path -File -Filter '*.md' |
                Sort-Object -Property Name |
                ForEach-Object { $files.Add($_) }
        }
    }

    return $files
}

# ---------------------------------------------------------------------------
# New-DocumentInventory (inlined from New-DocumentInventory.ps1)
# ---------------------------------------------------------------------------

function New-DocumentInventory {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$ProjectRoot,
        [string]$OutputPath
    )

    if ([string]::IsNullOrWhiteSpace($OutputPath)) {
        $OutputPath = Join-Path -Path $ProjectRoot -ChildPath 'docs\build\document-inventory.md'
    }
    Ensure-Directory -Path (Split-Path -Parent -Path $OutputPath)

    $files = Get-DocumentationFiles -ProjectRoot $ProjectRoot
    $lines = New-Object -TypeName System.Collections.Generic.List[string]
    $lines.Add('# Document Inventory')
    $lines.Add('')
    $lines.Add('| Document | First heading | Last modified |')
    $lines.Add('|---|---|---|')

    foreach ($file in $files) {
        $firstHeading = Get-Content -LiteralPath $file.FullName -Encoding UTF8 |
            Where-Object { $_ -match '^#\s+' } |
            Select-Object -First 1
        if ($null -eq $firstHeading) {
            $firstHeading = '(No level-one heading)'
        } else {
            $firstHeading = $firstHeading -replace '^#\s+', ''
        }
        $relative = $file.FullName.Substring($ProjectRoot.Length).TrimStart('\', '/') -replace '\\', '/'
        $lines.Add(('| [{0}](../../{1}) | {2} | {3} |' -f $file.Name, $relative, $firstHeading, $file.LastWriteTime.ToString('yyyy-MM-dd')))
    }

    $lines | Set-Content -LiteralPath $OutputPath -Encoding UTF8
    return $OutputPath
}

# ---------------------------------------------------------------------------
# Merge-Documentation (inlined from Merge-Documentation.ps1)
# ---------------------------------------------------------------------------

function Merge-Documentation {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$ProjectRoot,
        [string]$OutputPath
    )

    if ([string]::IsNullOrWhiteSpace($OutputPath)) {
        $OutputPath = Join-Path -Path $ProjectRoot -ChildPath 'docs\build\OpenData-Manual.md'
    }
    Ensure-Directory -Path (Split-Path -Parent -Path $OutputPath)

    $frontMatter = Join-Path -Path $ProjectRoot -ChildPath 'docs\_includes\manual-front-matter.md'
    $files = Get-DocumentationFiles -ProjectRoot $ProjectRoot

    $writer = New-Object -TypeName System.IO.StreamWriter -ArgumentList ($OutputPath, $false, (New-Object -TypeName System.Text.UTF8Encoding -ArgumentList ($false)))
    try {
        if (Test-Path -LiteralPath $frontMatter) {
            $writer.WriteLine((Get-Content -LiteralPath $frontMatter -Raw -Encoding UTF8))
            $writer.WriteLine()
            $writer.WriteLine('\\newpage')
            $writer.WriteLine()
        }
        foreach ($file in $files) {
            $content = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8
            $content = $content -replace '(?ms)^---\s*\r?\n.*?\r?\n---\s*\r?\n', ''
            $writer.WriteLine($content.Trim())
            $writer.WriteLine()
            $writer.WriteLine('\\newpage')
            $writer.WriteLine()
        }
    } finally {
        $writer.Dispose()
    }

    return $OutputPath
}

# ---------------------------------------------------------------------------
# Render-PlantUml (inlined from Render-PlantUml.ps1)
# ---------------------------------------------------------------------------

function Invoke-PlantUmlRender {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$ProjectRoot,
        [ValidateSet('svg', 'png')]
        [string]$Format = 'svg',
        [switch]$Clean
    )

    $config = Read-DocumentationConfig -ProjectRoot $ProjectRoot
    $source = Join-Path -Path $ProjectRoot -ChildPath $config.diagramSourceDirectory
    $output = Join-Path -Path $ProjectRoot -ChildPath $config.diagramOutputDirectory
    $jar    = Join-Path -Path $ProjectRoot -ChildPath $config.plantUmlJar
    Ensure-Directory -Path $output

    if ($Clean) {
        Get-ChildItem -LiteralPath $output -File -ErrorAction SilentlyContinue | Remove-Item -Force
    }

    if (-not (Test-Path -LiteralPath $jar)) {
        throw "PlantUML JAR not found at '$jar'. Download plantuml.jar and place it in the tools folder."
    }
    Assert-CommandAvailable -Name 'java'

    $diagramFiles = Get-ChildItem -LiteralPath $source -Filter '*.puml' -File -Recurse
    foreach ($diagram in $diagramFiles) {
        & java -jar $jar "-t$Format" -charset UTF-8 -o $output $diagram.FullName
        if ($LASTEXITCODE -ne 0) {
            throw "PlantUML failed for $($diagram.FullName)."
        }
    }
    Write-Output ("Rendered {0} diagram(s) to {1}" -f $diagramFiles.Count, $output)
}

# ---------------------------------------------------------------------------
# Test-Documentation (inlined from Test-Documentation.ps1)
# ---------------------------------------------------------------------------

function Test-Documentation {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$ProjectRoot,
        [switch]$FailOnWarning
    )

    $files  = Get-DocumentationFiles -ProjectRoot $ProjectRoot
    $issues = New-Object System.Collections.Generic.List[object]

    foreach ($file in $files) {
        $content = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8

        if ($content -notmatch '(?m)^#\s+\S') {
            $issues.Add([pscustomobject]@{ Severity = 'Error'; File = $file.FullName; Message = 'Missing level-one heading.' })
        }

        if ($content -match '\t') {
            $issues.Add([pscustomobject]@{ Severity = 'Warning'; File = $file.FullName; Message = 'Tab character found.' })
        }

        $linkMatches = [regex]::Matches($content, '\[[^\]]+\]\(([^)#]+)(?:#[^)]+)?\)')
        foreach ($match in $linkMatches) {
            $target = $match.Groups[1].Value
            if ($target -match '^(https?:|mailto:|#)') { continue }
            $decoded = [System.Uri]::UnescapeDataString($target)
            $linkPath = Join-Path -Path $file.DirectoryName -ChildPath ($decoded -replace '/', '\')
            if (-not (Test-Path -LiteralPath $linkPath)) {
                $issues.Add([pscustomobject]@{ Severity = 'Error'; File = $file.FullName; Message = "Broken relative link: $target" })
            }
        }
    }

    if ($issues.Count -gt 0) {
        $issues | Format-Table -AutoSize | Out-String | Write-Host
    }

    $errorCount   = @($issues | Where-Object Severity -eq 'Error').Count
    $warningCount = @($issues | Where-Object Severity -eq 'Warning').Count
    Write-Host ("Documentation validation completed: {0} error(s), {1} warning(s)." -f $errorCount, $warningCount)

    if ($errorCount -gt 0 -or ($FailOnWarning -and $warningCount -gt 0)) {
        throw 'Documentation validation failed.'
    }
}

# ---------------------------------------------------------------------------
# Clean-Documentation (inlined from Clean-Documentation.ps1)
# ---------------------------------------------------------------------------

function Clear-Documentation {
    [CmdletBinding(SupportsShouldProcess = $true)]
    param(
        [Parameter(Mandatory = $true)]
        [string]$ProjectRoot
    )

    $config = Read-DocumentationConfig -ProjectRoot $ProjectRoot
    foreach ($relative in @($config.buildDirectory, $config.diagramOutputDirectory)) {
        $path = Join-Path -Path $ProjectRoot -ChildPath $relative
        if (Test-Path -LiteralPath $path) {
            if ($PSCmdlet.ShouldProcess($path, 'Remove generated documentation')) {
                Get-ChildItem -LiteralPath $path -Force | Remove-Item -Recurse -Force
            }
        }
    }
    Write-Output 'Clean completed.'
}

# ---------------------------------------------------------------------------
# Build-Documentation (inlined from Build-Documentation.ps1)
# ---------------------------------------------------------------------------

function Build-Documentation {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$ProjectRoot,
        [ValidateSet('All', 'Html', 'Docx', 'Pdf', 'None')]
        [string]$Format = 'All',
        [AllowNull()]
        [string]$ReferenceDoc,
        [switch]$RenderDiagrams,
        [ValidateSet('svg', 'png')]
        [string]$DiagramFormat = 'svg'
    )

    $config = Read-DocumentationConfig -ProjectRoot $ProjectRoot
    $build  = Join-Path -Path $ProjectRoot -ChildPath $config.buildDirectory
    Ensure-Directory -Path $build

    if ($RenderDiagrams) {
        Invoke-PlantUmlRender -ProjectRoot $ProjectRoot -Format $DiagramFormat
    }

    $null = New-DocumentInventory -ProjectRoot $ProjectRoot
    $manual = Merge-Documentation -ProjectRoot $ProjectRoot

    if ($Format -eq 'None') {
        Write-Output ('Documentation preparation completed: {0}' -f $manual)
        return
    }

    Assert-CommandAvailable -Name 'pandoc'
    $baseArgs = @(
        $manual,
        '--from=markdown+yaml_metadata_block+pipe_tables+fenced_divs',
        '--standalone',
        '--toc',
        '--toc-depth=3',
        '--number-sections',
        '--resource-path=' + (Join-Path -Path $ProjectRoot -ChildPath 'docs'),
        '--metadata', 'lang=en-GB'
    )

    $formats = if ($Format -eq 'All') { @('Html', 'Docx', 'Pdf') } else { @($Format) }

    foreach ($item in $formats) {
        switch ($item) {
            'Html' {
                $out = Join-Path -Path $build -ChildPath 'OpenData-Technical-Documentation.html'
                & pandoc @baseArgs '--embed-resources' '--output' $out
            }
            'Docx' {
                $out = Join-Path -Path $build -ChildPath 'OpenData-Technical-Documentation.docx'
                $docxArgs = @($baseArgs)
                $effectiveReference = $ReferenceDoc
                if ([string]::IsNullOrWhiteSpace($effectiveReference)) {
                    $candidate = Join-Path -Path $ProjectRoot -ChildPath $config.referenceDoc
                    if (Test-Path -LiteralPath $candidate) { $effectiveReference = $candidate }
                }
                if (-not [string]::IsNullOrWhiteSpace($effectiveReference)) {
                    $docxArgs += '--reference-doc=' + (Resolve-Path -LiteralPath $effectiveReference).Path
                }
                & pandoc @docxArgs '--output' $out
            }
            'Pdf' {
                $out = Join-Path -Path $build -ChildPath 'OpenData-Technical-Documentation.pdf'
                & pandoc @baseArgs ('--pdf-engine=' + $config.pdfEngine) '--output' $out
            }
        }
        if ($LASTEXITCODE -ne 0) {
            throw ('Pandoc failed while building {0} output.' -f $item)
        }
        Write-Output ('Created {0}' -f $out)
    }
}

# ---------------------------------------------------------------------------
# Entry point — dispatch based on -Action
# ---------------------------------------------------------------------------

if ([string]::IsNullOrWhiteSpace($ProjectRoot)) {
    $ProjectRoot = Resolve-ProjectRoot
}

switch ($Action) {
    'Build' {
        Build-Documentation `
            -ProjectRoot    $ProjectRoot `
            -Format         $Format `
            -ReferenceDoc   $ReferenceDoc `
            -RenderDiagrams:$RenderDiagrams `
            -DiagramFormat  $DiagramFormat
    }
    'Test' {
        Test-Documentation -ProjectRoot $ProjectRoot -FailOnWarning:$FailOnWarning
    }
    'Clean' {
        if ($PSCmdlet.ShouldProcess($ProjectRoot, 'Clean generated documentation')) {
            Clear-Documentation -ProjectRoot $ProjectRoot
        }
    }
}
