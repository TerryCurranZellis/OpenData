<#
    Copyright © 2026 Terry Curran
    SPDX-License-Identifier: Apache-2.0

    Read-only audit for diagram-source placement, Markdown figure captions and
    Markdown-document links that must not be carried into generated documents.
#>

#Requires -Version 5.1

function check-documents {
[CmdletBinding()]
param(
    [string] $ProjectRoot = (Split-Path -Parent $PSScriptRoot),
    [string] $CsvPath,
    [switch] $IncludeTemplates
)

$ErrorActionPreference = 'Stop'

function New-Finding {
    param(
        [string] $Rule,
        [string] $Severity,
        [string] $File,
        [int] $Line,
        [string] $Message,
        [string] $Value
    )

    [pscustomobject]@{
        Rule = $Rule
        Severity = $Severity
        File = $File
        Line = $Line
        Message = $Message
        Value = $Value
    }
}

function Get-RelativePath {
    param([string] $BasePath, [string] $Path)

    $base = New-Object System.Uri(($BasePath.TrimEnd([IO.Path]::DirectorySeparatorChar) + [IO.Path]::DirectorySeparatorChar))
    $target = New-Object System.Uri($Path)
    return [Uri]::UnescapeDataString($base.MakeRelativeUri($target).ToString()).Replace('/', [IO.Path]::DirectorySeparatorChar)
}

$root = (Resolve-Path -LiteralPath $ProjectRoot).Path
$configPath = Join-Path $root 'config\documentation.json'
if (-not (Test-Path -LiteralPath $configPath -PathType Leaf)) {
    throw "Documentation configuration was not found: $configPath"
}

$config = Get-Content -LiteralPath $configPath -Raw -Encoding UTF8 | ConvertFrom-Json
$docsRoot = Join-Path $root ([string]$config.sourceDirectory)
$diagramRoot = (Join-Path $root ([string]$config.diagramSourceDirectory)).TrimEnd('\', '/')
$findings = New-Object 'System.Collections.Generic.List[object]'

# All operational PlantUML sources belong under the configured diagram source
# directory. Template examples are still reported so their disposition is an
# explicit decision rather than an accidental omission from conversion.
Get-ChildItem -LiteralPath $docsRoot -Recurse -File -Filter '*.puml' | ForEach-Object {
    $insideDiagramRoot = $_.FullName.StartsWith(
        $diagramRoot + [IO.Path]::DirectorySeparatorChar,
        [StringComparison]::OrdinalIgnoreCase
    )
    if (-not $insideDiagramRoot) {
        $findings.Add((New-Finding -Rule 'PUML_OUTSIDE_SOURCE_FOLDER' -Severity 'Warning' `
            -File (Get-RelativePath $root $_.FullName) -Line 0 `
            -Message 'PlantUML file is outside the configured diagram source directory.' `
            -Value ([string]$config.diagramSourceDirectory)))
    }
}

$markdownFiles = Get-ChildItem -LiteralPath $docsRoot -Recurse -File -Filter '*.md' |
    Where-Object {
        $relative = Get-RelativePath $docsRoot $_.FullName
        $IncludeTemplates -or ($relative -notmatch '^(?:_templates|templates)[\\/]')
    }

$imagePattern = '!\[(?<caption>[^\]]*)\]\((?<target>[^\s\)]+)(?<attributes>[^\)]*)\)'
$markdownLinkPattern = '(?<!!)\[(?<text>[^\]]+)\]\((?<target>[^\)\s]+\.md(?:#[^\)\s]+)?)(?:\s+[^\)]*)?\)'

foreach ($file in $markdownFiles) {
    $lines = @(Get-Content -LiteralPath $file.FullName -Encoding UTF8)
    for ($index = 0; $index -lt $lines.Count; $index++) {
        $line = [string]$lines[$index]
        $lineNumber = $index + 1

        foreach ($match in [regex]::Matches($line, $imagePattern, [Text.RegularExpressions.RegexOptions]::IgnoreCase)) {
            $caption = $match.Groups['caption'].Value.Trim()
            $target = $match.Groups['target'].Value.Trim()

            if ([string]::IsNullOrWhiteSpace($caption)) {
                $findings.Add((New-Finding -Rule 'FIGURE_CAPTION_MISSING' -Severity 'Error' `
                    -File (Get-RelativePath $root $file.FullName) -Line $lineNumber `
                    -Message 'Image has no caption text in its Markdown alt-text.' -Value $target))
            } elseif ($caption -match '^Figure\s+\d+\s*:') {
                $findings.Add((New-Finding -Rule 'FIGURE_NUMBER_HARDCODED' -Severity 'Warning' `
                    -File (Get-RelativePath $root $file.FullName) -Line $lineNumber `
                    -Message 'Do not hard-code Figure numbers; numbering must be generated per output document.' -Value $caption))
            }

            if ($target -match '\.md(?:#.*)?$') {
                $findings.Add((New-Finding -Rule 'IMAGE_TARGET_IS_MARKDOWN' -Severity 'Error' `
                    -File (Get-RelativePath $root $file.FullName) -Line $lineNumber `
                    -Message 'Image target is a Markdown document rather than an image.' -Value $target))
            }
        }

        foreach ($match in [regex]::Matches($line, $markdownLinkPattern, [Text.RegularExpressions.RegexOptions]::IgnoreCase)) {
            $findings.Add((New-Finding -Rule 'MARKDOWN_LINK_IN_OUTPUT_SOURCE' -Severity 'Warning' `
                -File (Get-RelativePath $root $file.FullName) -Line $lineNumber `
                -Message 'Generated documents must retain this label as text but remove the .md hyperlink.' `
                -Value $match.Groups['target'].Value))
        }
    }
}

$ordered = @($findings | Sort-Object Rule, File, Line)
$summary = @($ordered | Group-Object Rule | Sort-Object Name)

Write-Host ''
Write-Host 'OpenData documentation content audit'
Write-Host ('Project root: {0}' -f $root)
Write-Host ('Markdown files checked: {0}' -f @($markdownFiles).Count)
Write-Host ('Findings: {0}' -f $ordered.Count)
Write-Host ''

if ($summary.Count -gt 0) {
    $summary | Select-Object @{Name='Rule';Expression={$_.Name}}, Count | Format-Table -AutoSize
    $ordered | Format-Table Severity, Rule, File, Line, Value -AutoSize -Wrap
} else {
    Write-Host 'No documentation-content inconsistencies were found.' -ForegroundColor Green
}

if (-not [string]::IsNullOrWhiteSpace($CsvPath)) {
    $resolvedCsv = if ([IO.Path]::IsPathRooted($CsvPath)) { $CsvPath } else { Join-Path $root $CsvPath }
    $csvDirectory = Split-Path -Parent $resolvedCsv
    if (-not (Test-Path -LiteralPath $csvDirectory -PathType Container)) {
        $null = New-Item -ItemType Directory -Path $csvDirectory -Force
    }
    $ordered | Export-Csv -LiteralPath $resolvedCsv -NoTypeInformation -Encoding UTF8
    Write-Host ('CSV report: {0}' -f $resolvedCsv)
}
<#
if ($ordered.Count -gt 0) { exit 1 }
exit 0
#>
}
check-documents -ProjectRoot 'C:\Users\terry\Documents\NetBeansProjects\opendata' -CsvPath 'c:\Users\terry\Documents\NetBeansProjects\opendata\scripts\check.csv'

import-csv -path 'c:\Users\terry\Documents\NetBeansProjects\opendata\scripts\check.csv' | out-gridview