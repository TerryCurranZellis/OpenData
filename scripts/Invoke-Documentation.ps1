<#
    Copyright © 2026 Terry Curran
    SPDX-License-Identifier: Apache-2.0
#>

#Requires -Version 5.1

function Invoke-Documentation {
  <#
      .SYNOPSIS
      Builds, validates or cleans manifest-driven OpenData documentation.

      .DESCRIPTION
      Discovers document manifests from the configured manifest directory. The
      engine contains no document-specific names or source lists.

      .PARAMETER Action
      Build, Test, Clean, ALL

      .PARAMETER ProjectRoot
      Top level of project

      .PARAMETER Document
      Currently all which means build everything

      .PARAMETER Format
      Docs, PDF, HTML

      .PARAMETER ReferenceDoc
      A word document to use as a formatting template

      .PARAMETER RenderDiagrams
      Create at the diagrams by converting the .PUML into .SVG files

      .PARAMETER FailOnWarning
      When testing if the are any warnings then stop

  #>

  [CmdletBinding(SupportsShouldProcess)]
  param(
    [ValidateSet('Build', 'Test', 'Clean', 'All')]
    [string] $Action = 'Build',

    [Parameter(Mandatory=$true)]
    [string] $ProjectRoot,

    [string[]] $Document = @('All'),

    [ValidateSet('All', 'Html', 'Docx', 'Pdf', 'None')]
    [string] $Format = 'All',

    [AllowNull()]
    [string] $ReferenceDoc,

    [switch] $RenderDiagrams,

    [switch] $FailOnWarning
  )

  $ErrorActionPreference = 'Stop'
  
  #-------------------------------------------------------------------------------
  # Resolve-ProjectRoot
  #-------------------------------------------------------------------------------
  function Resolve-ProjectRoot {
    <#
        .SYNOPSIS
        Finds the project root if it was not supplied
    #>
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
  #-------------------------------------------------------------------------------
  # Read-JsonFile
  #-------------------------------------------------------------------------------
  function Read-JsonFile {
    param(
      [Parameter(Mandatory)]
      [string] $Path
    )

    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) {
      throw ('JSON file was not found: {0}' -f $Path)
    }
    try {
      return Get-Content -LiteralPath $Path -Raw -Encoding UTF8 | ConvertFrom-Json
    } catch {
      throw ('Invalid JSON in {0}: {1}' -f $Path, $_.Exception.Message)
    }
  }
  #-------------------------------------------------------------------------------
  # Read-DocumentConfig
  #-------------------------------------------------------------------------------
  function Read-DocumentationConfig {
    param(
      [Parameter(Mandatory)]
      [string] $Root
    )

    return Read-JsonFile -Path (Join-Path -Path $Root -ChildPath 'config\documentation.json')
  }

  #-------------------------------------------------------------------------------
  # Get-ObjectProperty
  #-------------------------------------------------------------------------------
  function Get-ObjectProperty {
    param(
      [AllowNull()] 
      [PSCustomObject] $Object,
      [Parameter(Mandatory)]
      [string] $Name,
      [AllowNull()] 
      $DefaultValue
    )

    if ($null -eq $Object) {
      return $DefaultValue
    }
    $property = $Object.PSObject.Properties[$Name]
    if ($null -eq $property -or $null -eq $property.Value) {
      return $DefaultValue
    }
    return $property.Value
  }

  #-------------------------------------------------------------------------------
  # ConvertTo-Hashtable
  #-------------------------------------------------------------------------------
  function ConvertTo-Hashtable {
    [CmdletBinding()]
    param(
      [AllowNull()] 
      [PSCustomObject] $Object
    )

    $result = @{}
    if ($null -ne $Object) {
      foreach ($property in $Object.PSObject.Properties) {
        $result[$property.Name] = $property.Value
      }
    }
    return $result
  }
  #-------------------------------------------------------------------------------
  # Get-ManifestSetting
  #-------------------------------------------------------------------------------
  function Get-ManifestSetting {
    param(
      [Parameter(Mandatory)] 
      [PSCustomObject] $Manifest,
      [AllowNull()] 
      [PSCustomObject] $Defaults,
      [Parameter(Mandatory)]
      [string] $Name,
      [AllowNull()] 
      $Fallback
    )

    $manifestProperty = $Manifest.PSObject.Properties[$Name]
    if ($null -ne $manifestProperty -and $null -ne $manifestProperty.Value) {
      return $manifestProperty.Value
    }
    if ($null -ne $Defaults) {
      $defaultProperty = $Defaults.PSObject.Properties[$Name]
      if ($null -ne $defaultProperty -and $null -ne $defaultProperty.Value) {
        return $defaultProperty.Value
      }
    }
    return $Fallback
  }
  #-------------------------------------------------------------------------------
  # Assert-Directory
  #-------------------------------------------------------------------------------
  function Assert-Directory {
    param(
      [Parameter(Mandatory)]
      [string] $Path
    )

    if (-not (Test-Path -LiteralPath $Path -PathType Container)) {
      $null = New-Item -ItemType Directory -Path $Path -Force
    }
  }
  #-------------------------------------------------------------------------------
  # Assert-Command
  #-------------------------------------------------------------------------------
  function Assert-Command {
    param(
      [Parameter(Mandatory)]
      [string] $Name
    )

    if (-not (Get-Command -Name $Name -ErrorAction SilentlyContinue)) {
      throw ("Required command '{0}' was not found." -f $Name)
    }
  }
  #--------------------------------------------------------------------------------
  # Sync-NoticeFiles
  #--------------------------------------------------------------------------------
  function Sync-NoticeFiles {
    param(
      [Parameter(Mandatory)]
      [string] $Root
    )
    $sources = @(
      Join-Path -Path $Root -ChildPath 'THIRD-PARTY-NOTICES.md'
      Join-Path -Path $Root -ChildPath 'DATA-SOURCE-NOTICES.md'
    )
    $destinationDirectory = Join-Path -Path $Root -ChildPath 'docs\shared'
    Assert-Directory -Path $destinationDirectory
    # Validate every source before copying anything.
    foreach ($source in $sources) {
      if (-not (Test-Path -LiteralPath $source -PathType Leaf)) {
        throw ('Notices file was not found: {0}' -f $source)
      }
    }
    foreach ($source in $sources) {
      $destinationFileName = (Split-Path -Path $source -Leaf).ToLowerInvariant()
      $destination = Join-Path -Path $destinationDirectory -ChildPath $destinationFileName
      Copy-Item -LiteralPath $source -Destination $destination -Force
      Write-Output -InputObject ('Synchronised notice: {0}' -f $destination)
    }
  }
  #-------------------------------------------------------------------------------
  # ConvertTo-NormalisedManifest
  #-------------------------------------------------------------------------------
  function ConvertTo-NormalisedManifest {
    param(
      [Parameter(Mandatory)]
      [System.IO.FileInfo] $ManifestFile,
      [Parameter(Mandatory)] 
      [PSCustomObject] $Config
    )

    $raw = Read-JsonFile -Path $ManifestFile.FullName
    $defaults = Get-ObjectProperty -Object $Config -Name 'defaultDocument' -DefaultValue $null
    $id = [string](Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'id' -Fallback $ManifestFile.BaseName)
    $title = [string](Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'title' -Fallback '')
    $output = [string](Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'output' -Fallback '')
    $sections = @(Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'sections' -Fallback @())

    if ([string]::IsNullOrWhiteSpace($id)) {
      throw ('Manifest id is missing: {0}' -f $ManifestFile.FullName)
    }
    if ([string]::IsNullOrWhiteSpace($title)) {
      throw ('Manifest title is missing: {0}' -f $ManifestFile.FullName)
    }
    if ([string]::IsNullOrWhiteSpace($output)) {
      throw ('Manifest output is missing: {0}' -f $ManifestFile.FullName)
    }
    if ($sections.Count -eq 0) {
      throw ('Manifest sections are missing: {0}' -f $ManifestFile.FullName)
    }
    if ([IO.Path]::IsPathRooted($output) -or $output -match '[\\/]') {
      throw ('Manifest output must be a filename, not a path: {0}' -f $output)
    }

    $metadata = ConvertTo-Hashtable -Object (Get-ObjectProperty -Object $Config -Name 'documentMetadata' -DefaultValue $null)
    $manifestMetadata = ConvertTo-Hashtable -Object (Get-ObjectProperty -Object $raw -Name 'metadata' -DefaultValue $null)
    foreach ($key in $manifestMetadata.Keys) {
      $metadata[$key] = $manifestMetadata[$key]
    }

    return [pscustomobject]@{
      Id = $id
      Order = [int](Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'order' -Fallback 100)
      Title = $title
      Output = $output
      Template = [string](Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'template' -Fallback '')
      CoverPage = [string](Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'coverPage' -Fallback '')
      CopyrightPage = [string](Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'copyright' -Fallback '')
      RevisionHistory = [string](Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'revisionHistory' -Fallback '')
      GenerateToc = [bool](Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'generateToc' -Fallback $true)
      TocDepth = [int](Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'tocDepth' -Fallback 3)
      NumberHeadings = [bool](Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'numberHeadings' -Fallback $true)
      IncludeInventory = [bool](Get-ManifestSetting -Manifest $raw -Defaults $defaults -Name 'includeInventory' -Fallback $true)
      Sections = $sections
      Metadata = $metadata
      SourcePath = $ManifestFile.FullName
      SourceName = $ManifestFile.Name
    }
  }
  #-------------------------------------------------------------------------------
  # Get-DocumentManifests
  #-------------------------------------------------------------------------------
  function Get-DocumentationManifests {
    param(
      [Parameter(Mandatory)]
      [string] $Root
    )

    $config = Read-DocumentationConfig -Root $Root
    $directorySetting = [string](Get-ObjectProperty -Object $config -Name 'manifestDirectory' -DefaultValue 'docs\manifests')
    $pattern = [string](Get-ObjectProperty -Object $config -Name 'manifestPattern' -DefaultValue '*.json')
    $directory = Join-Path -Path $Root -ChildPath $directorySetting
    if (-not (Test-Path -LiteralPath $directory -PathType Container)) {
      throw ('Documentation manifest directory was not found: {0}' -f $directory)
    }

    $files = @(Get-ChildItem -LiteralPath $directory -File -Filter $pattern | Sort-Object -Property Name)
    if ($files.Count -eq 0) {
      throw ('No document manifests matched {0} in {1}.' -f $pattern, $directory)
    }

    $manifests = @(foreach ($file in $files) {
        ConvertTo-NormalisedManifest -ManifestFile $file -Config $config
    })

    $duplicateIds = @($manifests | Group-Object -Property Id | Where-Object Count -gt 1)
    if ($duplicateIds.Count -gt 0) {
      throw ('Duplicate manifest id(s): {0}' -f (($duplicateIds.Name | Sort-Object) -join ', '))
    }
    $duplicateOutputs = @($manifests | Group-Object -Property Output | Where-Object Count -gt 1)
    if ($duplicateOutputs.Count -gt 0) {
      throw ('Duplicate manifest output filename(s): {0}' -f (($duplicateOutputs.Name | Sort-Object) -join ', '))
    }

    return @($manifests | Sort-Object -Property Order, Id)
  }
  #-------------------------------------------------------------------------------
  # Select-DocumentManifests
  #-------------------------------------------------------------------------------
  function Select-DocumentationManifests {
    param(
      [Parameter(Mandatory)]
      [string] $Root,
      [Parameter(Mandatory)]
      [string[]] $Requested
    )

    $all = @(Get-DocumentationManifests -Root $Root)
    if ($Requested.Count -eq 0 -or @($Requested | Where-Object { $_ -eq 'All' }).Count -gt 0) {
      return $all
    }

    $selected = New-Object -TypeName 'System.Collections.Generic.List[object]'
    $seen = New-Object -TypeName 'System.Collections.Generic.HashSet[string]' -ArgumentList ([StringComparer]::OrdinalIgnoreCase)
    foreach ($name in $Requested) {
      $matches = @($all | Where-Object {
          $_.Id -eq $name -or
          [IO.Path]::GetFileNameWithoutExtension($_.SourceName) -eq $name -or
          [IO.Path]::GetFileNameWithoutExtension($_.Output) -eq $name
      })
      if ($matches.Count -eq 0) {
        throw ('Unknown document manifest: {0}. Available ids: {1}' -f $name, (($all.Id) -join ', '))
      }
      foreach ($item in $matches) {
        if ($seen.Add($item.Id)) {
          $selected.Add($item)
        }
      }
    }
    return $selected.ToArray()
  }
  #-------------------------------------------------------------------------------
  # Convert-TemplateTokens
  #-------------------------------------------------------------------------------
  function Convert-TemplateTokens {
    param(
      [Parameter(Mandatory)]
      [string] $Content,
      [Parameter(Mandatory)]
      [hashtable] $Tokens
    )

    $result = $Content
    foreach ($key in $Tokens.Keys) {
      $result = $result.Replace(('{{' + $key + '}}'), [string]$Tokens[$key])
    }
    return $result
  }
  #-------------------------------------------------------------------------------
  # Assert-NoUnresolvedTokens
  #-------------------------------------------------------------------------------
  function Assert-NoUnresolvedTokens {
    param(
      [Parameter(Mandatory)][string] $Content,
      [Parameter(Mandatory)][string] $SourceName
    )

    $unresolved = @([regex]::Matches($Content, '\{\{[A-Za-z][A-Za-z0-9_.-]*\}\}') |
    ForEach-Object Value | Sort-Object -Unique)
    if ($unresolved.Count -gt 0) {
      throw ('Unresolved documentation token(s) in {0}: {1}' -f $SourceName, ($unresolved -join ', '))
    }
  }
  #-------------------------------------------------------------------------------
  # Convert-ManifestGlobToRegex
  #-------------------------------------------------------------------------------
  function Convert-ManifestGlobToRegex {
    param(
      [Parameter(Mandatory)]
      [string] $Pattern
    )

    $normalised = $Pattern.Replace('\', '/').TrimStart('/')
    $escaped = [regex]::Escape($normalised)
    $escaped = $escaped -replace '\\\*\\\*/', '(?:.*/)?'
    $escaped = $escaped -replace '\\\*\\\*', '.*'
    $escaped = $escaped -replace '\\\*', '[^/]*'
    $escaped = $escaped -replace '\\\?', '[^/]'
    return '^(?:{0})$' -f $escaped
  }
  #-------------------------------------------------------------------------------
  # Get-ManifestSourceMatches
  #-------------------------------------------------------------------------------
  function Get-ManifestSourceMatches {
    param(
      [Parameter(Mandatory)]
      [string] $DocsRoot,
      [Parameter(Mandatory)]
      [string] $Entry
    )

    $normalisedEntry = $Entry.Replace('\', '/').TrimStart('/')
    $hasWildcard = [System.Management.Automation.WildcardPattern]::ContainsWildcardCharacters($normalisedEntry)
    if (-not $hasWildcard) {
      $exactPath = Join-Path -Path $DocsRoot -ChildPath ($normalisedEntry -replace '/', '\')
      if (Test-Path -LiteralPath $exactPath -PathType Leaf) {
        return ,(Get-Item -LiteralPath $exactPath)
      }
      return @()
    }

    $regex = Convert-ManifestGlobToRegex -Pattern $normalisedEntry
    return @(Get-ChildItem -LiteralPath $DocsRoot -File -Recurse |
      Where-Object {
        $relative = $_.FullName.Substring($DocsRoot.Length).TrimStart('\', '/').Replace('\', '/')
        $relative -match $regex
      } |
    Sort-Object -Property FullName)
  }
  #-------------------------------------------------------------------------------
  # Resolve-DocumentationContenFile
  #-------------------------------------------------------------------------------
  function Resolve-DocumentationContentFile {
    param(
      [Parameter(Mandatory)]
      [string] $Root,
      [Parameter(Mandatory)]
      [string] $RelativePath,
      [Parameter(Mandatory)]
      [string] $Description
    )

    $docsRoot = [IO.Path]::GetFullPath((Join-Path -Path $Root -ChildPath 'docs'))
    $path = [IO.Path]::GetFullPath((Join-Path -Path $docsRoot -ChildPath ($RelativePath -replace '/', '\')))
    if (-not $path.StartsWith($docsRoot, [StringComparison]::OrdinalIgnoreCase)) {
      throw ('{0} escapes the docs directory: {1}' -f $Description, $RelativePath)
    }
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
      throw ('{0} was not found: {1}' -f $Description, $path)
    }
    return Get-Item -LiteralPath $path
  }
  #-------------------------------------------------------------------------------
  # Get-DocumentationFiles
  #-------------------------------------------------------------------------------
  function Get-DocumentationFiles {
    param(
      [Parameter(Mandatory)]
      [string] $Root,
      [Parameter(Mandatory)] 
      [PSCustomObject] $Manifest
    )

    $docsRoot = Join-Path -Path $Root -ChildPath 'docs'
    $files = New-Object -TypeName 'System.Collections.Generic.List[System.IO.FileInfo]'
    $seen = New-Object -TypeName 'System.Collections.Generic.HashSet[string]' -ArgumentList ([StringComparer]::OrdinalIgnoreCase)
    foreach ($entry in @($Manifest.Sections)) {
      if ([string]::IsNullOrWhiteSpace([string]$entry)) {
        continue
      }
      $matches = @(Get-ManifestSourceMatches -DocsRoot $docsRoot -Entry ([string]$entry))
      if ($matches.Count -eq 0) {
        throw ('Manifest source or pattern matched no files in {0}: docs/{1}' -f $Manifest.SourceName, $entry)
      }
      foreach ($file in $matches) {
        if ($file.Extension -ne '.md') {
          throw ('Manifest sections must resolve to Markdown files: {0}' -f $file.FullName)
        }
        if ($seen.Add($file.FullName)) {
          $files.Add($file)
        }
      }
    }
    return $files.ToArray()
  }
  #-------------------------------------------------------------------------------
  # Get-ManifestFrontMatterFiles
  #-------------------------------------------------------------------------------
  function Get-ManifestFrontMatterFiles {
    param(
      [Parameter(Mandatory)]
      [string] $Root,
      [Parameter(Mandatory)] 
      [PSCustomObject] $Manifest
    )

    $files = New-Object -TypeName 'System.Collections.Generic.List[System.IO.FileInfo]'
    foreach ($definition in @(
        @{ Name = 'Cover page'
        Path = $Manifest.CoverPage },
        @{ Name = 'Copyright page'
        Path = $Manifest.CopyrightPage },
        @{ Name = 'Revision history'
        Path = $Manifest.RevisionHistory }
    )) {
      if (-not [string]::IsNullOrWhiteSpace([string]$definition.Path)) {
        $file = Resolve-DocumentationContentFile -Root $Root -RelativePath ([string]$definition.Path) -Description $definition.Name
        $files.Add($file)
      }
    }
    return $files.ToArray()
  }
  #-------------------------------------------------------------------------------
  # Remove-DocumentHeader
  #-------------------------------------------------------------------------------
  function Remove-DocumentHeader {
    param(
      [Parameter(Mandatory)]
      [string] $Content
    )

    $result = $Content -replace '(?ms)^---\s*\r?\n.*?\r?\n---\s*\r?\n', ''
    $result = $result -replace '(?ms)^\*\*Document ID:\*\*.*?\r?\n---\s*\r?\n', ''
    return $result.Trim()
  }
  #-------------------------------------------------------------------------------
  # ConvertTo-YamlSingleQuotedString
  #-------------------------------------------------------------------------------
  function ConvertTo-YamlSingleQuotedString {
    param(
      [Parameter(Mandatory)]
      [AllowEmptyString()]
      [string] $Value
    )

    return "'" + $Value.Replace("'", "''") + "'"
  }
  #-------------------------------------------------------------------------------
  # Test-TrailingLandscapeBlock
  #-------------------------------------------------------------------------------
  function Test-TrailingLandscapeBlock {
    param(
      [Parameter(Mandatory)]
      [string] $Content
    )

    return $Content -match '(?ms):::\s*\{\.landscape\}.*?:::\s*$'
  }
  #-------------------------------------------------------------------------------
  # New-DocumentationTokens
  #-------------------------------------------------------------------------------
  function New-DocumentationTokens {
    param(
      [Parameter(Mandatory)] 
      $Config,
      [Parameter(Mandatory)] 
      [PSCustomObject] $Manifest,
      [Parameter(Mandatory)]
      [string] $DocumentDate
    )

    $author = if ($Manifest.Metadata.ContainsKey('author')) {
      [string]$Manifest.Metadata['author']
    } else {
      [string]$Config.author
    }
    $tokens = @{
      title = [string]$Manifest.Title
      projectTitle = [string]$Config.projectTitle
      slogan = [string]$Config.slogan
      author = $author
      version = [string]$Config.projectVersion
      date = $DocumentDate
      year = (Get-Date).Year.ToString([cultureinfo]::InvariantCulture)
      coverImage = [string]$Config.coverImage
      documentId = [string]$Manifest.Id
      output = [string]$Manifest.Output
    }
    foreach ($key in $Manifest.Metadata.Keys) {
      $tokens[[string]$key] = [string]$Manifest.Metadata[$key]
    }
    return $tokens
  }
  #-------------------------------------------------------------------------------
  # Get-OutputBaseName
  #-------------------------------------------------------------------------------
  function Get-OutputBaseName {
    param(
      [Parameter(Mandatory)] 
      [PSCustomObject]$Manifest
    )

    $extension = [IO.Path]::GetExtension($Manifest.Output)
    if ([string]::IsNullOrWhiteSpace($extension)) {
      return $Manifest.Output
    }
    return [IO.Path]::GetFileNameWithoutExtension($Manifest.Output)
  }
  #-------------------------------------------------------------------------------
  # New-DocumentInventory
  #-------------------------------------------------------------------------------
  function New-DocumentInventory {
    param(
      [Parameter(Mandatory)]
      [string] $Root,
      [Parameter(Mandatory)] 
      [PSCustomObject] $Manifest
    )

    $config = Read-DocumentationConfig -Root $Root
    $build = Join-Path -Path $Root -ChildPath $config.buildDirectory
    Assert-Directory -Path $build
    $safeId = $Manifest.Id -replace '[^A-Za-z0-9._-]', '-'
    $output = Join-Path -Path $build -ChildPath ('{0}-document-inventory.md' -f $safeId)
    $lines = New-Object -TypeName 'System.Collections.Generic.List[string]'
    $lines.Add(('# {0} Document Source Inventory' -f $Manifest.Title))
    $lines.Add('')
    $lines.Add(('Manifest: `{0}`' -f $Manifest.SourceName))
    $lines.Add('')
    $lines.Add('| Order | Role | Document | First heading |')
    $lines.Add('|---:|---|---|---|')
    $position = 1

    foreach ($file in @(Get-ManifestFrontMatterFiles -Root $Root -Manifest $Manifest)) {
      $heading = Get-Content -LiteralPath $file.FullName -Encoding UTF8 |
      Where-Object { $_ -match '^#\s+\S' } |
      Select-Object -First 1
      if ($null -eq $heading) {
        $heading = '(No level-one heading)'
      } else {
        $heading = $heading -replace '^#\s+', ''
      }
      $relative = $file.FullName.Substring($Root.Length).TrimStart('\', '/') -replace '\\', '/'
      $lines.Add(('| {0} | Front matter | [{1}](../../{2}) | {3} |' -f $position, $file.Name, $relative, $heading))
      $position++
    }

    foreach ($file in @(Get-DocumentationFiles -Root $Root -Manifest $Manifest)) {
      $heading = Get-Content -LiteralPath $file.FullName -Encoding UTF8 |
      Where-Object { $_ -match '^#\s+\S' } |
      Select-Object -First 1
      if ($null -eq $heading) {
        $heading = '(No level-one heading)'
      } else {
        $heading = $heading -replace '^#\s+', ''
      }
      $relative = $file.FullName.Substring($Root.Length).TrimStart('\', '/') -replace '\\', '/'
      $lines.Add(('| {0} | Section | [{1}](../../{2}) | {3} |' -f $position, $file.Name, $relative, $heading))
      $position++
    }

    $lines | Set-Content -LiteralPath $output -Encoding UTF8
    return $output
  }
  #-------------------------------------------------------------------------------
  # Merge-Documentation
  #-------------------------------------------------------------------------------
  function Merge-Documentation {
    param(
      [Parameter(Mandatory)]
      [string] $Root,
      [Parameter(Mandatory)] 
      [PSCustomObject] $Manifest
    )

    $config = Read-DocumentationConfig -Root $Root
    $build = Join-Path -Path $Root -ChildPath $config.buildDirectory
    Assert-Directory -Path $build
    $output = Join-Path -Path $build -ChildPath ('{0}.md' -f (Get-OutputBaseName -Manifest $Manifest))
    $encoding = New-Object -TypeName System.Text.UTF8Encoding -ArgumentList ($false)
    $writer = New-Object -TypeName System.IO.StreamWriter -ArgumentList ($output, $false, $encoding)
    try {
      $documentDate = (Get-Date).ToString($config.dateFormat, [cultureinfo]::InvariantCulture)
      $author = if ($Manifest.Metadata.ContainsKey('author')) {
        [string]$Manifest.Metadata['author']
      } else {
        [string]$config.author
      }
      $language = if ($Manifest.Metadata.ContainsKey('language')) {
        [string]$Manifest.Metadata['language']
      } else {
        [string]$config.language
      }

      $writer.WriteLine('---')
      $writer.WriteLine('title: {0}' -f (ConvertTo-YamlSingleQuotedString -Value $Manifest.Title))
      $writer.WriteLine('author: {0}' -f (ConvertTo-YamlSingleQuotedString -Value $author))
      $writer.WriteLine('date: {0}' -f (ConvertTo-YamlSingleQuotedString -Value $documentDate))
      $writer.WriteLine('lang: {0}' -f $language)
      $writer.WriteLine('opendata-generate-toc: {0}' -f $Manifest.GenerateToc.ToString().ToLowerInvariant())
      $writer.WriteLine('opendata-toc-depth: {0}' -f $Manifest.TocDepth)
      $writer.WriteLine('---')
      $writer.WriteLine()

      $tokens = New-DocumentationTokens -Config $config -Manifest $Manifest -DocumentDate $documentDate
      foreach ($frontMatterFile in @(Get-ManifestFrontMatterFiles -Root $Root -Manifest $Manifest)) {
        $content = Get-Content -LiteralPath $frontMatterFile.FullName -Raw -Encoding UTF8
        $content = Convert-TemplateTokens -Content $content -Tokens $tokens
        Assert-NoUnresolvedTokens -Content $content -SourceName $frontMatterFile.FullName
        $writer.WriteLine($content.Trim())
        $writer.WriteLine()
      }

      if ($Manifest.GenerateToc) {
        $writer.WriteLine('::: {.document-toc}')
        $writer.WriteLine(':::')
        $writer.WriteLine()
      }

      $files = @(Get-DocumentationFiles -Root $Root -Manifest $Manifest)
      for ($index = 0; $index -lt $files.Count; $index++) {
        $file = $files[$index]
        $content = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8
        $content = Remove-DocumentHeader -Content $content
        $content = Convert-TemplateTokens -Content $content -Tokens $tokens
        Assert-NoUnresolvedTokens -Content $content -SourceName $file.FullName
        $writer.WriteLine($content)
        $writer.WriteLine()
        if ($index -lt ($files.Count - 1) -and -not (Test-TrailingLandscapeBlock -Content $content)) {
          $writer.WriteLine('\newpage')
          $writer.WriteLine()
        }
      }
    } finally {
      $writer.Dispose()
    }
    return $output
  }
  #-------------------------------------------------------------------------------
  # Get-PandocResourcePath
  #-------------------------------------------------------------------------------
  function Get-PandocResourcePath {
    param(
      [Parameter(Mandatory)]
      [string] $Root,
      [Parameter(Mandatory)]
      [string] $Build,
      [Parameter(Mandatory)] 
      [PSCustomObject] $Manifest
    )

    $paths = New-Object -TypeName 'System.Collections.Generic.List[string]'
    $seen = New-Object -TypeName 'System.Collections.Generic.HashSet[string]' -ArgumentList ([StringComparer]::OrdinalIgnoreCase)
    $files = @()
    $files += @(Get-ManifestFrontMatterFiles -Root $Root -Manifest $Manifest)
    $files += @(Get-DocumentationFiles -Root $Root -Manifest $Manifest)

    foreach ($candidate in @($Build, $Root, (Join-Path -Path $Root -ChildPath 'docs'))) {
      $fullPath = [IO.Path]::GetFullPath($candidate)
      if ($seen.Add($fullPath)) {
        $paths.Add($fullPath)
      }
    }
    foreach ($file in $files) {
      if ($null -ne $file -and $seen.Add($file.DirectoryName)) {
        $paths.Add($file.DirectoryName)
      }
    }
    return ($paths -join [IO.Path]::PathSeparator)
  }
  #-------------------------------------------------------------------------------
  # Invoke-PlantUmlRender
  #-------------------------------------------------------------------------------
  function Invoke-PlantUmlRender {
    param(
      [Parameter(Mandatory)]
      [string] $Root,
      [Parameter(Mandatory)]
      [ValidateSet('svg', 'png')]
      [string] $OutputFormat
    )

    $renderer = Join-Path -Path $Root -ChildPath 'scripts\Convert-PlantUml.ps1'
    if (-not (Test-Path -LiteralPath $renderer -PathType Leaf)) {
      throw ('PlantUML renderer was not found: {0}' -f $renderer)
    }
    . $renderer
    Convert-PlantUml -ProjectRoot $Root -Format $OutputFormat -Clean
  }
  #-------------------------------------------------------------------------------
  # Test-Documentation
  #-------------------------------------------------------------------------------
  function Test-Documentation {
    param(
      [Parameter(Mandatory)][string] $Root,
      [switch] $WarningsAreErrors
    )

    $issues = New-Object -TypeName 'System.Collections.Generic.List[object]'
    $docsRoot = Join-Path -Path $Root -ChildPath 'docs'
    $config = Read-DocumentationConfig -Root $Root

    try {
      $manifests = @(Get-DocumentationManifests -Root $Root)
      foreach ($manifest in $manifests) {
        $null = @(Get-ManifestFrontMatterFiles -Root $Root -Manifest $manifest)
        $null = @(Get-DocumentationFiles -Root $Root -Manifest $manifest)
        if (-not [string]::IsNullOrWhiteSpace($manifest.Template)) {
          $templatePath = Join-Path -Path $Root -ChildPath ($manifest.Template -replace '/', '\')
          if (-not (Test-Path -LiteralPath $templatePath -PathType Leaf)) {
            throw ('Word template was not found for {0}: {1}' -f $manifest.Id, $templatePath)
          }
        }
        if ($manifest.TocDepth -lt 1 -or $manifest.TocDepth -gt 6) {
          throw ('tocDepth must be between 1 and 6 in {0}.' -f $manifest.SourceName)
        }
      }
    } catch {
      $issues.Add([pscustomobject]@{
          Severity = 'Error'
          File = (Join-Path -Path $Root -ChildPath $config.manifestDirectory)
          Message = $_.Exception.Message
      })
    }

    $buildRoot = [IO.Path]::GetFullPath((Join-Path -Path $Root -ChildPath $config.buildDirectory))
    $files = @(Get-ChildItem -LiteralPath $docsRoot -File -Recurse -Filter '*.md' |
      Where-Object {
        -not $_.FullName.StartsWith($buildRoot, [StringComparison]::OrdinalIgnoreCase)
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
        if ($target -match '^(https?:|mailto:|#)' -or $target -match '\{\{[^}]+\}\}') {
          continue
        }
        $decoded = [uri]::UnescapeDataString($target)
        $linkPath = [IO.Path]::GetFullPath((Join-Path -Path $file.DirectoryName -ChildPath ($decoded -replace '/', '\')))
        if (-not (Test-Path -LiteralPath $linkPath)) {
          $issues.Add([pscustomobject]@{
              Severity = 'Error'
              File = $file.FullName
              Message = ('Broken relative link: {0}' -f $target)
          })
        }
      }
    }

    $canonicalSource = [IO.Path]::GetFullPath((Join-Path -Path $Root -ChildPath $config.diagramSourceDirectory))
    $legacyPuml = @(Get-ChildItem -LiteralPath (Join-Path -Path $Root -ChildPath 'docs\diagrams') -File -Recurse -Filter '*.puml' |
      Where-Object {
        -not [string]::Equals($_.DirectoryName, $canonicalSource, [StringComparison]::OrdinalIgnoreCase)
    })
    foreach ($file in $legacyPuml) {
      $issues.Add([pscustomobject]@{
          Severity = 'Error'
          File = $file.FullName
          Message = ('PlantUML source is outside {0}.' -f $config.diagramSourceDirectory)
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
  #-------------------------------------------------------------------------------
  # Convert-SvgAssetsForPdf
  #-------------------------------------------------------------------------------
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
  # Remove-InvalidXmlCharacters
  #--------------------------------------------------------------------------------
  function Remove-InvalidXmlCharacters {
    <#
        .SYNOPSIS
        Removes characters that are not permitted in XML 1.0 documents.

        .DESCRIPTION
        Pandoc can preserve control characters from source Markdown or generated
        metadata in a DOCX XML part. System.Xml.XmlDocument rejects those
        characters when the documentation engine subsequently updates the DOCX.
    #>
    [CmdletBinding()]
    param(
      [AllowEmptyString()]
      [string] $Content,

      [Parameter(Mandatory)]
      [string] $PartName
    )

    if ([string]::IsNullOrEmpty($Content)) {
      return $Content
    }

    $invalidXmlPattern = '[\x00-\x08\x0B\x0C\x0E-\x1F\uFFFE\uFFFF]'
    $invalidCharacters = [Text.RegularExpressions.Regex]::Matches(
      $Content,
      $invalidXmlPattern)

    if ($invalidCharacters.Count -eq 0) {
      return $Content
    }

    $characterCodes = @(
      $invalidCharacters |
      ForEach-Object {
        '0x{0:X4}' -f [int][char]$_.Value[0]
      } |
      Select-Object -Unique
    )

    Write-Warning (
      'Removed {0} invalid XML character(s) ({1}) from {2}.' -f
      $invalidCharacters.Count,
      ($characterCodes -join ', '),
      $PartName)

    return [Text.RegularExpressions.Regex]::Replace(
      $Content,
      $invalidXmlPattern,
      '')
  }

  #--------------------------------------------------------------------------------
  # Set-DocxFieldRefresh
  #--------------------------------------------------------------------------------
  function Set-DocxFieldRefresh {
    param([Parameter(Mandatory)][string] $Path)

    Add-Type -AssemblyName System.IO.Compression
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $wordNamespace = 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'
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
      $entry = $archive.GetEntry('word/settings.xml')
      if ($null -eq $entry) {
        throw ('The DOCX package does not contain word/settings.xml: {0}' -f $Path)
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
          $settingsXml = $reader.ReadToEnd()
          $settingsXml = Remove-InvalidXmlCharacters `
          -Content $settingsXml `
          -PartName 'word/settings.xml'
        } finally {
          $reader.Dispose()
        }
      } finally {
        $entryStream.Dispose()
      }
      $xml = New-Object -TypeName System.Xml.XmlDocument
      $xml.PreserveWhitespace = $true
      $xml.LoadXml($settingsXml)
      $namespaces = New-Object -TypeName System.Xml.XmlNamespaceManager -ArgumentList ($xml.NameTable)
      $namespaces.AddNamespace('w', $wordNamespace)
      $settingsNode = $xml.SelectSingleNode('/w:settings', $namespaces)
      if ($null -eq $settingsNode) {
        throw ('The DOCX settings could not be read: {0}' -f $Path)
      }
      $updateFields = $settingsNode.SelectSingleNode('./w:updateFields', $namespaces)
      if ($null -eq $updateFields) {
        $updateFields = $xml.CreateElement('w', 'updateFields', $wordNamespace)
        $null = $settingsNode.PrependChild($updateFields)
      }
      $null = $updateFields.SetAttribute('val', $wordNamespace, 'true')
      
      $entryStream = $entry.Open()
      try {
        $entryStream.SetLength(0)
        $writerSettings = New-Object -TypeName System.Xml.XmlWriterSettings
        $writerSettings.Encoding = New-Object -TypeName System.Text.UTF8Encoding -ArgumentList ($false)
        $writerSettings.Indent = $false
        $writer = [Xml.XmlWriter]::Create($entryStream, $writerSettings)
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

    Write-Output -InputObject 'Configured Word to refresh document fields when the DOCX is opened.'
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
          $documentXml = Remove-InvalidXmlCharacters `
          -Content $documentXml `
          -PartName 'word/document.xml'
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
  #-------------------------------------------------------------------------------
  # Publish=Document
  #-------------------------------------------------------------------------------
  function Publish-Document {
    param(
      [Parameter(Mandatory)]
      [string] $Root,
      [Parameter(Mandatory)] 
      [PSCustomObject] $Manifest,
      [Parameter(Mandatory)]
      [ValidateSet('All', 'Html', 'Docx', 'Pdf', 'None')]
      [string] $OutputFormat,
      [AllowNull()]
      [string] $DocxReference
    )

    $config = Read-DocumentationConfig -Root $Root
    if ($Manifest.IncludeInventory) {
      $inventory = New-DocumentInventory -Root $Root -Manifest $Manifest
      Write-Output -InputObject ('Created source inventory {0}' -f $inventory)
    }

    $manual = Merge-Documentation -Root $Root -Manifest $Manifest
    if ($OutputFormat -eq 'None') {
      Write-Output -InputObject ('Created {0}' -f $manual)
      return
    }

    Assert-Command -Name 'pandoc'
    $build = Join-Path -Path $Root -ChildPath $config.buildDirectory
    $resourcePath = Get-PandocResourcePath -Root $Root -Build $build -Manifest $Manifest
    $landscapeFilter = Join-Path -Path $Root -ChildPath $config.landscapeFilter
    $tocFilter = Join-Path -Path $Root -ChildPath $config.tocFilter
    $latexHeader = Join-Path -Path $Root -ChildPath $config.latexLandscapeHeader

    foreach ($requiredFilter in @($tocFilter, $landscapeFilter)) {
      if (-not (Test-Path -LiteralPath $requiredFilter -PathType Leaf)) {
        throw ('Pandoc filter was not found: {0}' -f $requiredFilter)
      }
    }

    $baseArgs = @(
      $manual,
      '--from=markdown+yaml_metadata_block+pipe_tables+fenced_divs+link_attributes+raw_attribute',
      '--standalone',
      ('--lua-filter={0}' -f $tocFilter),
      ('--lua-filter={0}' -f $landscapeFilter),
      ('--resource-path={0}' -f $resourcePath),
      '--metadata', ('lang={0}' -f $config.language),
      '--metadata', 'papersize=a4'
    )
    if ($Manifest.NumberHeadings) {
      $baseArgs += '--number-sections'
    }

    $formats = if ($OutputFormat -eq 'All') {
      @('Html', 'Docx', 'Pdf')
    } else {
      @($OutputFormat)
    }

    foreach ($item in $formats) {
      $extension = $item.ToLowerInvariant()
      $output = Join-Path -Path $build -ChildPath (
      '{0}.{1}' -f (Get-OutputBaseName -Manifest $Manifest), $extension)

      switch ($item) {
        'Html' {
          & pandoc @baseArgs --embed-resources --output $output
        }
        'Docx' {
          $reference = $DocxReference
          if ([string]::IsNullOrWhiteSpace($reference) -and
          -not [string]::IsNullOrWhiteSpace($Manifest.Template)) {
            $candidate = Join-Path -Path $Root -ChildPath ($Manifest.Template -replace '/', '\')
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
            Set-DocxFieldRefresh -Path $output
          }
        }
        'Pdf' {
          Convert-SvgAssetsForPdf -Root $Root
          & pandoc @baseArgs `
          ('--include-in-header={0}' -f $latexHeader) `
          ('--pdf-engine={0}' -f $config.pdfEngine) `
          --output $output
        }
      }

      if ($LASTEXITCODE -ne 0) {
        throw ('Pandoc failed while building {0} {1} output.' -f $Manifest.Id, $item)
      }
      $Size = (Get-item -path $Output).Length
      Write-Output -InputObject ('Created {0}, {1} bytes' -f $output,$size)
      
    }
  }

  $ProjectRoot = if ([string]::IsNullOrWhiteSpace($ProjectRoot)) {
    Resolve-ProjectRoot
  } else {
    (Resolve-Path -LiteralPath $ProjectRoot).Path
  }

  if ($Action -eq 'All') {
    $Action = 'Build'
    $Document = @('All')
  }
  
  if ($Action -in @('Build', 'Test')) {
    Sync-NoticeFiles -Root $ProjectRoot
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
      $manifests = @(Select-DocumentationManifests -Root $ProjectRoot -Requested $Document)
      foreach ($manifest in $manifests) {
        Publish-Document -Root $ProjectRoot `
        -Manifest $manifest `
        -OutputFormat $Format `
        -DocxReference $ReferenceDoc
      }
    }
  }
}
$ProjectRoot = 'C:\Users\terry\Documents\NetBeansProjects\opendata'
$ReferenceDoc = Join-Path -Path $ProjectRoot -ChildPath 'docs\templates\Reference Styles.docx'
$Parameters = @{
	Action         = 'All' 
	ProjectRoot    = $ProjectRoot
	Format         = 'docx' 
	FailOnWarning  = $true
	verbose        = $true
	RenderDiagrams = $true
	ReferenceDoc   = $ReferenceDoc
}

Invoke-Documentation @Parameters