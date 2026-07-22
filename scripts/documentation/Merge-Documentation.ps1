[CmdletBinding()]
param(
    [string]$ProjectRoot,
    [string]$OutputPath
)

. (Join-Path $PSScriptRoot 'Common.ps1')
if ([string]::IsNullOrWhiteSpace($ProjectRoot)) { $ProjectRoot = Resolve-ProjectRoot }
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $ProjectRoot 'docs\build\OpenData-Manual.md'
}
Ensure-Directory -Path (Split-Path -Parent $OutputPath)

$frontMatter = Join-Path $ProjectRoot 'docs\_includes\manual-front-matter.md'
$files = & (Join-Path $PSScriptRoot 'Get-DocumentationFiles.ps1') -ProjectRoot $ProjectRoot

$writer = New-Object System.IO.StreamWriter($OutputPath, $false, (New-Object System.Text.UTF8Encoding($false)))
try {
    if (Test-Path -LiteralPath $frontMatter) {
        $writer.WriteLine((Get-Content -LiteralPath $frontMatter -Raw -Encoding UTF8))
        $writer.WriteLine()
        $writer.WriteLine('\\newpage')
        $writer.WriteLine()
    }
    foreach ($file in $files) {
        $content = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8
        $content = $content -replace '(?ms)^---\s*\r?\n.*?\r?\n---\s*\r?\n', ''
        $writer.WriteLine($content.Trim())
        $writer.WriteLine()
        $writer.WriteLine('\\newpage')
        $writer.WriteLine()
    }
}
finally { $writer.Dispose() }
Write-Output $OutputPath
