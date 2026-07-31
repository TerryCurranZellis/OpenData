# Copyright © 2026 Terry Curran
# SPDX-License-Identifier: Apache-2.0

#Requires -Version 5.1
[CmdletBinding()]
param(
  [string[]]$Document = @('All'),
  [ValidateSet('Html','Docx','Pdf','All','None')][string]$Format = 'Html',
  [switch]$RenderDiagrams,
  [switch]$FailOnWarning
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
. (Join-Path $PSScriptRoot 'Invoke-Documentation.ps1')
Invoke-Documentation -Action Build -ProjectRoot $projectRoot -Document $Document -Format $Format -RenderDiagrams:$RenderDiagrams -FailOnWarning:$FailOnWarning
