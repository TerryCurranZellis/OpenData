# Copyright (c) 2026. SPDX-License-Identifier: Apache-2.0
# Run from the extracted OpenData project root, after applying the patch ZIP.
[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$newTest = Join-Path $root 'opendata-core\src\test\java\com\towermarsh\opendata\core\plugin\AllPluginsRegistrationTest.java'
$oldTest = Join-Path $root 'opendata-core\src\test\java\com\towermarsh\opendata\plugin\AllPluginsRegistrationTest.java'
$moduleInfo = Join-Path $root 'opendata-core\src\main\java\module-info.java'

if (-not (Test-Path -LiteralPath $newTest -PathType Leaf)) {
    throw "Patch not installed: new Core test file does not exist: $newTest"
}
if (-not (Test-Path -LiteralPath $moduleInfo -PathType Leaf)) {
    throw "Patch not installed: Core module descriptor does not exist: $moduleInfo"
}
$descriptor = Get-Content -LiteralPath $moduleInfo -Raw
if ($descriptor -notmatch 'requires\s+static\s+jdk\.httpserver\s*;') {
    throw 'Patch not installed: requires static jdk.httpserver; is missing.'
}
$newContent = Get-Content -LiteralPath $newTest -Raw
if ($newContent -notmatch 'package\s+com\.towermarsh\.opendata\.core\.plugin\s*;') {
    throw 'New test has the wrong Java package.'
}

if (Test-Path -LiteralPath $oldTest -PathType Leaf) {
    $oldContent = Get-Content -LiteralPath $oldTest -Raw
    if ($oldContent -match 'package\s+com\.towermarsh\.opendata\.plugin\s*;') {
        Remove-Item -LiteralPath $oldTest -Force
        Write-Host "Removed obsolete split-package test: $oldTest"
    } else {
        Write-Warning "The legacy test was modified; preserving it for manual inspection: $oldTest"
    }
}
Write-Host 'OpenData 4.0.0 test compilation fix applied.'
Write-Host 'Next run: mvn -pl opendata-core -am test'
