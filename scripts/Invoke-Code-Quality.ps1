# Copyright © 2026 Terry Curran
# SPDX-License-Identifier: Apache-2.0

[CmdletBinding()]
param(
    [switch]$Strict,
    [switch]$SkipTests
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location $projectRoot
try {
    $arguments = @('clean')
    if (-not $SkipTests) { $arguments += 'test' }
    $arguments += 'verify'
    if ($Strict) { $arguments += '-Dquality.failOnViolation=true' }

    Write-Host "Running: mvn $($arguments -join ' ')"
    & mvn @arguments
    if ($LASTEXITCODE -ne 0) { throw "Maven quality verification failed with exit code $LASTEXITCODE." }
}
finally {
    Pop-Location
}
