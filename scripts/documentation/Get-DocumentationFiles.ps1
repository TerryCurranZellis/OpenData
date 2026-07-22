[CmdletBinding()]
param(
    [string]$ProjectRoot
)

. (Join-Path $PSScriptRoot 'Common.ps1')
if ([string]::IsNullOrWhiteSpace($ProjectRoot)) { $ProjectRoot = Resolve-ProjectRoot }
$config = Read-DocumentationConfig -ProjectRoot $ProjectRoot
$docsRoot = Join-Path $ProjectRoot $config.sourceDirectory

$orderedDirectories = @('architecture','development','standards','guides','reference','roadmap','decisions')
$files = New-Object System.Collections.Generic.List[System.IO.FileInfo]

$rootReadme = Join-Path $docsRoot 'README.md'
if (Test-Path -LiteralPath $rootReadme) { $files.Add((Get-Item -LiteralPath $rootReadme)) }

foreach ($directory in $orderedDirectories) {
    $path = Join-Path $docsRoot $directory
    if (Test-Path -LiteralPath $path) {
        Get-ChildItem -LiteralPath $path -File -Filter '*.md' |
            Sort-Object Name |
            ForEach-Object { $files.Add($_) }
    }
}

$files
