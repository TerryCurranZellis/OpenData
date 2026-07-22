[CmdletBinding()]
param(
    [string]$ProjectRoot = (Get-Location).Path,
    [switch]$Force
)

$folders = @(
    'docs\_templates','docs\_includes','docs\architecture','docs\development',
    'docs\standards','docs\guides','docs\reference','docs\roadmap','docs\decisions',
    'docs\diagrams\source','docs\diagrams\generated','docs\build',
    'scripts\documentation','config','tools'
)
foreach ($folder in $folders) {
    $path = Join-Path $ProjectRoot $folder
    if (-not (Test-Path -LiteralPath $path)) { New-Item -ItemType Directory -Path $path -Force | Out-Null }
}
Write-Host "Documentation structure created below $ProjectRoot"
