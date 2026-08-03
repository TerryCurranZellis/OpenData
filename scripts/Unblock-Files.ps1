<#
Copyright © 2026 Terry Curran
SPDX-License-Identifier: Apache-2.0
#>

#Requires -Version 5.1

$Path = 'C:\Users\terry\Documents\NetBeansProjects\opendata'


$ErrorActionPreference = 'Stop'
if (-not (Test-Path -LiteralPath $Path -PathType Container)) {
    throw "Folder was not found: $Path"
}
Get-ChildItem -LiteralPath $Path -Recurse -File | Unblock-File
Write-Host "Unblocked files beneath $Path"
