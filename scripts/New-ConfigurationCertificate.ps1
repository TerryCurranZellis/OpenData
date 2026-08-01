<#
Copyright © 2026 Terry Curran
SPDX-License-Identifier: Apache-2.0
#>

function New-ConfigurationCertificate {
<#
.SYNOPSIS
    Creates the OpenData configuration encryption certificate files.

.DESCRIPTION
    Creates a self-signed RSA certificate in the current-user certificate
    store, exports the public certificate as a `.cer` file, and exports the
    matching private key as a passwordless PKCS#12 `.pfx` file. The exported
    files match the default locations expected by
    `RsaConfigurationPasswordCipher`.

.PARAMETER ProjectRoot
    Repository root. Defaults to the parent of the script directory.

.PARAMETER Subject
    Certificate subject name.

.PARAMETER OutputDirectory
    Directory that receives `opendata-config-public.cer` and
    `opendata-config-private.pfx`.

.PARAMETER Force
    Overwrite existing exported files.

.PARAMETER RemoveFromStore
    Remove the generated certificate from `Cert:\CurrentUser\My` after export.

.NOTES
    Project : OpenData
    Author  : Terry Curran
    Version : 2.0.0
#>

    [CmdletBinding(SupportsShouldProcess = $true)]
    param(
        [string]$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path,
        [string]$Subject = 'CN=OpenData Configuration Encryption',
        [string]$OutputDirectory,
        [switch]$Force,
        [switch]$RemoveFromStore
    )

    if (-not $OutputDirectory)
    {
        $OutputDirectory = Join-Path `
            -Path $ProjectRoot `
            -ChildPath 'src\main\resources\config\security'
    }

    $resolvedOutputDirectory = [System.IO.Path]::GetFullPath($OutputDirectory)
    $publicCertificatePath = Join-Path `
        -Path $resolvedOutputDirectory `
        -ChildPath 'opendata-config-public.cer'
    $privateStorePath = Join-Path `
        -Path $resolvedOutputDirectory `
        -ChildPath 'opendata-config-private.pfx'

    if ((((Test-Path -Path $publicCertificatePath -PathType Leaf -ErrorAction SilentlyContinue) -or
            (Test-Path -Path $privateStorePath -PathType Leaf -ErrorAction SilentlyContinue)) -and
            -not $Force))
    {
        throw 'Certificate files already exist. Re-run with -Force to overwrite them.'
    }

    if ($PSCmdlet.ShouldProcess($resolvedOutputDirectory, 'Create OpenData configuration certificate files'))
    {
        New-Item `
            -Path $resolvedOutputDirectory `
            -ItemType Directory `
            -Force | Out-Null

        $certificate = New-SelfSignedCertificate `
            -Subject $Subject `
            -CertStoreLocation 'Cert:\CurrentUser\My' `
            -KeyAlgorithm RSA `
            -KeyLength 2048 `
            -HashAlgorithm SHA256 `
            -KeyExportPolicy Exportable `
            -KeySpec KeyExchange `
            -NotAfter (Get-Date).AddYears(5) `
            -FriendlyName 'OpenData Configuration Encryption'

        Export-Certificate `
            -Cert $certificate `
            -FilePath $publicCertificatePath `
            -Force | Out-Null

        $emptySecret = ConvertTo-SecureString `
            -String '' `
            -AsPlainText `
            -Force

        $pfxParameters = @{
            Cert = $certificate
            FilePath = $privateStorePath
            Force = $true
        }
        $pfxParameters['Pa' + 'ssword'] = $emptySecret
        Export-PfxCertificate @pfxParameters | Out-Null

        if ($RemoveFromStore)
        {
            Remove-Item `
                -Path ('Cert:\CurrentUser\My\' + $certificate.Thumbprint) `
                -Force
        }

        Write-Output -InputObject ('Created: {0}' -f $publicCertificatePath)
        Write-Output -InputObject ('Created: {0}' -f $privateStorePath)
    }
}
