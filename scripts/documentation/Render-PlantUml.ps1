#Requires -Version 5.1
<#
.SYNOPSIS
Renders OpenData PlantUML sources without building the documentation.

.EXAMPLE
.\Render-PlantUml.ps1

.EXAMPLE
.\Render-PlantUml.ps1 -Format svg -Clean
#>
[CmdletBinding(SupportsShouldProcess)]
param(
    [string] $ProjectRoot,

    [ValidateSet('svg', 'png')]
    [string] $Format = 'svg',

    [switch] $Clean
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version 2.0

function Resolve-ProjectRoot {
    param([string] $StartPath = $PSScriptRoot)

    $current = Get-Item -LiteralPath (Resolve-Path -LiteralPath $StartPath)
    while ($null -ne $current) {
        $candidate = Join-Path $current.FullName 'config\documentation.json'
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

$configPath = Join-Path $ProjectRoot 'config\documentation.json'
$config = Get-Content -LiteralPath $configPath -Raw -Encoding UTF8 |
    ConvertFrom-Json
$source = Join-Path $ProjectRoot $config.diagramSourceDirectory
$output = Join-Path $ProjectRoot $config.diagramOutputDirectory
$jar = Join-Path $ProjectRoot $config.plantUmlJar

if (-not (Test-Path -LiteralPath $jar -PathType Leaf)) {
    throw "PlantUML JAR was not found: $jar"
}
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw "The 'java' command was not found."
}
if (-not (Test-Path -LiteralPath $output -PathType Container)) {
    $null = New-Item -ItemType Directory -Path $output -Force
}

$legacySources = @(Get-ChildItem -LiteralPath (Join-Path $ProjectRoot 'docs\diagrams') `
    -File -Recurse -Filter '*.puml' |
    Where-Object { $_.DirectoryName -ne $source })
if ($legacySources.Count -gt 0) {
    $paths = $legacySources.FullName -join [Environment]::NewLine
    throw "PlantUML sources exist outside the canonical source folder:`n$paths"
}

if ($Clean -and $PSCmdlet.ShouldProcess($output, "Remove generated $Format files")) {
    Get-ChildItem -LiteralPath $output -File -Filter "*.$Format" |
        Remove-Item -Force
}

$diagrams = @(Get-ChildItem -LiteralPath $source -File -Filter '*.puml' |
    Sort-Object Name)
if ($diagrams.Count -eq 0) {
    throw "No PlantUML sources were found in $source"
}

$failures = New-Object 'System.Collections.Generic.List[string]'
foreach ($diagram in $diagrams) {
    Write-Output "Rendering $($diagram.Name)"
    & java -jar $jar "-t$Format" -charset UTF-8 -o '..\generated' $diagram.FullName
    $rendered = Join-Path $output "$($diagram.BaseName).$Format"
    if ($LASTEXITCODE -ne 0 -or
        -not (Test-Path -LiteralPath $rendered -PathType Leaf) -or
        (Get-Item -LiteralPath $rendered).Length -eq 0) {
        $failures.Add($diagram.FullName)
    }
}

if ($failures.Count -gt 0) {
    throw ("PlantUML failed for:`n" + ($failures -join [Environment]::NewLine))
}

Write-Output "Rendered $($diagrams.Count) diagram(s) to $output"
