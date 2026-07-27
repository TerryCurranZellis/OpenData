#Requires -Version 5.1
function Convert-PlantUml {
  <#
      .SYNOPSIS
      Renders OpenData PlantUML sources without building the documentation.

      .EXAMPLE
      Convert-PlantUml

      .EXAMPLE
      Convert-PlantUml -Format svg -Clean
  #>

  [CmdletBinding(SupportsShouldProcess)]
  param(
    [Parameter(Mandatory)][string] $ProjectRoot,

    [ValidateSet('svg', 'png')]
    [string] $Format = 'svg',

    [switch] $Clean
  )

  $ErrorActionPreference = 'Stop'

  function Resolve-ProjectRoot {
    [CmdletBinding(SupportsShouldProcess)]
    param([string] $StartPath = $PSScriptRoot)

    $current = Get-Item -LiteralPath (Resolve-Path -LiteralPath $StartPath)
    while ($null -ne $current) {
      $candidate = Join-Path -Path $current.FullName -ChildPath 'config\documentation.json'
      if (Test-Path -LiteralPath $candidate -PathType Leaf) {
        return $current.FullName
      }
      $current = $current.Parent
    }
    throw 'Unable to locate config\documentation.json.'
  }

  if ([string]::IsNullOrWhiteSpace($ProjectRoot)) {
    $ProjectRoot = Resolve-ProjectRoot
  } else {
    $ProjectRoot = (Resolve-Path -LiteralPath $ProjectRoot).Path
  }

  $configPath = Join-Path -Path $ProjectRoot -ChildPath 'config\documentation.json'
  $config = Get-Content -LiteralPath $configPath -Raw -Encoding UTF8 |
  ConvertFrom-Json
  $source = Join-Path -Path $ProjectRoot -ChildPath $config.diagramSourceDirectory
  $output = Join-Path -Path $ProjectRoot -ChildPath $config.diagramOutputDirectory
  $jar = Join-Path -Path $ProjectRoot -ChildPath $config.plantUmlJar

  if (-not (Test-Path -LiteralPath $jar -PathType Leaf)) {
    throw ('PlantUML JAR was not found: {0}' -f $jar)
  }
  if (-not (Get-Command -Name java -ErrorAction SilentlyContinue)) {
    throw "The 'java' command was not found."
  }
  if (-not (Test-Path -LiteralPath $output -PathType Container)) {
    $null = New-Item -ItemType Directory -Path $output -Force
  }

  $legacySources = @(Get-ChildItem -LiteralPath (Join-Path -Path $ProjectRoot -ChildPath 'docs\diagrams') -File -Recurse -Filter '*.puml' |
	Where-Object { $_.DirectoryName -ne $source })
  if ($legacySources.Count -gt 0) {
    $paths = $legacySources.FullName -join [Environment]::NewLine
    throw ("PlantUML sources exist outside the canonical source folder:`n{0}" -f $paths)
  }

  if ($Clean -and $PSCmdlet.ShouldProcess($output, ('Remove generated {0} files' -f $Format))) {
    Get-ChildItem -LiteralPath $output -File -Filter ('*.{0}' -f $Format) |
    Remove-Item -Force
  }

  $diagrams = @(Get-ChildItem -LiteralPath $source -File -Filter '*.puml' |
	Sort-Object -Property Name)
  if ($diagrams.Count -eq 0) {
    throw ('No PlantUML sources were found in {0}' -f $source)
  }

  $failures = New-Object -TypeName 'System.Collections.Generic.List[string]'
  foreach ($diagram in $diagrams) {
    Write-Output -InputObject ('Rendering {0}' -f $diagram.Name)
    & java -jar $jar ('-t{0}' -f $Format) -charset UTF-8 -o '..\generated' $diagram.FullName
    $rendered = Join-Path -Path $output -ChildPath ('{0}.{1}' -f $diagram.BaseName, $Format)
    if ($LASTEXITCODE -ne 0 -or
      -not (Test-Path -LiteralPath $rendered -PathType Leaf) -or
    (Get-Item -LiteralPath $rendered).Length -eq 0) {
      $failures.Add($diagram.FullName)
    }
  }

  if ($failures.Count -gt 0) {
    throw ("PlantUML failed for:`n" + ($failures -join [Environment]::NewLine))
  }

  Write-Output -InputObject ('Rendered {0} diagram(s) to {1}' -f $diagrams.Count, $output)
}
$ProjectRoot = 'C:\Users\terry\Documents\NetBeansProjects\opendata'

Convert-PlantUml -ProjectRoot $ProjectRoot