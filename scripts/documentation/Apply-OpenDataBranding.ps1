<#
Copyright © 2026 Terry Curran
SPDX-License-Identifier: Apache-2.0
#>

#Requires -Version 5.1
<#
.SYNOPSIS
Adds the OpenData branded cover page to the existing documentation builder.

.DESCRIPTION
This script patches scripts\Invoke-Documentation.ps1 in place. It is
idempotent, creates a timestamped backup, and preserves unrelated local edits.
Run it once from the project root after copying the overlay files.
#>
[CmdletBinding(SupportsShouldProcess)]
param(
    [Parameter(Mandatory = $false)]
    [string] $ProjectRoot = (Split-Path -Parent (Split-Path -Parent $PSScriptRoot))
)

$ErrorActionPreference = 'Stop'
$documentationScript = Join-Path $ProjectRoot 'scripts\Invoke-Documentation.ps1'
if (-not (Test-Path -LiteralPath $documentationScript -PathType Leaf)) {
    throw "Documentation builder was not found: $documentationScript"
}

$content = Get-Content -LiteralPath $documentationScript -Raw -Encoding UTF8
$marker = '# OPENDATA-BRANDED-COVER-PAGE'
if ($content.Contains($marker)) {
    Write-Output 'The branded documentation cover-page change is already installed.'
    return
}

$titlePattern = "'title: {0}' -f (ConvertTo-YamlSingleQuotedString -Value `$title)"
$pageTitleReplacement = "'pagetitle: {0}' -f (ConvertTo-YamlSingleQuotedString -Value `$title)"
if (-not $content.Contains($titlePattern)) {
    throw 'Unable to locate the Pandoc title metadata block. The documentation script may have changed.'
}
$content = $content.Replace($titlePattern, $pageTitleReplacement)

$anchor = @'
        $writer.WriteLine("lang: $($config.language)")
        $writer.WriteLine('---')
        $writer.WriteLine()
'@

$coverBlock = @'
        $writer.WriteLine("lang: $($config.language)")
        $writer.WriteLine('---')
        $writer.WriteLine()

        # OPENDATA-BRANDED-COVER-PAGE
        $coverSource = Join-Path -Path $Root -ChildPath $config.coverImage
        if (-not (Test-Path -LiteralPath $coverSource -PathType Leaf)) {
            throw "Documentation cover image was not found: $coverSource"
        }
        $coverFileName = [System.IO.Path]::GetFileName($coverSource)
        $coverTarget = Join-Path -Path $build -ChildPath $coverFileName
        Copy-Item -LiteralPath $coverSource -Destination $coverTarget -Force

        $writer.WriteLine("![$title]($coverFileName){ width=100% }")
        $writer.WriteLine()
        $writer.WriteLine("# $title")
        $writer.WriteLine()
        $writer.WriteLine("## $($config.slogan)")
        $writer.WriteLine()
        $writer.WriteLine("**$($config.author)**")
        $writer.WriteLine()
        $writer.WriteLine($documentDate)
        $writer.WriteLine()
        $writer.WriteLine('\newpage')
        $writer.WriteLine()
'@

if (-not $content.Contains($anchor)) {
    throw 'Unable to locate the documentation metadata anchor. No changes were written.'
}
$content = $content.Replace($anchor, $coverBlock)

$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$backup = "$documentationScript.$timestamp.bak"
if ($PSCmdlet.ShouldProcess($documentationScript, 'Add OpenData branded cover page')) {
    Copy-Item -LiteralPath $documentationScript -Destination $backup
    $encoding = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText($documentationScript, $content, $encoding)
    Write-Output "Updated $documentationScript"
    Write-Output "Backup created at $backup"
}
