[CmdletBinding()]
param(
    [string]$ProjectRoot,
    [switch]$FailOnWarning
)

. (Join-Path $PSScriptRoot 'Common.ps1')
if ([string]::IsNullOrWhiteSpace($ProjectRoot)) { $ProjectRoot = Resolve-ProjectRoot }
$files = & (Join-Path $PSScriptRoot 'Get-DocumentationFiles.ps1') -ProjectRoot $ProjectRoot
$issues = New-Object System.Collections.Generic.List[object]

foreach ($file in $files) {
    $content = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8
    if ($content -notmatch '(?m)^#\s+\S') {
        $issues.Add([pscustomobject]@{ Severity='Error'; File=$file.FullName; Message='Missing level-one heading.' })
    }
    if ($content -match '\t') {
        $issues.Add([pscustomobject]@{ Severity='Warning'; File=$file.FullName; Message='Tab character found.' })
    }
    $matches = [regex]::Matches($content, '\[[^\]]+\]\(([^)#]+)(?:#[^)]+)?\)')
    foreach ($match in $matches) {
        $target = $match.Groups[1].Value
        if ($target -match '^(https?:|mailto:|#)') { continue }
        $decoded = [System.Uri]::UnescapeDataString($target)
        $path = Join-Path $file.DirectoryName ($decoded -replace '/', '\')
        if (-not (Test-Path -LiteralPath $path)) {
            $issues.Add([pscustomobject]@{ Severity='Error'; File=$file.FullName; Message="Broken relative link: $target" })
        }
    }
}

if ($issues.Count -gt 0) { $issues | Format-Table -AutoSize | Out-String | Write-Host }
$errorCount = @($issues | Where-Object Severity -eq 'Error').Count
$warningCount = @($issues | Where-Object Severity -eq 'Warning').Count
Write-Host "Documentation validation completed: $errorCount error(s), $warningCount warning(s)."
if ($errorCount -gt 0 -or ($FailOnWarning -and $warningCount -gt 0)) { exit 1 }
