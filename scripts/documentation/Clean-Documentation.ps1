[CmdletBinding(SupportsShouldProcess=$true)]
param(
  [Parameter(Mandatory=$true)]
  [string]$ProjectRoot
)

. (Join-Path -Path $PSScriptRoot -ChildPath 'Common.ps1')
if ([string]::IsNullOrWhiteSpace($ProjectRoot)) { $ProjectRoot = Resolve-ProjectRoot }
$config = Read-DocumentationConfig -ProjectRoot $ProjectRoot
foreach ($relative in @($config.buildDirectory, $config.diagramOutputDirectory)) {
  $path = Join-Path -Path $ProjectRoot -ChildPath $relative
  if (Test-Path -LiteralPath $path) {
    if ($PSCmdlet.ShouldProcess($path, 'Remove generated documentation')) {
      Get-ChildItem -LiteralPath $path -Force | Remove-Item -Recurse -Force
    }
  }
}
