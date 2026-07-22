[CmdletBinding(SupportsShouldProcess=$true)]
param([string]$ProjectRoot)

. (Join-Path $PSScriptRoot 'Common.ps1')
if ([string]::IsNullOrWhiteSpace($ProjectRoot)) { $ProjectRoot = Resolve-ProjectRoot }
$config = Read-DocumentationConfig -ProjectRoot $ProjectRoot
foreach ($relative in @($config.buildDirectory, $config.diagramOutputDirectory)) {
    $path = Join-Path $ProjectRoot $relative
    if (Test-Path -LiteralPath $path) {
        if ($PSCmdlet.ShouldProcess($path, 'Remove generated documentation')) {
            Get-ChildItem -LiteralPath $path -Force | Remove-Item -Recurse -Force
        }
    }
}
