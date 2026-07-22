[CmdletBinding()]
param(
    [string]$ProjectRoot,
    [ValidateSet('svg','png')][string]$Format = 'svg',
    [switch]$Clean
)

. (Join-Path $PSScriptRoot 'Common.ps1')
if ([string]::IsNullOrWhiteSpace($ProjectRoot)) { $ProjectRoot = Resolve-ProjectRoot }
$config = Read-DocumentationConfig -ProjectRoot $ProjectRoot
$source = Join-Path $ProjectRoot $config.diagramSourceDirectory
$output = Join-Path $ProjectRoot $config.diagramOutputDirectory
$jar = Join-Path $ProjectRoot $config.plantUmlJar
Ensure-Directory -Path $output

if ($Clean) {
    Get-ChildItem -LiteralPath $output -File -ErrorAction SilentlyContinue | Remove-Item -Force
}
if (-not (Test-Path -LiteralPath $jar)) {
    throw "PlantUML JAR not found at '$jar'. Download plantuml.jar and place it in the tools folder."
}
Assert-CommandAvailable -Name 'java'

$diagramFiles = Get-ChildItem -LiteralPath $source -Filter '*.puml' -File -Recurse
foreach ($diagram in $diagramFiles) {
    & java -jar $jar "-t$Format" -charset UTF-8 -o $output $diagram.FullName
    if ($LASTEXITCODE -ne 0) { throw "PlantUML failed for $($diagram.FullName)." }
}
Write-Output "Rendered $($diagramFiles.Count) diagram(s) to $output"
