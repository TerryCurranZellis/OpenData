# Copyright © 2026 Terry Curran
# SPDX-License-Identifier: Apache-2.0

#Requires -Version 5.1
function Test-Headers {
[CmdletBinding()]
param(
  [Parameter()]
  [string] $Path,

  [Parameter()]
  [string] $CsvPath,

  [Parameter()]
  [switch] $IncludeHiddenDirectories,

  [Parameter()]
  [switch] $AllowEmptyValues
)

$ErrorActionPreference = 'Stop'

if ([string]::IsNullOrWhiteSpace($Path)) {
  $projectRoot = Split-Path -Parent $PSScriptRoot
  $Path = Join-Path $projectRoot 'docs'
}

$resolvedRoot = (Resolve-Path -LiteralPath $Path).Path
$requiredFields = @('Document ID', 'Date', 'Version Number', 'Revision Date')

function New-HeaderFinding {
  param(
    [Parameter(Mandatory)] [System.IO.FileInfo] $File,
    [Parameter(Mandatory)] [string] $Code,
    [Parameter(Mandatory)] [ValidateSet('Error', 'Warning')] [string] $Severity,
    [Parameter(Mandatory)] [string] $Message,
    [Parameter()] [int] $Line = 0
  )

  [PSCustomObject]@{
    File = $File.FullName.Substring($resolvedRoot.Length).TrimStart('\', '/')
    Line = if ($Line -gt 0) { $Line } else { $null }
    Severity = $Severity
    Code = $Code
    Message = $Message
  }
}

function Get-MarkdownFiles {
  $files = Get-ChildItem -LiteralPath $resolvedRoot -Recurse -File -Filter '*.md'
  if ($IncludeHiddenDirectories) {
    return $files
  }

  return $files | Where-Object {
    $relativePath = $_.FullName.Substring($resolvedRoot.Length).TrimStart('\', '/')
    -not ($relativePath -split '[\\/]' | Where-Object { $_ -match '^\.' })
  }
}

function Test-DocumentHeader {
  param([Parameter(Mandatory)] [System.IO.FileInfo] $File)

  $findings = [System.Collections.Generic.List[object]]::new()
  [string[]] $lines = @(Get-Content -LiteralPath $File.FullName)

  $headingIndex = -1
  for ($index = 0; $index -lt $lines.Count; $index++) {
    if ($lines[$index] -match '^#(?!#)\s+\S') {
      $headingIndex = $index
      break
    }
  }

  if ($headingIndex -lt 0) {
    $findings.Add((New-HeaderFinding -File $File -Code 'MISSING_H1' -Severity Error `
      -Message 'No level-one Markdown heading was found.'))
    return $findings
  }

  $blockStart = $headingIndex + 1
  while ($blockStart -lt $lines.Count -and [string]::IsNullOrWhiteSpace($lines[$blockStart])) {
    $blockStart++
  }

  if ($blockStart -ge $lines.Count -or $lines[$blockStart].Trim() -ne '---') {
    $findings.Add((New-HeaderFinding -File $File -Code 'MISSING_OPENING_DELIMITER' -Severity Error `
      -Line ($headingIndex + 2) -Message 'The header block does not start with --- immediately after the first heading.'))

    $metadataLine = -1
    for ($index = 0; $index -lt $lines.Count; $index++) {
      if ($lines[$index] -match '(?i)^\s*(?:\*\*)?Document ID(?:\*\*)?\s*:') {
        $metadataLine = $index
        break
      }
    }
    if ($metadataLine -ge 0) {
      $findings.Add((New-HeaderFinding -File $File -Code 'MISPLACED_OR_LEGACY_BLOCK' -Severity Warning `
        -Line ($metadataLine + 1) -Message 'Document metadata exists, but it is not in the required delimited block.'))
    } else {
      $findings.Add((New-HeaderFinding -File $File -Code 'MISSING_HEADER_BLOCK' -Severity Error `
        -Line ($headingIndex + 2) -Message 'No document metadata block was found after the first heading.'))
    }
    return $findings
  }

  $blockEnd = -1
  for ($index = $blockStart + 1; $index -lt $lines.Count; $index++) {
    if ($lines[$index].Trim() -eq '---') {
      $blockEnd = $index
      break
    }
  }

  if ($blockEnd -lt 0) {
    $findings.Add((New-HeaderFinding -File $File -Code 'MISSING_CLOSING_DELIMITER' -Severity Error `
      -Line ($blockStart + 1) -Message 'The header block is not terminated by ---.'))
    return $findings
  }

  $values = @{}
  $fieldLines = @{}
  for ($index = $blockStart + 1; $index -lt $blockEnd; $index++) {
    $line = $lines[$index]
    if ($line -match '^([^:]+):\s*(.*?)\s*$') {
      $label = $Matches[1].Trim()
      $value = $Matches[2].Trim()
      if ($requiredFields -contains $label) {
        if ($values.ContainsKey($label)) {
          $findings.Add((New-HeaderFinding -File $File -Code 'DUPLICATE_FIELD' -Severity Error `
            -Line ($index + 1) -Message "Header field '$label' occurs more than once."))
        } else {
          $values[$label] = $value
          $fieldLines[$label] = $index + 1
        }
      } else {
        $findings.Add((New-HeaderFinding -File $File -Code 'UNEXPECTED_FIELD' -Severity Warning `
          -Line ($index + 1) -Message "Unexpected header field '$label'."))
      }
    } elseif (-not [string]::IsNullOrWhiteSpace($line)) {
      $findings.Add((New-HeaderFinding -File $File -Code 'INVALID_HEADER_LINE' -Severity Error `
        -Line ($index + 1) -Message 'Header lines must use Label: value syntax without Markdown bold markers.'))
    }
  }

  foreach ($field in $requiredFields) {
    if (-not $values.ContainsKey($field)) {
      $findings.Add((New-HeaderFinding -File $File -Code 'MISSING_FIELD' -Severity Error `
        -Line ($blockStart + 1) -Message "Required header field '$field' is missing."))
    } elseif (-not $AllowEmptyValues -and [string]::IsNullOrWhiteSpace([string]$values[$field])) {
      $findings.Add((New-HeaderFinding -File $File -Code 'EMPTY_VALUE' -Severity Warning `
        -Line $fieldLines[$field] -Message "Header field '$field' has no value."))
    }
  }

  if ($values.ContainsKey('Document ID')) {
    $expectedDocumentId = $File.Name
    if ($values['Document ID'] -ne $expectedDocumentId) {
      $findings.Add((New-HeaderFinding -File $File -Code 'DOCUMENT_ID_MISMATCH' -Severity Error `
        -Line $fieldLines['Document ID'] -Message "Document ID must equal the filename '$expectedDocumentId'."))
    }
  }

  foreach ($dateField in @('Date', 'Revision Date')) {
    if ($values.ContainsKey($dateField) -and -not [string]::IsNullOrWhiteSpace([string]$values[$dateField])) {
      $parsedDate = [DateTime]::MinValue
      $isValidDate = [DateTime]::TryParseExact(
        $values[$dateField],
        'yyyy-MM-dd',
        [Globalization.CultureInfo]::InvariantCulture,
        [Globalization.DateTimeStyles]::None,
        [ref] $parsedDate)
      if (-not $isValidDate) {
        $findings.Add((New-HeaderFinding -File $File -Code 'INVALID_DATE' -Severity Error `
          -Line $fieldLines[$dateField] -Message "Header field '$dateField' must use YYYY-MM-DD."))
      }
    }
  }

  $actualOrder = @($fieldLines.GetEnumerator() | Sort-Object Value | ForEach-Object Key)
  $expectedPresentOrder = @($requiredFields | Where-Object { $fieldLines.ContainsKey($_) })
  if (($actualOrder -join '|') -ne ($expectedPresentOrder -join '|')) {
    $findings.Add((New-HeaderFinding -File $File -Code 'FIELD_ORDER' -Severity Warning `
      -Line ($blockStart + 2) -Message "Header fields are not in the required order: $($requiredFields -join ', ')."))
  }

  return $findings
}

$files = @(Get-MarkdownFiles | Sort-Object FullName)
$allFindings = [System.Collections.Generic.List[object]]::new()
$inconsistentFiles = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::OrdinalIgnoreCase)

foreach ($file in $files) {
  $fileFindings = @(Test-DocumentHeader -File $file)
  foreach ($finding in $fileFindings) {
    $allFindings.Add($finding)
    [void] $inconsistentFiles.Add($finding.File)
  }
}

if ($allFindings.Count -gt 0) {
  $allFindings | Sort-Object File, Line, Severity, Code | Format-Table File, Line, Severity, Code, Message -AutoSize -Wrap
} else {
  Write-Host 'All Markdown documents have a consistent header block.' -ForegroundColor Green
}

Write-Host ''
Write-Host ("Documents checked : {0}" -f $files.Count)
Write-Host ("Consistent        : {0}" -f ($files.Count - $inconsistentFiles.Count))
Write-Host ("Inconsistent      : {0}" -f $inconsistentFiles.Count)
Write-Host ("Errors            : {0}" -f @($allFindings | Where-Object Severity -eq 'Error').Count)
Write-Host ("Warnings          : {0}" -f @($allFindings | Where-Object Severity -eq 'Warning').Count)

if (-not [string]::IsNullOrWhiteSpace($CsvPath)) {
  $csvParent = Split-Path -Parent $CsvPath
  if (-not [string]::IsNullOrWhiteSpace($csvParent) -and -not (Test-Path -LiteralPath $csvParent)) {
    New-Item -ItemType Directory -Path $csvParent -Force | Out-Null
  }
  $allFindings | Sort-Object File, Line, Severity, Code |
    Export-Csv -LiteralPath $CsvPath -NoTypeInformation -Encoding UTF8
  Write-Host ("CSV report        : {0}" -f (Resolve-Path -LiteralPath $CsvPath).Path)
}

if ($inconsistentFiles.Count -gt 0) {
  exit 1
}

exit 0
}
Test-Headers -Path 'C:\Users\terry\Documents\NetBeansProjects\opendata' -CsvPath "C:\Users\terry\Documents\NetBeansProjects\opendata\headers.csv"

import-csv -Path "C:\Users\terry\Documents\NetBeansProjects\opendata\headers.csv" | out-gridview