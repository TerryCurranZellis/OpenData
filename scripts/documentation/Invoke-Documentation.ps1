#Requires -Version 5.1
<#
.SYNOPSIS
Builds, validates or cleans OpenData documentation.

.DESCRIPTION
Builds separate technical documentation and user-guide outputs, validates all
Markdown links, and renders canonical PlantUML sources from
docs\diagrams\source into docs\diagrams\generated.

.EXAMPLE
.\Invoke-Documentation.ps1 -Action Test

.EXAMPLE
.\Invoke-Documentation.ps1 -Action Build -Document All -Format Docx -RenderDiagrams
#>
[CmdletBinding(SupportsShouldProcess)]
param(
    [ValidateSet('Build', 'Test', 'Clean')]
    [string] $Action = 'Build',

    [string] $ProjectRoot,

    [ValidateSet('Technical', 'User', 'All')]
    [string] $Document = 'All',

    [ValidateSet('All', 'Html', 'Docx', 'Pdf', 'None')]
    [string] $Format = 'All',

    [string] $ReferenceDoc,

    [switch] $RenderDiagrams,

    [switch] $FailOnWarning
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version 2.0

function Resolve-ProjectRoot {
    param([string] $StartPath = $PSScriptRoot)

    $current = Get-Item -LiteralPath (Resolve-Path -LiteralPath $StartPath)
    while ($null -ne $current) {
        $config = Join-Path -Path $current.FullName -ChildPath 'config\documentation.json'
        if (Test-Path -LiteralPath $config -PathType Leaf) {
            return $current.FullName
        }
        $current = $current.Parent
    }
    throw 'Unable to locate a project root containing config\documentation.json.'
}

function Read-DocumentationConfig {
    param([Parameter(Mandatory)][string] $Root)

    $path = Join-Path -Path $Root -ChildPath 'config\documentation.json'
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        throw "Documentation configuration was not found: $path"
    }
    Get-Content -LiteralPath $path -Raw -Encoding UTF8 | ConvertFrom-Json
}

function Ensure-Directory {
    param([Parameter(Mandatory)][string] $Path)

    if (-not (Test-Path -LiteralPath $Path -PathType Container)) {
        $null = New-Item -ItemType Directory -Path $Path -Force
    }
}

function Assert-Command {
    param([Parameter(Mandatory)][string] $Name)

    if (-not (Get-Command -Name $Name -ErrorAction SilentlyContinue)) {
        throw "Required command '$Name' was not found."
    }
}

function Add-MarkdownDirectory {
    param(
        [Parameter(Mandatory)]
        [System.Collections.Generic.List[System.IO.FileInfo]] $List,
        [Parameter(Mandatory)]
        [string] $Path
    )

    if (-not (Test-Path -LiteralPath $Path -PathType Container)) {
        return
    }

    $readme = Join-Path -Path $Path -ChildPath 'README.md'
    if (Test-Path -LiteralPath $readme -PathType Leaf) {
        $List.Add((Get-Item -LiteralPath $readme))
    }

    $preferredNames = @('ARCHITECTURE.md')
    foreach ($preferredName in $preferredNames) {
        $preferred = Join-Path -Path $Path -ChildPath $preferredName
        if (Test-Path -LiteralPath $preferred -PathType Leaf) {
            $List.Add((Get-Item -LiteralPath $preferred))
        }
    }

    Get-ChildItem -LiteralPath $Path -File -Recurse -Filter '*.md' |
        Where-Object {
            $_.FullName -ne $readme -and
            $_.Name -notin $preferredNames
        } |
        Sort-Object -Property FullName |
        ForEach-Object { $List.Add($_) }
}

function Get-DocumentationFiles {
    param(
        [Parameter(Mandatory)][string] $Root,
        [Parameter(Mandatory)]
        [ValidateSet('Technical', 'User')]
        [string] $DocumentSet
    )

    $docsRoot = Join-Path -Path $Root -ChildPath 'docs'
    $files = New-Object 'System.Collections.Generic.List[System.IO.FileInfo]'

    if ($DocumentSet -eq 'User') {
        Add-MarkdownDirectory -List $files -Path (Join-Path $docsRoot 'user-guide')
        return $files
    }

    $rootReadme = Join-Path -Path $docsRoot -ChildPath 'README.md'
    if (Test-Path -LiteralPath $rootReadme -PathType Leaf) {
        $files.Add((Get-Item -LiteralPath $rootReadme))
    }

    foreach ($directory in @(
            'architecture',
            'development',
            'standards',
            'guides',
            'operations',
            'plugins',
            'reference',
            'roadmap',
            'decisions')) {
        Add-MarkdownDirectory -List $files -Path (Join-Path $docsRoot $directory)
    }

    foreach ($review in @(
            'DOCUMENTATION-GAP-ANALYSIS-2026-07-26.md',
            'UNRESOLVED-TOOLCHAIN-AND-SPECIFICATION-GAPS-2026-07-26.md')) {
        $reviewPath = Join-Path $docsRoot "review\$review"
        if (Test-Path -LiteralPath $reviewPath -PathType Leaf) {
            $files.Add((Get-Item -LiteralPath $reviewPath))
        }
    }
    return $files
}

function Remove-DocumentHeader {
    param([Parameter(Mandatory)][string] $Content)

    $result = $Content -replace '(?ms)^---\s*\r?\n.*?\r?\n---\s*\r?\n', ''
    $result = $result -replace '(?ms)^\*\*Document ID:\*\*.*?\r?\n---\s*\r?\n', ''
    return $result.Trim()
}

function ConvertTo-YamlSingleQuotedString {
    param([AllowEmptyString()][string] $Value)

    return "'" + $Value.Replace("'", "''") + "'"
}

function Test-TrailingLandscapeBlock {
    param([Parameter(Mandatory)][string] $Content)

    return $Content -match
        '(?ms):::\s*\{\.landscape\}.*?:::\s*$'
}

function New-DocumentInventory {
    param(
        [Parameter(Mandatory)][string] $Root,
        [Parameter(Mandatory)]
        [ValidateSet('Technical', 'User')]
        [string] $DocumentSet
    )

    $config = Read-DocumentationConfig -Root $Root
    $build = Join-Path -Path $Root -ChildPath $config.buildDirectory
    Ensure-Directory -Path $build
    $name = '{0}-document-inventory.md' -f $DocumentSet.ToLowerInvariant()
    $output = Join-Path -Path $build -ChildPath $name
    $lines = New-Object 'System.Collections.Generic.List[string]'
    $lines.Add("# $DocumentSet Document Inventory")
    $lines.Add('')
    $lines.Add('| Document | First heading |')
    $lines.Add('|---|---|')

    foreach ($file in (Get-DocumentationFiles -Root $Root -DocumentSet $DocumentSet)) {
        $heading = Get-Content -LiteralPath $file.FullName -Encoding UTF8 |
            Where-Object { $_ -match '^#\s+\S' } |
            Select-Object -First 1
        if ($null -eq $heading) {
            $heading = '(No level-one heading)'
        } else {
            $heading = $heading -replace '^#\s+', ''
        }
        $relative = $file.FullName.Substring($Root.Length).TrimStart('\', '/') -replace '\\', '/'
        $lines.Add("| [$($file.Name)](../../$relative) | $heading |")
    }
    $lines | Set-Content -LiteralPath $output -Encoding UTF8
    return $output
}

function Merge-Documentation {
    param(
        [Parameter(Mandatory)][string] $Root,
        [Parameter(Mandatory)]
        [ValidateSet('Technical', 'User')]
        [string] $DocumentSet
    )

    $config = Read-DocumentationConfig -Root $Root
    $build = Join-Path -Path $Root -ChildPath $config.buildDirectory
    Ensure-Directory -Path $build
    $baseName = if ($DocumentSet -eq 'Technical') {
        $config.technicalOutputBaseName
    } else {
        $config.userOutputBaseName
    }
    $title = if ($DocumentSet -eq 'Technical') {
        $config.manualTitle
    } else {
        $config.userGuideTitle
    }
    $output = Join-Path -Path $build -ChildPath "$baseName.md"
    $encoding = New-Object System.Text.UTF8Encoding($false)
    $writer = New-Object System.IO.StreamWriter($output, $false, $encoding)
    try {
        $documentDate = (Get-Date).ToString(
            $config.dateFormat,
            [System.Globalization.CultureInfo]::InvariantCulture)
        $writer.WriteLine('---')
        $writer.WriteLine(
            'title: {0}' -f (ConvertTo-YamlSingleQuotedString -Value $title))
        $writer.WriteLine(
            'author: {0}' -f (
                ConvertTo-YamlSingleQuotedString -Value $config.author))
        $writer.WriteLine(
            'date: {0}' -f (
                ConvertTo-YamlSingleQuotedString -Value $documentDate))
        $writer.WriteLine("lang: $($config.language)")
        $writer.WriteLine('---')
        $writer.WriteLine()

        $files = @(Get-DocumentationFiles -Root $Root -DocumentSet $DocumentSet)
        for ($index = 0; $index -lt $files.Count; $index++) {
            $file = $files[$index]
            $content = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8
            $content = Remove-DocumentHeader -Content $content
            $writer.WriteLine($content)
            $writer.WriteLine()
            $hasFollowingContent =
                $index -lt ($files.Count - 1) -or $DocumentSet -eq 'User'
            if ($hasFollowingContent -and
                -not (Test-TrailingLandscapeBlock -Content $content)) {
                $writer.WriteLine('\newpage')
                $writer.WriteLine()
            }
        }

        if ($DocumentSet -eq 'User') {
            $license = Join-Path -Path $Root -ChildPath 'LICENSE.md'
            if (-not (Test-Path -LiteralPath $license -PathType Leaf)) {
                throw "Repository licence was not found: $license"
            }
            $writer.WriteLine('# Appendix A: Apache License 2.0')
            $writer.WriteLine()
            $writer.WriteLine((Get-Content -LiteralPath $license -Raw -Encoding UTF8))
        }
    } finally {
        $writer.Dispose()
    }
    return $output
}

function Invoke-PlantUmlRender {
    param(
        [Parameter(Mandatory)][string] $Root,
        [ValidateSet('svg', 'png')][string] $OutputFormat
    )

    $renderer = Join-Path $Root 'scripts\documentation\Render-PlantUml.ps1'
    if (-not (Test-Path -LiteralPath $renderer -PathType Leaf)) {
        throw "PlantUML renderer was not found: $renderer"
    }
    & $renderer -ProjectRoot $Root -Format $OutputFormat
    if (-not $?) {
        throw 'PlantUML rendering failed.'
    }
}

function Test-Documentation {
    param(
        [Parameter(Mandatory)][string] $Root,
        [switch] $WarningsAreErrors
    )

    $issues = New-Object 'System.Collections.Generic.List[object]'
    $docsRoot = Join-Path -Path $Root -ChildPath 'docs'
    $config = Read-DocumentationConfig -Root $Root
    $buildRoot = [System.IO.Path]::GetFullPath(
        (Join-Path -Path $Root -ChildPath $config.buildDirectory))
    $files = @(Get-ChildItem -LiteralPath $docsRoot -File -Recurse -Filter '*.md' |
        Where-Object {
            -not $_.FullName.StartsWith(
                $buildRoot,
                [System.StringComparison]::OrdinalIgnoreCase)
        })

    foreach ($file in $files) {
        $content = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8
        if ($content -notmatch '(?m)^#\s+\S') {
            $issues.Add([pscustomobject]@{
                    Severity = 'Error'; File = $file.FullName
                    Message = 'Missing level-one heading.'
                })
        }
        if ($content -match "`t") {
            $issues.Add([pscustomobject]@{
                    Severity = 'Warning'; File = $file.FullName
                    Message = 'Tab character found.'
                })
        }

        foreach ($match in [regex]::Matches($content, '!?\[[^\]]*\]\(([^)#]+)(?:#[^)]+)?\)')) {
            $target = $match.Groups[1].Value.Trim('<', '>')
            if ($target -match '^(https?:|mailto:|#)') {
                continue
            }
            $decoded = [uri]::UnescapeDataString($target)
            $linkPath = [System.IO.Path]::GetFullPath(
                (Join-Path -Path $file.DirectoryName -ChildPath ($decoded -replace '/', '\')))
            if (Test-Path -LiteralPath $linkPath) {
                continue
            }

            $issues.Add([pscustomobject]@{
                    Severity = 'Error'; File = $file.FullName
                    Message = "Broken relative link: $target"
                })
        }
    }

    $legacyPuml = @(Get-ChildItem -LiteralPath (Join-Path $Root 'docs\diagrams') `
        -File -Recurse -Filter '*.puml' |
        Where-Object { $_.DirectoryName -ne (Join-Path $Root 'docs\diagrams\source') })
    foreach ($file in $legacyPuml) {
        $issues.Add([pscustomobject]@{
                Severity = 'Error'; File = $file.FullName
                Message = 'PlantUML source is outside docs\diagrams\source.'
            })
    }

    if ($issues.Count -gt 0) {
        $issues | Format-Table -AutoSize | Out-String | Write-Output
    }
    $errors = @($issues | Where-Object Severity -eq 'Error').Count
    $warnings = @($issues | Where-Object Severity -eq 'Warning').Count
    Write-Output "Documentation validation completed: $errors error(s), $warnings warning(s)."
    if ($errors -gt 0 -or ($WarningsAreErrors -and $warnings -gt 0)) {
        throw 'Documentation validation failed.'
    }
}

function Convert-SvgAssetsForPdf {
    param([Parameter(Mandatory)][string] $Root)

    $config = Read-DocumentationConfig -Root $Root
    $diagramDirectory = Join-Path -Path $Root -ChildPath $config.diagramOutputDirectory
    $svgFiles = @(Get-ChildItem -LiteralPath $diagramDirectory -File -Filter '*.svg')
    if ($svgFiles.Count -eq 0) {
        return
    }

    $rsvg = Get-Command -Name 'rsvg-convert' -ErrorAction SilentlyContinue
    $inkscape = Get-Command -Name 'inkscape' -ErrorAction SilentlyContinue
    if ($null -eq $rsvg -and $null -eq $inkscape) {
        throw "PDF output with SVG diagrams requires 'rsvg-convert' or 'inkscape'."
    }

    foreach ($svg in $svgFiles) {
        $pdf = Join-Path $diagramDirectory "$($svg.BaseName).pdf"
        if ((Test-Path -LiteralPath $pdf -PathType Leaf) -and
            (Get-Item -LiteralPath $pdf).LastWriteTimeUtc -ge $svg.LastWriteTimeUtc) {
            continue
        }

        if ($null -ne $rsvg) {
            & $rsvg.Path -f pdf -o $pdf $svg.FullName
        } else {
            & $inkscape.Path $svg.FullName --export-type=pdf `
                "--export-filename=$pdf" --export-area-drawing
        }
        if ($LASTEXITCODE -ne 0 -or
            -not (Test-Path -LiteralPath $pdf -PathType Leaf) -or
            (Get-Item -LiteralPath $pdf).Length -eq 0) {
            throw "Unable to create the PDF diagram asset: $pdf"
        }
    }
}

function Set-DocxPageLayout {
    param(
        [Parameter(Mandatory)][string] $Path,
        [Parameter(Mandatory)][double] $PortraitWidthCm,
        [Parameter(Mandatory)][double] $PortraitHeightCm,
        [Parameter(Mandatory)][double] $LandscapeWidthCm,
        [Parameter(Mandatory)][double] $LandscapeHeightCm
    )

    Add-Type -AssemblyName System.IO.Compression
    Add-Type -AssemblyName System.IO.Compression.FileSystem

    $wordNamespace =
        'http://schemas.openxmlformats.org/wordprocessingml/2006/main'
    $drawingNamespace =
        'http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'
    $drawingMlNamespace =
        'http://schemas.openxmlformats.org/drawingml/2006/main'
    $stream = [System.IO.File]::Open(
        $Path,
        [System.IO.FileMode]::Open,
        [System.IO.FileAccess]::ReadWrite,
        [System.IO.FileShare]::None)
    $archive = New-Object System.IO.Compression.ZipArchive(
        $stream,
        [System.IO.Compression.ZipArchiveMode]::Update,
        $false)

    try {
        $entry = $archive.GetEntry('word/document.xml')
        if ($null -eq $entry) {
            throw "The DOCX package does not contain word/document.xml: $Path"
        }

        $entryStream = $entry.Open()
        try {
            $reader = New-Object System.IO.StreamReader(
                $entryStream,
                [System.Text.Encoding]::UTF8,
                $true,
                4096,
                $true)
            try {
                $documentXml = $reader.ReadToEnd()
            } finally {
                $reader.Dispose()
            }
        } finally {
            $entryStream.Dispose()
        }

        $xml = New-Object System.Xml.XmlDocument
        $xml.PreserveWhitespace = $true
        $xml.LoadXml($documentXml)
        $namespaces = New-Object System.Xml.XmlNamespaceManager($xml.NameTable)
        $namespaces.AddNamespace('w', $wordNamespace)
        $namespaces.AddNamespace('wp', $drawingNamespace)
        $namespaces.AddNamespace('a', $drawingMlNamespace)
        $body = $xml.SelectSingleNode('/w:document/w:body', $namespaces)
        if ($null -eq $body) {
            throw "The DOCX document body could not be read: $Path"
        }

        $sectionImages =
            New-Object 'System.Collections.Generic.List[System.Xml.XmlElement]'
        $resized = 0

        foreach ($child in @($body.ChildNodes)) {
            foreach ($inline in @($child.SelectNodes('.//wp:inline', $namespaces))) {
                $sectionImages.Add($inline)
            }

            $sectionProperties = if (
                $child.LocalName -eq 'sectPr' -and
                $child.NamespaceURI -eq $wordNamespace) {
                $child
            } else {
                $child.SelectSingleNode('.//w:sectPr', $namespaces)
            }
            if ($null -eq $sectionProperties) {
                continue
            }

            $pageSize =
                $sectionProperties.SelectSingleNode('./w:pgSz', $namespaces)
            $landscape = $false
            if ($null -ne $pageSize) {
                $orientation =
                    $pageSize.GetAttribute('orient', $wordNamespace)
                $width = $pageSize.GetAttribute('w', $wordNamespace)
                $height = $pageSize.GetAttribute('h', $wordNamespace)
                $landscape = $orientation -eq 'landscape' -or
                    ([long]$width -gt [long]$height)
            }

            $widthCm = if ($landscape) {
                $LandscapeWidthCm
            } else {
                $PortraitWidthCm
            }
            $heightCm = if ($landscape) {
                $LandscapeHeightCm
            } else {
                $PortraitHeightCm
            }
            $maximumWidth = [long][Math]::Round($widthCm * 360000.0)
            $maximumHeight = [long][Math]::Round($heightCm * 360000.0)

            foreach ($inline in @($sectionImages)) {
                $extent = $inline.SelectSingleNode('./wp:extent', $namespaces)
                if ($null -eq $extent) {
                    continue
                }
                $oldWidth = [double]$extent.GetAttribute('cx')
                $oldHeight = [double]$extent.GetAttribute('cy')
                if ($oldWidth -le 0 -or $oldHeight -le 0) {
                    continue
                }

                $targetWidth = $maximumWidth
                $targetHeight =
                    [long][Math]::Round($oldHeight * $targetWidth / $oldWidth)
                if ($targetHeight -gt $maximumHeight) {
                    $targetHeight = $maximumHeight
                    $targetWidth =
                        [long][Math]::Round(
                            $oldWidth * $targetHeight / $oldHeight)
                }
                $extent.SetAttribute('cx', [string]$targetWidth)
                $extent.SetAttribute('cy', [string]$targetHeight)
                $shapeExtent =
                    $inline.SelectSingleNode('.//a:xfrm/a:ext', $namespaces)
                if ($null -ne $shapeExtent) {
                    $shapeExtent.SetAttribute('cx', [string]$targetWidth)
                    $shapeExtent.SetAttribute('cy', [string]$targetHeight)
                }
                $resized++
            }
            $sectionImages.Clear()
        }

        $entryStream = $entry.Open()
        try {
            $entryStream.SetLength(0)
            $settings = New-Object System.Xml.XmlWriterSettings
            $settings.Encoding = New-Object System.Text.UTF8Encoding($false)
            $settings.Indent = $false
            $writer = [System.Xml.XmlWriter]::Create($entryStream, $settings)
            try {
                $xml.Save($writer)
            } finally {
                $writer.Dispose()
            }
        } finally {
            $entryStream.Dispose()
        }
    } finally {
        $archive.Dispose()
        $stream.Dispose()
    }

    Write-Output "Sized $resized DOCX image(s) to fit their A4 section."
}

function Build-DocumentSet {
    param(
        [Parameter(Mandatory)][string] $Root,
        [Parameter(Mandatory)]
        [ValidateSet('Technical', 'User')]
        [string] $DocumentSet,
        [Parameter(Mandatory)]
        [ValidateSet('All', 'Html', 'Docx', 'Pdf', 'None')]
        [string] $OutputFormat,
        [string] $DocxReference
    )

    $config = Read-DocumentationConfig -Root $Root
    $null = New-DocumentInventory -Root $Root -DocumentSet $DocumentSet
    $manual = Merge-Documentation -Root $Root -DocumentSet $DocumentSet
    if ($OutputFormat -eq 'None') {
        Write-Output "Created $manual"
        return
    }

    Assert-Command -Name 'pandoc'
    $build = Join-Path -Path $Root -ChildPath $config.buildDirectory
    $baseName = if ($DocumentSet -eq 'Technical') {
        $config.technicalOutputBaseName
    } else {
        $config.userOutputBaseName
    }
    $landscapeFilter = Join-Path -Path $Root -ChildPath $config.landscapeFilter
    $latexHeader = Join-Path -Path $Root -ChildPath $config.latexLandscapeHeader
    $baseArgs = @(
        $manual,
        '--from=markdown+yaml_metadata_block+pipe_tables+fenced_divs+link_attributes+raw_attribute',
        '--standalone',
        '--toc',
        '--toc-depth=3',
        '--number-sections',
        "--lua-filter=$landscapeFilter",
        "--resource-path=$build",
        '--metadata', "lang=$($config.language)",
        '--metadata', 'papersize=a4'
    )
    $formats = if ($OutputFormat -eq 'All') {
        @('Html', 'Docx', 'Pdf')
    } else {
        @($OutputFormat)
    }

    foreach ($item in $formats) {
        $output = Join-Path -Path $build -ChildPath (
            "$baseName.$($item.ToLowerInvariant())" -replace '\.docx$', '.docx')
        switch ($item) {
            'Html' {
                & pandoc @baseArgs --embed-resources --output $output
            }
            'Docx' {
                $reference = $DocxReference
                if ([string]::IsNullOrWhiteSpace($reference)) {
                    $candidate = Join-Path -Path $Root -ChildPath $config.referenceDoc
                    if (Test-Path -LiteralPath $candidate -PathType Leaf) {
                        $reference = $candidate
                    }
                }
                $arguments = @() + $baseArgs
                if (-not [string]::IsNullOrWhiteSpace($reference)) {
                    $arguments += '--reference-doc=' + (Resolve-Path -LiteralPath $reference).Path
                }
                & pandoc @arguments --output $output
                if ($LASTEXITCODE -eq 0) {
                    Set-DocxPageLayout -Path $output `
                        -PortraitWidthCm $config.portraitImageWidthCm `
                        -PortraitHeightCm $config.portraitImageHeightCm `
                        -LandscapeWidthCm $config.landscapeImageWidthCm `
                        -LandscapeHeightCm $config.landscapeImageHeightCm
                }
            }
            'Pdf' {
                Convert-SvgAssetsForPdf -Root $Root
                & pandoc @baseArgs "--include-in-header=$latexHeader" `
                    "--pdf-engine=$($config.pdfEngine)" --output $output
            }
        }
        if ($LASTEXITCODE -ne 0) {
            throw "Pandoc failed while building $DocumentSet $item output."
        }
        Write-Output "Created $output"
    }
}

if ([string]::IsNullOrWhiteSpace($ProjectRoot)) {
    $ProjectRoot = Resolve-ProjectRoot
} else {
    $ProjectRoot = (Resolve-Path -LiteralPath $ProjectRoot).Path
}

switch ($Action) {
    'Test' {
        Test-Documentation -Root $ProjectRoot -WarningsAreErrors:$FailOnWarning
    }
    'Clean' {
        $config = Read-DocumentationConfig -Root $ProjectRoot
        $buildPath = Join-Path -Path $ProjectRoot -ChildPath $config.buildDirectory
        if ((Test-Path -LiteralPath $buildPath) -and
            $PSCmdlet.ShouldProcess($buildPath, 'Remove generated manuals')) {
            Get-ChildItem -LiteralPath $buildPath -Force |
                Where-Object Name -ne '.gitkeep' |
                Remove-Item -Recurse -Force
        }
        $diagramPath = Join-Path -Path $ProjectRoot -ChildPath $config.diagramOutputDirectory
        if ((Test-Path -LiteralPath $diagramPath) -and
            $PSCmdlet.ShouldProcess($diagramPath, 'Remove intermediate diagram files')) {
            Get-ChildItem -LiteralPath $diagramPath -File |
                Where-Object {
                    $_.Name -ne '.gitkeep' -and $_.Extension -ne '.svg'
                } |
                Remove-Item -Force
        }
    }
    'Build' {
        if ($RenderDiagrams) {
            Invoke-PlantUmlRender -Root $ProjectRoot -OutputFormat 'svg'
        }
        Test-Documentation -Root $ProjectRoot -WarningsAreErrors:$FailOnWarning
        $sets = if ($Document -eq 'All') {
            @('Technical', 'User')
        } else {
            @($Document)
        }
        foreach ($set in $sets) {
            Build-DocumentSet -Root $ProjectRoot -DocumentSet $set `
                -OutputFormat $Format -DocxReference $ReferenceDoc
        }
    }
}
