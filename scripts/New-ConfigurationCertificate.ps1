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
    [string]$ProjectRoot = (Resolve-Path -Path (Join-Path -Path $PSScriptRoot -ChildPath '..')).Path,
    [string]$Subject = 'CN=OpenData Configuration Encryption',
    [AllowNull()]
    [string]$OutputDirectory,
    [switch]$Force,
    [switch]$RemoveFromStore
  )

  if (-not $OutputDirectory)
  {
    $OutputDirectory = Join-Path -Path $ProjectRoot -ChildPath 'src\main\resources\config\security'
  }

  $resolvedOutputDirectory = [System.IO.Path]::GetFullPath($OutputDirectory)
  $publicCertificatePath = Join-Path -Path $resolvedOutputDirectory -ChildPath 'opendata-config-public.cer'
  $privateStorePath = Join-Path -Path $resolvedOutputDirectory -ChildPath 'opendata-config-private.pfx'

  if ((((Test-Path -Path $publicCertificatePath -PathType Leaf -ErrorAction SilentlyContinue) -or
      (Test-Path -Path $privateStorePath -PathType Leaf -ErrorAction SilentlyContinue)) -and
  -not $Force))
  {
    throw 'Certificate files already exist. Re-run with -Force to overwrite them.'
  }

  if ($PSCmdlet.ShouldProcess($resolvedOutputDirectory, 'Create OpenData configuration certificate files'))
  {
    $null = New-Item -Path $resolvedOutputDirectory -ItemType Directory -Force

    try{
      $Parameters = @{
        Subject = $Subject
        CertStoreLocation = 'Cert:\LocalMachine\My'
        KeyAlgorithm      = 'RSA'
        KeyLength         = 2048 
        HashAlgorithm     = 'SHA256'
        KeyExportPolicy   = 'Exportable' 
        KeySpec           = 'KeyExchange'
        NotAfter          = (Get-Date).AddYears(5)
        FriendlyName      = 'OpenData Configuration Encryption'
        Provider = 'Microsoft Strong Cryptographic Provider'
      }
      $certificate = New-SelfSignedCertificate @Parameters
            
      Export-Certificate  -Cert $certificate -FilePath $publicCertificatePath

      $pfxParameters = @{
        Cert = $certificate
        FilePath = $privateStorePath
        Password = (ConvertTo-SecureString -String 'nopassword' -AsPlainText -Force)
      }
      $null = Export-PfxCertificate @pfxParameters 

      if ($RemoveFromStore)
      {
      <#
        Get-ChildItem -Path Cert:\LocalMachine\My |
            Where-Object { $_.Subject -eq 'CN=OpenData Configuration Encryption' } |
            Remove-Item
      #>
        Remove-Item -Path ('Cert:\LocalMachine\My' + $certificate.Thumbprint) -Force
      }

      Write-Output -InputObject ('Created: {0}' -f $publicCertificatePath)
      Write-Output -InputObject ('Created: {0}' -f $privateStorePath)
    } catch {
      Write-Warning -Message ('error {0}' -f $_)
    }
  }
}

$ProjectRoot = 'C:\Users\terry\Documents\NetBeansProjects\opendata'

New-ConfigurationCertificate -ProjectRoot $ProjectRoot -force -RemoveFromStore