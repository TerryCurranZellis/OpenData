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
  $path = Join-Path -Path $ProjectRoot -ChildPath $folder
  if (-not (Test-Path -LiteralPath $path)) { 
    $null = New-Item -ItemType Directory -Path $path -Force 
  }
}
Write-Output -InputObject ('Documentation structure created below {0}' -f $ProjectRoot)
