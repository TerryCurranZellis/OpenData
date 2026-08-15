# Copyright © 2026 Terry Curran
# SPDX-License-Identifier: Apache-2.0

#Requires -Version 5.1
[CmdletBinding()]
param(
    [string[]] $Document = @('All'),

    [ValidateSet('All', 'Html', 'Docx', 'Pdf', 'Chm', 'None')]
    [string] $Format = 'All',

    [AllowNull()]
    [string] $ReferenceDoc,

    [switch] $RenderDiagrams,

    [switch] $FailOnWarning
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
. (Join-Path $PSScriptRoot 'Invoke-Documentation.ps1')

$parameters = @{
    Action        = 'Build'
    ProjectRoot   = $projectRoot
    Document      = $Document
    Format        = $Format
    FailOnWarning = $FailOnWarning
}
if ($PSBoundParameters.ContainsKey('ReferenceDoc')) {
    $parameters.ReferenceDoc = $ReferenceDoc
}
if ($RenderDiagrams) {
    $parameters.RenderDiagrams = $true
}

Invoke-Documentation @parameters
