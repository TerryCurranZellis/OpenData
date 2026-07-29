# Copyright © 2026 Terry Curran
# SPDX-License-Identifier: Apache-2.0

#Requires -Version 5.1
[CmdletBinding()]
param([switch]$FailOnWarning)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
. (Join-Path $PSScriptRoot 'Invoke-Documentation.ps1')
Invoke-Documentation -Action Test -ProjectRoot $projectRoot -FailOnWarning:$FailOnWarning
