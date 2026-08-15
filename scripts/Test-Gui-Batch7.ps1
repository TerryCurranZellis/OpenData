<#
.SYNOPSIS
Runs Batch 7 verification checks against an OpenData working tree.
#>
[CmdletBinding()]
param(
    [Parameter()]
    [string] $RepositoryPath = (Get-Location).Path,

    [Parameter()]
    [switch] $SkipMaven
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$RepositoryPath = (Resolve-Path -LiteralPath $RepositoryPath).Path

function Assert-True {
    param(
        [Parameter(Mandatory)][bool] $Condition,
        [Parameter(Mandatory)][string] $Message
    )
    if (-not $Condition) {
        throw $Message
    }
}

$applicationInfo = Join-Path $RepositoryPath `
    'src\main\java\com\towermarsh\opendata\app\ApplicationInfo.java'
$legacyInfo = Join-Path $RepositoryPath `
    'src\main\java\com\towermarsh\opendata\ui\ApplicationInfo.java'
$openData = Join-Path $RepositoryPath `
    'src\main\java\com\towermarsh\opendata\OpenData.java'

Assert-True (Test-Path -LiteralPath $applicationInfo -PathType Leaf) `
    'ApplicationInfo.java was not moved to com.towermarsh.opendata.app.'
Assert-True (-not (Test-Path -LiteralPath $legacyInfo)) `
    'Legacy ui/ApplicationInfo.java still exists.'
Assert-True (Test-Path -LiteralPath $openData -PathType Leaf) `
    'OpenData.java was not found.'

$openDataText = Get-Content -LiteralPath $openData -Raw
Assert-True ($openDataText -notmatch 'StartupSplashScreen') `
    'OpenData.java still references the retired Swing StartupSplashScreen.'

$javaFiles = Get-ChildItem -LiteralPath (Join-Path $RepositoryPath 'src') `
    -Filter '*.java' -File -Recurse
$legacyImports = $javaFiles | Select-String -Pattern 'com\.towermarsh\.opendata\.ui\.'
if ($legacyImports) {
    Write-Warning 'Legacy com.towermarsh.opendata.ui references remain:'
    $legacyImports | ForEach-Object { Write-Warning $_.Line.Trim() }
}

$swingReferences = $javaFiles | Select-String -Pattern 'javax\.swing|java\.awt'
if ($swingReferences) {
    Write-Warning 'Swing/AWT references remain and should be reviewed:'
    $swingReferences | ForEach-Object { Write-Warning ("{0}:{1}: {2}" -f $_.Path, $_.LineNumber, $_.Line.Trim()) }
} else {
    Write-Host 'No Swing/AWT references remain in Java source.'
}

$deprecatedUiClasses = @(
    'src\main\java\com\towermarsh\opendata\ui\StartupSplashScreen.java',
    'src\main\java\com\towermarsh\opendata\ui\OpenDataImageLoader.java',
    'src\main\java\com\towermarsh\opendata\ui\AboutDialog.java',
    'src\main\java\com\towermarsh\opendata\ui\GuiLauncher.java'
)
foreach ($relative in $deprecatedUiClasses) {
    Assert-True (-not (Test-Path -LiteralPath (Join-Path $RepositoryPath $relative))) `
        "Deprecated UI compatibility source still exists: $relative"
}

if (-not $SkipMaven) {
    $maven = Get-Command 'mvn.cmd' -ErrorAction SilentlyContinue
    if ($null -eq $maven) {
        $maven = Get-Command 'mvn' -ErrorAction SilentlyContinue
    }
    if ($null -eq $maven) {
        throw 'Maven was not found on PATH.'
    }
    Push-Location $RepositoryPath
    try {
        & $maven.Path test
        if ($LASTEXITCODE -ne 0) {
            throw "Maven tests failed with exit code $LASTEXITCODE"
        }
    } finally {
        Pop-Location
    }
}

$help = Join-Path $RepositoryPath `
    'docs\build\help\TechnicalUserGuide\OpenData-Technical-User-Guide.chm'
if (Test-Path -LiteralPath $help -PathType Leaf) {
    Write-Host "Compiled Windows Help found: $help"
} else {
    Write-Warning 'Compiled Windows Help is not currently present; build the Technical User Guide help output before final packaging.'
}

Write-Host 'Batch 7 structural checks completed successfully.'
