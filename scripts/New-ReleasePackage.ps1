# Copyright © 2026 Terry Curran
# SPDX-License-Identifier: Apache-2.0

#Requires -Version 5.1
[CmdletBinding()]
param(
  [Parameter(Mandatory)][ValidatePattern('^\d+\.\d+\.\d+([-.][0-9A-Za-z.-]+)?$')][string]$Version,
  [switch]$SkipQuality
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location $projectRoot
try {
  $declared = (& mvn help:evaluate -Dexpression=project.version -q -DforceStdout).Trim()
  if ($LASTEXITCODE -ne 0) { throw 'Unable to read the Maven project version.' }
  if ($declared -ne $Version) { throw "pom.xml version '$declared' does not match requested release '$Version'." }
  $args = @('--batch-mode','--no-transfer-progress','clean','verify','package')
  if (-not $SkipQuality) { $args += '-Dquality.failOnViolation=true' }
  & mvn @args
  if ($LASTEXITCODE -ne 0) { throw 'Maven release verification failed.' }
  $releaseDir = Join-Path $projectRoot 'target/release'
  New-Item -ItemType Directory -Path $releaseDir -Force | Out-Null
  $archive = Join-Path $releaseDir "OpenData-$Version-source.zip"
  if (Test-Path $archive) { Remove-Item $archive -Force }
  & git archive --format=zip --prefix="OpenData-$Version/" --output=$archive HEAD
  if ($LASTEXITCODE -ne 0) { throw 'Unable to create source archive with git archive.' }
  Get-ChildItem (Join-Path $projectRoot 'target') -Filter '*.jar' -File | Copy-Item -Destination $releaseDir -Force
  Get-ChildItem $releaseDir -File | ForEach-Object {
    $hash = Get-FileHash $_.FullName -Algorithm SHA256
    "{0}  {1}" -f $hash.Hash.ToLowerInvariant(), $_.Name
  } | Set-Content (Join-Path $releaseDir 'SHA256SUMS.txt') -Encoding ASCII
  Write-Host "Release package created in $releaseDir"
} finally { Pop-Location }
