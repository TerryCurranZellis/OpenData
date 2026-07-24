[CmdletBinding()]
param(
  [Parameter(Mandatory)]
  [string]$ProjectRoot,
  [Parameter(Mandatory)]
  [string]$OutputPath
)

. (Join-Path -Path $PSScriptRoot -ChildPath 'Common.ps1')
if ([string]::IsNullOrWhiteSpace($ProjectRoot)) { 
  $ProjectRoot = Resolve-ProjectRoot 
}
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
  $OutputPath = Join-Path -Path $ProjectRoot -ChildPath 'docs\build\document-inventory.md'
}
Ensure-Directory -Path (Split-Path -Parent -Path $OutputPath)

$files = & (Join-Path -Path $PSScriptRoot -ChildPath 'Get-DocumentationFiles.ps1') -ProjectRoot $ProjectRoot
$lines = New-Object -TypeName System.Collections.Generic.List[string]
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
  $lines.Add(('| [{0}](../../{1}) | {2} | {3} |' -f $file.Name, $relative, $firstHeading, $file.LastWriteTime.ToString('yyyy-MM-dd')))
}
$lines | Set-Content -LiteralPath $OutputPath -Encoding UTF8
Write-Output -InputObject $OutputPath
