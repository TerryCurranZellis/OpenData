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
      matching private key as a password protected PKCS#12 `.pfx` file. The exported
      files match the default locations expected by
      `RsaConfigurationPasswordCipher`.
	  The password for the exported PKC#12 file is 'nopassword' as it will not allow
	  a blank password.
      .PARAMETER ProjectRoot
      Repository root. Defaults to the parent of the script directory.
      .PARAMETER Subject
      Certificate subject name.
	  Defaults to 'CN=OpenData Configuration Encryption'
      .PARAMETER Force
      Overwrite existing exported files.
      .NOTES
      Project : OpenData
      Author  : Terry Curran
      Version : 2.0.0
  #>
  [CmdletBinding(SupportsShouldProcess = $true)]
  param(
    [Parameter(Mandatory),HelpMessage='Project root location')]
    [string]$ProjectRoot,
    [switch]$Force
  )
  $Subject = 'CN=OpenData Configuration Encryption',
  $OutputDirectory = Join-Path -Path $ProjectRoot -ChildPath 'src\main\resources\config\security'
  $resolvedOutputDirectory = [System.IO.Path]::GetFullPath($OutputDirectory)
  $publicCertificatePath = Join-Path -Path $resolvedOutputDirectory -ChildPath 'opendata-config-public.cer'
  $privateStorePath = Join-Path -Path $resolvedOutputDirectory -ChildPath 'opendata-config-private.pfx'
  if ((((Test-Path -Path $publicCertificatePath -PathType Leaf -ErrorAction SilentlyContinue) -or
      (Test-Path -Path $privateStorePath -PathType Leaf -ErrorAction SilentlyContinue)) -and
  -not $Force))
  {
    throw 'Certificate files already exist. Re-run with -Force to overwrite them.'
  }
  if ($PSCmdlet.ShouldProcess($resolvedOutputDirectory, 'Create OpenData configuration certificate files')) {  
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
      Write-Output -InputObject ('Created: {0}' -f $publicCertificatePath)
      Write-Output -InputObject ('Created: {0}' -f $privateStorePath)
    } catch {
      Write-Warning -Message ('error {0}' -f $_)
    }
  }
}
$Parameters = @{
	ProjectRoot = 'C:\Users\terry\Documents\NetBeansProjects\opendata'
	Force = $true
}
New-ConfigurationCertificate @Parameters