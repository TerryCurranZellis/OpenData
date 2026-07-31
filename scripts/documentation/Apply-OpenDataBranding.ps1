<#
Copyright © 2026 Terry Curran
SPDX-License-Identifier: Apache-2.0
#>

#Requires -Version 5.1
<#
.SYNOPSIS
Reports that the legacy cover-page patch is no longer required.

.DESCRIPTION
Cover-page composition and branding are now controlled by document manifests,
shared Markdown and the generic documentation engine. This compatibility stub
intentionally makes no changes and prevents the retired patch from modifying
scripts\Invoke-Documentation.ps1.
#>
[CmdletBinding()]
param(
  [string] $ProjectRoot = (Split-Path -Parent (Split-Path -Parent $PSScriptRoot))
)

Write-Warning 'Apply-OpenDataBranding.ps1 is obsolete. No files were changed; branding and cover-page placement are manifest-driven.'
