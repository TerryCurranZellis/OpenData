<#
    Copyright © 2026 Terry Curran
    SPDX-License-Identifier: Apache-2.0
#>

#Requires -Version 5.1

<#
    [CmdletBinding(SupportsShouldProcess)]
    param(
    [ValidateSet('Build', 'Test', 'Clean')]
    [string] $Action = 'Build',

    [Parameter(Mandatory)]
    [string] $ProjectRoot,

    [ValidateSet('Technical', 'User', 'All')]
    [string] $Document = 'All',

    [ValidateSet('All', 'Html', 'Docx', 'Pdf', 'None')]
    [string] $Format = 'All',

    [AllowNull()]
    [string] $ReferenceDoc,

    [switch] $RenderDiagrams = $true,

    [switch] $FailOnWarning
    )
#>
function Invoke-Documentation {
  <#
      .SYNOPSIS
      Builds, validates or cleans OpenData documentation.

      .DESCRIPTION
      Builds separate technical documentation and user-guide outputs, validates all
      Markdown links, and renders canonical PlantUML sources from
      docs\diagrams\source into docs\diagrams\generated.

      .EXAMPLE
      Invoke-Documentation -Action Test

      .EXAMPLE
      Invoke-Documentation -Action Build -Document All -Format Docx -RenderDiagrams
  #>
  [CmdletBinding(SupportsShouldProcess)]
  param(
    [ValidateSet('Build', 'Test', 'Clean')]
    [string] $Action = 'Build',

    [Parameter(Mandatory)][string] $ProjectRoot,

    [ValidateSet('Technical', 'User', 'All')]
    [string] $Document = 'All',

    [ValidateSet('All', 'Html', 'Docx', 'Pdf', 'None')]
    [string] $Format = 'All',

    [AllowNull()]
    [string] $ReferenceDoc,

    [switch] $RenderDiagrams,

    [switch] $FailOnWarning
  )

  $ErrorActionPreference = 'Stop'

  #--------------------------------------------------------------------------------
  # Resolve-ProjectRoot
  #--------------------------------------------------------------------------------
  function Resolve-ProjectRoot {
    [CmdletBinding()]
    param(
      [string] $StartPath = $PSScriptRoot
    )

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

  #--------------------------------------------------------------------------------
  # Read-DocumentationConfig
  #--------------------------------------------------------------------------------
  function Read-DocumentationConfig {
    param([Parameter(Mandatory)][string] $Root)

    $path = Join-Path -Path $Root -ChildPath 'config\documentation.json'
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
      throw ('Documentation configuration was not found: {0}' -f $path)
    }
    Get-Content -LiteralPath $path -Raw -Encoding UTF8 | ConvertFrom-Json
  }

  #--------------------------------------------------------------------------------
  # Read-DocumentationManifest
  #--------------------------------------------------------------------------------
  function Read-DocumentationManifest {
    param([Parameter(Mandatory)][string] $Root)

    $config = Read-DocumentationConfig -Root $Root
    $relativePath = if ([string]::IsNullOrWhiteSpace($config.manifestPath)) {
      'docs\manifest.json'
    } else {
      $config.manifestPath
    }
    $path = Join-Path -Path $Root -ChildPath $relativePath
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
      throw ('Documentation manifest was not found: {0}' -f $path)
    }
    Get-Content -LiteralPath $path -Raw -Encoding UTF8 | ConvertFrom-Json
  }
#--------------------------------------------------------------------------------
# Convert-TemplateTokens
#--------------------------------------------------------------------------------
function Convert-TemplateTokens {
  param(
    [Parameter(Mandatory)]
    [string] $Content,

    [Parameter(Mandatory)]
    [hashtable] $Tokens
  )

  $result = $Content

  foreach ($key in $Tokens.Keys) {
    $value = [string]$Tokens[$key]

    # Construct the double-braced placeholder literally.
    $placeholder = '{{' + $key + '}}'

    $result = $result.Replace($placeholder, $value)
  }

  return $result
}

  #--------------------------------------------------------------------------------
  # Assert-Directory
  #--------------------------------------------------------------------------------
  function Assert-Directory {
    param([Parameter(Mandatory)][string] $Path)

    if (-not (Test-Path -LiteralPath $Path -PathType Container)) {
      $null = New-Item -ItemType Directory -Path $Path -Force
    }
  }

  #--------------------------------------------------------------------------------
  # Assert-Command
  #--------------------------------------------------------------------------------
  function Assert-Command {
    param([Parameter(Mandatory)][string] $Name)

    if (-not (Get-Command -Name $Name -ErrorAction SilentlyContinue)) {
      throw ("Required command '{0}' was not found." -f $Name)
    }
  }

  #--------------------------------------------------------------------------------
  # Convert-ManifestGlobToRegex
  #--------------------------------------------------------------------------------
  function Convert-ManifestGlobToRegex {
    param([Parameter(Mandatory)][string] $Pattern)

    $normalised = $Pattern.Replace('\', '/').TrimStart('/')
    $escaped = [regex]::Escape($normalised)
    $escaped = $escaped -replace '\\\*\\\*/', '(?:.*/)?'
    $escaped = $escaped -replace '\\\*\\\*', '.*'
    $escaped = $escaped -replace '\\\*', '[^/]*'
    $escaped = $escaped -replace '\\\?', '[^/]'
    return '^(?:{0})$' -f $escaped
  }

  #--------------------------------------------------------------------------------
  # Get-ManifestSourceMatches
  #--------------------------------------------------------------------------------
  function Get-ManifestSourceMatches {
    param(
      [Parameter(Mandatory)][string] $DocsRoot,
      [Parameter(Mandatory)][string] $Entry
    )

    $normalisedEntry = $Entry.Replace('\', '/').TrimStart('/')
    $containsWildcard =
    [System.Management.Automation.WildcardPattern]::ContainsWildcardCharacters(
    $normalisedEntry)

    if (-not $containsWildcard) {
      $exactPath = Join-Path -Path $DocsRoot -ChildPath (
      $normalisedEntry -replace '/', '\')
      if (Test-Path -LiteralPath $exactPath -PathType Leaf) {
        return ,(Get-Item -LiteralPath $exactPath)
      }
      return @()
    }

    $regex = Convert-ManifestGlobToRegex -Pattern $normalisedEntry
    return @(
      Get-ChildItem -LiteralPath $DocsRoot -File -Recurse |
      Where-Object {
        $relative = $_.FullName.Substring($DocsRoot.Length)
        $relative = $relative.TrimStart('\', '/').Replace('\', '/')
        $relative -match $regex
      } |
      Sort-Object -Property FullName
    )
  }

  #--------------------------------------------------------------------------------
  # Get-DocumentationFiles
  #--------------------------------------------------------------------------------
  function Get-DocumentationFiles {
    param(
      [Parameter(Mandatory)][string] $Root,
      [Parameter(Mandatory)]
      [ValidateSet('Technical', 'User')]
      [string] $DocumentSet
    )

    $manifest = Read-DocumentationManifest -Root $Root
    $manualName = $DocumentSet.ToLowerInvariant()
    $manual = $manifest.manuals.$manualName
    if ($null -eq $manual) {
      throw ('Manual definition is missing from docs\manifest.json: {0}' -f $manualName)
    }

    $docsRoot = Join-Path -Path $Root -ChildPath 'docs'
    $files = New-Object -TypeName 'System.Collections.Generic.List[System.IO.FileInfo]'
    $seen = New-Object -TypeName 'System.Collections.Generic.HashSet[string]' -ArgumentList ([StringComparer]::OrdinalIgnoreCase)

    foreach ($entry in @($manual.sources)) {
      if ([string]::IsNullOrWhiteSpace([string]$entry)) {
        continue
      }

      $matchedFiles = @(Get-ManifestSourceMatches `
        -DocsRoot $docsRoot `
      -Entry ([string]$entry))
      if ($matchedFiles.Count -eq 0) {
        throw ('Manifest source or pattern matched no files: docs/{0}' -f $entry)
      }

      foreach ($matchedFile in $matchedFiles) {
        if ($seen.Add($matchedFile.FullName)) {
          $files.Add($matchedFile)
        }
      }
    }
    return $files
  }

  #--------------------------------------------------------------------------------
  # Get-ManualDefinition
  #--------------------------------------------------------------------------------
  function Get-ManualDefinition {
    param(
      [Parameter(Mandatory)][string] $Root,
      [Parameter(Mandatory)][ValidateSet('Technical', 'User')][string] $DocumentSet
    )

    $manifest = Read-DocumentationManifest -Root $Root
    $manual = $manifest.manuals.($DocumentSet.ToLowerInvariant())
    if ($null -eq $manual) {
      throw ('Manual definition is missing: {0}' -f $DocumentSet)
    }
    return $manual
  }

  #--------------------------------------------------------------------------------
  # Remove-DocumentHeader
  #--------------------------------------------------------------------------------
  function Remove-DocumentHeader {
    param(
      [Parameter(Mandatory)]
      [string] $Content
    )

    $result = $Content -replace '(?ms)^---\s*\r?\n.*?\r?\n---\s*\r?\n', ''
    $result = $result -replace '(?ms)^\*\*Document ID:\*\*.*?\r?\n---\s*\r?\n', ''
    return $result.Trim()
  }

  #--------------------------------------------------------------------------------
  # ConvertTo-YamlSingleQuotedString
  #--------------------------------------------------------------------------------
  function ConvertTo-YamlSingleQuotedString {
    param(
      [Parameter(Mandatory)]
      [AllowEmptyString()]
      [string] $Value
    )

    return "'" + $Value.Replace("'", "''") + "'"
  }

  #--------------------------------------------------------------------------------
  # Test-TrailingLandscapeBlock 
  #--------------------------------------------------------------------------------
  function Test-TrailingLandscapeBlock {
    param(
      [Parameter(Mandatory)]
      [string] $Content
    )

    return $Content -match '(?ms):::\s*\{\.landscape\}.*?:::\s*$'
  }

  #--------------------------------------------------------------------------------
  # New-DocumentationTokens
  #--------------------------------------------------------------------------------
  function New-DocumentationTokens {
    param(
      [Parameter(Mandatory)] $Config,
      [Parameter(Mandatory)][string] $Title,
      [Parameter(Mandatory)][string] $DocumentSet,
      [Parameter(Mandatory)][string] $DocumentDate
    )

    return @{
      title = $Title
      projectTitle = [string]$Config.projectTitle
      slogan = [string]$Config.slogan
      author = [string]$Config.author
      version = [string]$Config.projectVersion
      date = $DocumentDate
      coverImage = [string]$Config.coverImage
      documentSet = $DocumentSet
    }
  }

  #--------------------------------------------------------------------------------
  # Assert-NoUnresolvedTokens
  #--------------------------------------------------------------------------------
  function Assert-NoUnresolvedTokens {
    param(
      [Parameter(Mandatory)][string] $Content,
      [Parameter(Mandatory)][string] $SourceName
    )

    $unresolved = @([regex]::Matches($Content, '\{\{[A-Za-z][A-Za-z0-9_.-]*\}\}') |
      ForEach-Object Value |
    Sort-Object -Unique)
    if ($unresolved.Count -gt 0) {
      throw ('Unresolved documentation token(s) in {0}: {1}' -f
      $SourceName, ($unresolved -join ', '))
    }
  }

  #--------------------------------------------------------------------------------
  # Get-PandocResourcePath
  #--------------------------------------------------------------------------------
  function Get-PandocResourcePath {
    param(
      [Parameter(Mandatory)][string] $Root,
      [Parameter(Mandatory)][string] $Build,
      [Parameter(Mandatory)][System.IO.FileInfo[]] $SourceFiles
    )

    $paths = New-Object -TypeName 'System.Collections.Generic.List[string]'
    $seenPaths = New-Object -TypeName 'System.Collections.Generic.HashSet[string]' `
    -ArgumentList ([StringComparer]::OrdinalIgnoreCase)

    foreach ($candidate in @($Build, $Root, (Join-Path -Path $Root -ChildPath 'docs'))) {
      $fullPath = [IO.Path]::GetFullPath($candidate)
      if ($seenPaths.Add($fullPath)) {
        $paths.Add($fullPath)
      }
    }

    foreach ($file in $SourceFiles) {
      if ($null -ne $file -and $seenPaths.Add($file.DirectoryName)) {
        $paths.Add($file.DirectoryName)
      }
    }

    return ($paths -join [IO.Path]::PathSeparator)
  }

  #--------------------------------------------------------------------------------
  # New-DocumentInventory
  #--------------------------------------------------------------------------------
  function New-DocumentInventory {
    param(
      [Parameter(Mandatory)][string] $Root,
      [Parameter(Mandatory)]
      [ValidateSet('Technical', 'User')]
      [string] $DocumentSet
    )

    $config = Read-DocumentationConfig -Root $Root
    $build = Join-Path -Path $Root -ChildPath $config.buildDirectory
    Assert-Directory -Path $build
    $name = '{0}-document-inventory.md' -f $DocumentSet.ToLowerInvariant()
    $output = Join-Path -Path $build -ChildPath $name
    $lines = New-Object -TypeName 'System.Collections.Generic.List[string]'
    $lines.Add(('# {0} Document Source Inventory' -f $DocumentSet))
    $lines.Add('')
    $lines.Add('> This file is only an inventory of source Markdown files. It is not the assembled manual.')
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
      $lines.Add(('| [{0}](../../{1}) | {2} |' -f $file.Name, $relative, $heading))
    }
    $lines | Set-Content -LiteralPath $output -Encoding UTF8
    return $output
  }

  #--------------------------------------------------------------------------------
  # Merge-Documentation
  #--------------------------------------------------------------------------------
  function Merge-Documentation {
    param(
      [Parameter(Mandatory)][string] $Root,
      [Parameter(Mandatory)]
      [ValidateSet('Technical', 'User')]
      [string] $DocumentSet
    )

    $config = Read-DocumentationConfig -Root $Root
    $build = Join-Path -Path $Root -ChildPath $config.buildDirectory
    Assert-Directory -Path $build
    $manualDefinition = Get-ManualDefinition -Root $Root -DocumentSet $DocumentSet
    $baseName = if ([string]::IsNullOrWhiteSpace($manualDefinition.outputBaseName)) {
      if ($DocumentSet -eq 'Technical') { $config.technicalOutputBaseName } else { $config.userOutputBaseName }
    } else {
      $manualDefinition.outputBaseName
    }
    $title = if ([string]::IsNullOrWhiteSpace($manualDefinition.title)) {
      if ($DocumentSet -eq 'Technical') { $config.manualTitle } else { $config.userGuideTitle }
    } else {
      $manualDefinition.title
    }
    $output = Join-Path -Path $build -ChildPath ('{0}.md' -f $baseName)
    $encoding = New-Object -TypeName System.Text.UTF8Encoding -ArgumentList ($false)
    $writer = New-Object -TypeName System.IO.StreamWriter -ArgumentList ($output, $false, $encoding)
    try {
      $documentDate = (Get-Date).ToString(
        $config.dateFormat,
      [cultureinfo]::InvariantCulture)
      $writer.WriteLine('---')
      $writer.WriteLine(
      'title: {0}' -f (ConvertTo-YamlSingleQuotedString -Value $title))
      $writer.WriteLine(
        'author: {0}' -f (
      ConvertTo-YamlSingleQuotedString -Value $config.author))
      $writer.WriteLine(
        'date: {0}' -f (
      ConvertTo-YamlSingleQuotedString -Value $documentDate))
      $writer.WriteLine(('lang: {0}' -f $config.language))
      $writer.WriteLine('---')
      $writer.WriteLine()

      $tokens = New-DocumentationTokens `
      -Config $config `
      -Title $title `
      -DocumentSet $DocumentSet `
      -DocumentDate $documentDate

      if (-not [string]::IsNullOrWhiteSpace($manualDefinition.coverTemplate)) {
        $coverPath = Join-Path -Path (Join-Path -Path $Root -ChildPath 'docs') `
        -ChildPath ($manualDefinition.coverTemplate -replace '/', '\')
        if (-not (Test-Path -LiteralPath $coverPath -PathType Leaf)) {
          throw ('Cover template was not found: {0}' -f $coverPath)
        }
        $cover = Get-Content -LiteralPath $coverPath -Raw -Encoding UTF8
        $cover = Convert-TemplateTokens -Content $cover -Tokens $tokens
        Assert-NoUnresolvedTokens -Content $cover -SourceName $coverPath
        $writer.WriteLine($cover.Trim())
        $writer.WriteLine()
      }

      $files = @(Get-DocumentationFiles -Root $Root -DocumentSet $DocumentSet)
      for ($index = 0; $index -lt $files.Count; $index++) {
        $file = $files[$index]
        $content = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8
        $content = Remove-DocumentHeader -Content $content
        $content = Convert-TemplateTokens -Content $content -Tokens $tokens
        Assert-NoUnresolvedTokens -Content $content -SourceName $file.FullName
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
    } finally {
      $writer.Dispose()
    }
    return $output
  }
  #--------------------------------------------------------------------------------
  # Invoke-PlantUmlRender
  #--------------------------------------------------------------------------------
  function Invoke-PlantUmlRender {
    param(
      [Parameter(Mandatory)][string] $Root,
      [Parameter(Mandatory)][ValidateSet('svg', 'png')][string] $OutputFormat
    )

    $renderer = Join-Path -Path $Root -ChildPath 'scripts\Convert-PlantUml.ps1'
    if (-not (Test-Path -LiteralPath $renderer -PathType Leaf)) {
      throw ('PlantUML renderer was not found: {0}' -f $renderer)
    }
    & $renderer -ProjectRoot $Root -Format $OutputFormat -Clean
    if (-not $?) {
      throw 'PlantUML rendering failed.'
    }
  }
  #--------------------------------------------------------------------------------
  # Test-Documentation
  #--------------------------------------------------------------------------------
  function Test-Documentation {
    param(
      [Parameter(Mandatory)][string] $Root,
      [switch] $WarningsAreErrors
    )

    $issues = New-Object -TypeName 'System.Collections.Generic.List[object]'
    $docsRoot = Join-Path -Path $Root -ChildPath 'docs'
    $config = Read-DocumentationConfig -Root $Root
    try {
      $manifest = Read-DocumentationManifest -Root $Root
      foreach ($set in @('Technical', 'User')) {
        $null = @(Get-DocumentationFiles -Root $Root -DocumentSet $set)
      }
    } catch {
      $issues.Add([pscustomobject]@{
          Severity = 'Error'
          File = (Join-Path -Path $Root -ChildPath $config.manifestPath)
          Message = $_.Exception.Message
      })
    }
    $buildRoot = [IO.Path]::GetFullPath(
    (Join-Path -Path $Root -ChildPath $config.buildDirectory))
    $files = @(Get-ChildItem -LiteralPath $docsRoot -File -Recurse -Filter '*.md' |
      Where-Object {
        -not $_.FullName.StartsWith(
          $buildRoot,
        [StringComparison]::OrdinalIgnoreCase)
    })

    foreach ($file in $files) {
      $content = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8
      if ($content -notmatch '(?m)^#\s+\S') {
        $issues.Add([pscustomobject]@{
            Severity = 'Error'
            File = $file.FullName
            Message = 'Missing level-one heading.'
        })
      }
      if ($content -match "`t") {
        $issues.Add([pscustomobject]@{
            Severity = 'Warning'
            File = $file.FullName
            Message = 'Tab character found.'
        })
      }

      foreach ($match in [regex]::Matches($content, '!?\[[^\]]*\]\(([^)#]+)(?:#[^)]+)?\)')) {
        $target = $match.Groups[1].Value.Trim('<', '>')
        if ($target -match '^(https?:|mailto:|#)') {
          continue
        }
        # Template placeholders such as {{diagramPath}} are resolved before
        # publication and are not filesystem paths during validation.
        if ($target -match '\{\{[^}]+\}\}') {
          continue
        }
        $decoded = [uri]::UnescapeDataString($target)
        $linkPath = [IO.Path]::GetFullPath(
        (Join-Path -Path $file.DirectoryName -ChildPath ($decoded -replace '/', '\')))
        if (Test-Path -LiteralPath $linkPath) {
          continue
        }

        $issues.Add([pscustomobject]@{
            Severity = 'Error'
            File = $file.FullName
            Message = ('Broken relative link: {0}' -f $target)
        })
      }
    }

    $legacyPuml = @(Get-ChildItem -LiteralPath (Join-Path -Path $Root -ChildPath 'docs\diagrams') `
      -File -Recurse -Filter '*.puml' |
    Where-Object { $_.DirectoryName -ne (Join-Path -Path $Root -ChildPath 'docs\diagrams\source') })
    foreach ($file in $legacyPuml) {
      $issues.Add([pscustomobject]@{
          Severity = 'Error'
          File = $file.FullName
          Message = 'PlantUML source is outside docs\diagrams\source.'
      })
    }

    if ($issues.Count -gt 0) {
      $issues | Format-Table -AutoSize | Out-String | Write-Output
    }
    $errors = @($issues | Where-Object Severity -eq 'Error').Count
    $warnings = @($issues | Where-Object Severity -eq 'Warning').Count
    Write-Output -InputObject ('Documentation validation completed: {0} error(s), {1} warning(s).' -f $errors, $warnings)
    if ($errors -gt 0 -or ($WarningsAreErrors -and $warnings -gt 0)) {
      throw 'Documentation validation failed.'
    }
  }
  #--------------------------------------------------------------------------------
  # Convert-SvgAssetsForPdf
  #--------------------------------------------------------------------------------
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
      $pdf = Join-Path -Path $diagramDirectory -ChildPath ('{0}.pdf' -f $svg.BaseName)
      if ((Test-Path -LiteralPath $pdf -PathType Leaf) -and
      (Get-Item -LiteralPath $pdf).LastWriteTimeUtc -ge $svg.LastWriteTimeUtc) {
        continue
      }

      if ($null -ne $rsvg) {
        & $rsvg.Path -f pdf -o $pdf $svg.FullName
      } else {
        & $inkscape.Path $svg.FullName --export-type=pdf `
        ('--export-filename={0}' -f $pdf) --export-area-drawing
      }
      if ($LASTEXITCODE -ne 0 -or
        -not (Test-Path -LiteralPath $pdf -PathType Leaf) -or
      (Get-Item -LiteralPath $pdf).Length -eq 0) {
        throw ('Unable to create the PDF diagram asset: {0}' -f $pdf)
      }
    }
  }
  #--------------------------------------------------------------------------------
  # Set-DocxPageLayout
  #--------------------------------------------------------------------------------
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

    $wordNamespace = 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'
    $drawingNamespace = 'http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'
    $drawingMlNamespace = 'http://schemas.openxmlformats.org/drawingml/2006/main'
    $stream = [IO.File]::Open(
      $Path,
      [IO.FileMode]::Open,
      [IO.FileAccess]::ReadWrite,
    [IO.FileShare]::None)
    $archive = New-Object -TypeName System.IO.Compression.ZipArchive -ArgumentList (
      $stream,
      [IO.Compression.ZipArchiveMode]::Update,
    $false)

    try {
      $entry = $archive.GetEntry('word/document.xml')
      if ($null -eq $entry) {
        throw ('The DOCX package does not contain word/document.xml: {0}' -f $Path)
      }

      $entryStream = $entry.Open()
      try {
        $reader = New-Object -TypeName System.IO.StreamReader -ArgumentList (
          $entryStream,
          [Text.Encoding]::UTF8,
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

      $xml = New-Object -TypeName System.Xml.XmlDocument
      $xml.PreserveWhitespace = $true
      $xml.LoadXml($documentXml)
      $namespaces = New-Object -TypeName System.Xml.XmlNamespaceManager -ArgumentList ($xml.NameTable)
      $namespaces.AddNamespace('w', $wordNamespace)
      $namespaces.AddNamespace('wp', $drawingNamespace)
      $namespaces.AddNamespace('a', $drawingMlNamespace)
      $body = $xml.SelectSingleNode('/w:document/w:body', $namespaces)
      if ($null -eq $body) {
        throw ('The DOCX document body could not be read: {0}' -f $Path)
      }

      $sectionImages =
      New-Object -TypeName 'System.Collections.Generic.List[System.Xml.XmlElement]'
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
        $settings = New-Object -TypeName System.Xml.XmlWriterSettings
        $settings.Encoding = New-Object -TypeName System.Text.UTF8Encoding -ArgumentList ($false)
        $settings.Indent = $false
        $writer = [Xml.XmlWriter]::Create($entryStream, $settings)
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

    Write-Output -InputObject ('Sized {0} DOCX image(s) to fit their A4 section.' -f $resized)
  }
  #--------------------------------------------------------------------------------
  # Publish-DocumentSet
  #--------------------------------------------------------------------------------
  function Publish-DocumentSet {
    param(
      [Parameter(Mandatory)][string] $Root,
      [Parameter(Mandatory)]
      [ValidateSet('Technical', 'User')]
      [string] $DocumentSet,
      [Parameter(Mandatory)]
      [ValidateSet('All', 'Html', 'Docx', 'Pdf', 'None')]
      [string] $OutputFormat,
      [AllowNull()]
      [string] $DocxReference
    )

    $config = Read-DocumentationConfig -Root $Root
    $inventory = New-DocumentInventory -Root $Root -DocumentSet $DocumentSet
    Write-Output -InputObject ('Created source inventory {0}' -f $inventory)
    $manual = Merge-Documentation -Root $Root -DocumentSet $DocumentSet
    if ($OutputFormat -eq 'None') {
      Write-Output -InputObject ('Created {0}' -f $manual)
      return
    }

    Assert-Command -Name 'pandoc'
    $build = Join-Path -Path $Root -ChildPath $config.buildDirectory
    $manualDefinition = Get-ManualDefinition -Root $Root -DocumentSet $DocumentSet
    $baseName = if ([string]::IsNullOrWhiteSpace([string]$manualDefinition.outputBaseName)) {
      if ($DocumentSet -eq 'Technical') {
        $config.technicalOutputBaseName
      } else {
        $config.userOutputBaseName
      }
    } else {
      [string]$manualDefinition.outputBaseName
    }
    $sourceFiles = @(Get-DocumentationFiles -Root $Root -DocumentSet $DocumentSet)
    $resourcePath = Get-PandocResourcePath -Root $Root -Build $build -SourceFiles $sourceFiles
    $landscapeFilter = Join-Path -Path $Root -ChildPath $config.landscapeFilter
    $latexHeader = Join-Path -Path $Root -ChildPath $config.latexLandscapeHeader
    $baseArgs = @(
      $manual,
      '--from=markdown+yaml_metadata_block+pipe_tables+fenced_divs+link_attributes+raw_attribute',
      '--standalone',
      '--toc',
      '--toc-depth=3',
      '--number-sections',
      ('--lua-filter={0}' -f $landscapeFilter),
      ('--resource-path={0}' -f $resourcePath),
      '--metadata', ('lang={0}' -f $config.language),
      '--metadata', 'papersize=a4'
    )
    $formats = if ($OutputFormat -eq 'All') {
      @('Html', 'Docx', 'Pdf')
    } else {
      @($OutputFormat)
    }

    foreach ($item in $formats) {
      $output = Join-Path -Path $build -ChildPath (
      ('{0}.{1}' -f $baseName, $item.ToLowerInvariant()) -replace '\.docx$', '.docx')
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
            $parameters = @{
              Path = $output 
              PortraitWidthCm = $config.portraitImageWidthCm 
              PortraitHeightCm = $config.portraitImageHeightCm 
              LandscapeWidthCm = $config.landscapeImageWidthCm 
              LandscapeHeightCm = $config.landscapeImageHeightCm
            }
            Set-DocxPageLayout @parameters
          }
        }
        'Pdf' {
          Convert-SvgAssetsForPdf -Root $Root
          & pandoc @baseArgs ('--include-in-header={0}' -f $latexHeader) `
          ('--pdf-engine={0}' -f $config.pdfEngine) --output $output
        }
      }
      if ($LASTEXITCODE -ne 0) {
        throw ('Pandoc failed while building {0} {1} output.' -f $DocumentSet, $item)
      }
      Write-Output -InputObject ('Created {0}' -f $output)
    }
  }
  #--------------------------------------------------------------------------------
  # main process here
  #--------------------------------------------------------------------------------
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
        $Parameters = @{
          Root = $ProjectRoot 
          DocumentSet = $set 
          OutputFormat = $Format 
          DocxReference = $ReferenceDoc
        }
        Publish-DocumentSet @parameters
      }
    }
  }
}

$Parameters = @{
  Action = 'build'
  ProjectRoot = 'C:\Users\terry\Documents\NetBeansProjects\opendata'
  Document = 'All'
  Format = 'All'
  ReferenceDoc = 'C:\Users\terry\Downloads\Corporate_Document_Template.docx'
  RenderDiagrams = $true
  FailOnWarning = $true
}
Invoke-Documentation @Parameters
