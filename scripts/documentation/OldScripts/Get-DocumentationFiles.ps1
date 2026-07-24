[CmdletBinding()]
param(
  [string]$ProjectRoot
)

. (Join-Path -Path $PSScriptRoot -ChildPath 'Common.ps1')
if ([string]::IsNullOrWhiteSpace($ProjectRoot)) { $ProjectRoot = Resolve-ProjectRoot }
$config = Read-DocumentationConfig -ProjectRoot $ProjectRoot
$docsRoot = Join-Path -Path $ProjectRoot -ChildPath $config.sourceDirectory

$orderedDirectories = @('architecture','development','standards','guides','reference','roadmap','decisions')
$files = New-Object -TypeName System.Collections.Generic.List[System.IO.FileInfo]

$rootReadme = Join-Path -Path $docsRoot -ChildPath 'README.md'
if (Test-Path -LiteralPath $rootReadme) { $files.Add((Get-Item -LiteralPath $rootReadme)) }

foreach ($directory in $orderedDirectories) {
  $path = Join-Path -Path $docsRoot -ChildPath $directory
  if (Test-Path -LiteralPath $path) {
    Get-ChildItem -LiteralPath $path -File -Filter '*.md' |
    Sort-Object -Property Name |
    ForEach-Object { $files.Add($_) }
  }
}

$files
