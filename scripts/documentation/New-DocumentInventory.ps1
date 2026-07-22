[CmdletBinding()]
param(
    [string]$ProjectRoot,
    [string]$OutputPath
)

. (Join-Path $PSScriptRoot 'Common.ps1')
if ([string]::IsNullOrWhiteSpace($ProjectRoot)) { $ProjectRoot = Resolve-ProjectRoot }
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $ProjectRoot 'docs\build\document-inventory.md'
}
Ensure-Directory -Path (Split-Path -Parent $OutputPath)

$files = & (Join-Path $PSScriptRoot 'Get-DocumentationFiles.ps1') -ProjectRoot $ProjectRoot
$lines = New-Object System.Collections.Generic.List[string]
$lines.Add('# Document Inventory')
$lines.Add('')
$lines.Add('| Document | First heading | Last modified |')
$lines.Add('|---|---|---|')
foreach ($file in $files) {
    $firstHeading = Get-Content -LiteralPath $file.FullName -Encoding UTF8 |
        Where-Object { $_ -match '^#\s+' } |
        Select-Object -First 1
    if ($null -eq $firstHeading) { $firstHeading = '(No level-one heading)' }
    else { $firstHeading = $firstHeading -replace '^#\s+', '' }
    $relative = $file.FullName.Substring($ProjectRoot.Length).TrimStart('\','/') -replace '\\','/'
    $lines.Add("| [$($file.Name)](../../$relative) | $firstHeading | $($file.LastWriteTime.ToString('yyyy-MM-dd')) |")
}
$lines | Set-Content -LiteralPath $OutputPath -Encoding UTF8
Write-Output $OutputPath
