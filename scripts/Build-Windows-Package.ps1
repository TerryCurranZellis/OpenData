<#
.SYNOPSIS
Builds a Windows jpackage image or installer for OpenData 3.1.0.

.DESCRIPTION
Creates a normal GUI launcher named OpenData and an additional console launcher
named OpenData-CLI. The same com.towermarsh.opendata.OpenData entry point is
used by both launchers; the main program selects GUI or CLI behaviour from the
arguments.

The default Type is app-image so the generated application can be acceptance
-tested before an EXE/MSI installer is produced.
#>
[CmdletBinding()]
param(
    [Parameter()]
    [ValidateSet('app-image', 'exe', 'msi')]
    [string] $Type = 'app-image',

    [Parameter()]
    [string] $Version = '3.1.0',

    [Parameter()]
    [string] $RepositoryPath = (Get-Location).Path,

    [Parameter()]
    [switch] $SkipBuild
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$RepositoryPath = (Resolve-Path -LiteralPath $RepositoryPath).Path
$pom = Join-Path $RepositoryPath 'pom.xml'
if (-not (Test-Path -LiteralPath $pom -PathType Leaf)) {
    throw "OpenData pom.xml was not found below $RepositoryPath"
}

$maven = Get-Command 'mvn.cmd' -ErrorAction SilentlyContinue
if ($null -eq $maven) {
    $maven = Get-Command 'mvn' -ErrorAction SilentlyContinue
}
if ($null -eq $maven) {
    throw 'Maven was not found on PATH.'
}

$jpackage = Get-Command 'jpackage.exe' -ErrorAction SilentlyContinue
if ($null -eq $jpackage) {
    $jpackage = Get-Command 'jpackage' -ErrorAction SilentlyContinue
}
if ($null -eq $jpackage) {
    throw 'jpackage was not found. Run this script with JDK 24 or later on PATH.'
}

Push-Location $RepositoryPath
try {
    if (-not $SkipBuild) {
        & $maven.Path clean package
        if ($LASTEXITCODE -ne 0) {
            throw "Maven package failed with exit code $LASTEXITCODE"
        }
    }

    $target = Join-Path $RepositoryPath 'target'
    $input = Join-Path $target 'jpackage-input'
    $destination = Join-Path $target 'jpackage'
    Remove-Item -LiteralPath $input -Recurse -Force -ErrorAction SilentlyContinue
    New-Item -ItemType Directory -Path $input -Force | Out-Null
    New-Item -ItemType Directory -Path $destination -Force | Out-Null

    & $maven.Path dependency:copy-dependencies `
        "-DincludeScope=runtime" `
        "-DoutputDirectory=$input"
    if ($LASTEXITCODE -ne 0) {
        throw "Maven dependency copy failed with exit code $LASTEXITCODE"
    }

    $applicationJar = Get-ChildItem -LiteralPath $target -Filter '*.jar' -File |
        Where-Object {
            $_.Name -notmatch '(-sources|-javadoc|original-).*\.jar$' -and
            $_.DirectoryName -eq $target
        } |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1

    if ($null -eq $applicationJar) {
        throw 'Unable to locate the built OpenData application JAR in target.'
    }
    Copy-Item -LiteralPath $applicationJar.FullName -Destination $input -Force

    $compiledHelp = Join-Path $RepositoryPath `
        'docs\build\help\TechnicalUserGuide\OpenData-Technical-User-Guide.chm'
    if (Test-Path -LiteralPath $compiledHelp -PathType Leaf) {
        $helpDirectory = Join-Path $input 'help'
        New-Item -ItemType Directory -Path $helpDirectory -Force | Out-Null
        Copy-Item -LiteralPath $compiledHelp -Destination $helpDirectory -Force
        Write-Host "Included compiled Help: $compiledHelp"
    } else {
        Write-Warning 'Compiled CHM help was not found. The packaged GUI will use its built-in JavaFX help fallback.'
    }

    $icon = Join-Path $RepositoryPath 'src\main\resources\opendata-icon-multisize.ico'
    $cliProperties = Join-Path $target 'OpenData-CLI.properties'
    @(
        "main-jar=$($applicationJar.Name)"
        'main-class=com.towermarsh.opendata.OpenData'
        'win-console=true'
        'description=OpenData command-line interface'
        $(if (Test-Path -LiteralPath $icon) { "icon=$icon" })
    ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } |
        Set-Content -LiteralPath $cliProperties -Encoding UTF8

    $arguments = @(
        '--type', $Type,
        '--name', 'OpenData',
        '--app-version', $Version,
        '--description', 'OpenData Processing Framework',
        '--input', $input,
        '--dest', $destination,
        '--main-jar', $applicationJar.Name,
        '--main-class', 'com.towermarsh.opendata.OpenData',
        '--add-launcher', "OpenData-CLI=$cliProperties"
    )

    if (Test-Path -LiteralPath $icon) {
        $arguments += @('--icon', $icon)
    }

    if ($Type -in @('exe', 'msi')) {
        $license = Join-Path $RepositoryPath 'LICENSE'
        if (Test-Path -LiteralPath $license -PathType Leaf) {
            $arguments += @('--license-file', $license)
        }
        $arguments += @('--win-menu', '--win-shortcut')
    }

    Write-Host "Running jpackage --type $Type"
    & $jpackage.Path @arguments
    if ($LASTEXITCODE -ne 0) {
        throw "jpackage failed with exit code $LASTEXITCODE"
    }

    Write-Host "Package output: $destination"
} finally {
    Pop-Location
}
