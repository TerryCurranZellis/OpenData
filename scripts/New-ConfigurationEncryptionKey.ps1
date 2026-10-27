<#
    Copyright © 2026 Terry Curran
    SPDX-License-Identifier: Apache-2.0

    Creates a 256-bit AES key file OUTSIDE the OpenData repository.
    Compatible with Windows PowerShell 5.1.

    Example:
      .\scripts\New-ConfigurationEncryptionKey.ps1 -Path 'C:\ProgramData\OpenData\security\database.key'

    Restrict access to the resulting file with NTFS ACLs so only the
    OpenData service account and administrators can read it.
#>
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string] $Path
)

$fullPath = [System.IO.Path]::GetFullPath($Path)
if (Test-Path -LiteralPath $fullPath -PathType Leaf) {
    throw "Key file already exists; refusing to replace it: $fullPath"
}
$folder = [System.IO.Path]::GetDirectoryName($fullPath)
if (-not (Test-Path -LiteralPath $folder -PathType Container)) {
    $null = New-Item -ItemType Directory -Path $folder -Force
}
$key = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
try {
    $rng.GetBytes($key)
    $base64 = [Convert]::ToBase64String($key)
    [System.IO.File]::WriteAllText(
        $fullPath, $base64, ([System.Text.UTF8Encoding]::new($false)))
} finally {
    [Array]::Clear($key, 0, $key.Length)
    $rng.Dispose()
}
Write-Host "AES key created at: $fullPath"
Write-Warning 'Restrict NTFS permissions before running OpenData; do not commit or distribute this file.'
