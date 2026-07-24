[CmdletBinding()]
param(
  [string]$ProjectRoot,
  [string]$OutputPath
)

. (Join-Path -Path $PSScriptRoot -ChildPath 'Common.ps1')
if ([string]::IsNullOrWhiteSpace($ProjectRoot)) { 
  $ProjectRoot = Resolve-ProjectRoot 
}
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
  $OutputPath = Join-Path -Path $ProjectRoot -ChildPath 'docs\build\OpenData-Manual.md'
}
Ensure-Directory -Path (Split-Path -Parent -Path $OutputPath)

$frontMatter = Join-Path -Path $ProjectRoot -ChildPath 'docs\_includes\manual-front-matter.md'
$files = & (Join-Path -Path $PSScriptRoot -ChildPath 'Get-DocumentationFiles.ps1') -ProjectRoot $ProjectRoot

$writer = New-Object -TypeName System.IO.StreamWriter -ArgumentList ($OutputPath, $false, (New-Object -TypeName System.Text.UTF8Encoding -ArgumentList ($false)))
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
finally { 
  $writer.Dispose() 
}
Write-Output -InputObject $OutputPath
