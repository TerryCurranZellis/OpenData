function New-Documentation {
  [CmdletBinding()]
  param(
    [ValidateSet('All','Html','Docx','Pdf','None')]
    [string]$Format = 'All',
    [Parameter(Mandatory=$true)]
    [string]$ProjectRoot,
    [AlllowNull()]
    [string]$ReferenceDoc,
    [switch]$RenderDiagrams,
    [switch]$Validate,
    [switch]$Clean
  )

  $ErrorActionPreference = 'Stop'
  . (Join-Path -Path $PSScriptRoot -ChildPath 'Common.ps1')
  if ([string]::IsNullOrWhiteSpace($ProjectRoot)) { 
    $ProjectRoot = Resolve-ProjectRoot 
  }
  $config = Read-DocumentationConfig -ProjectRoot $ProjectRoot
  $build = Join-Path -Path $ProjectRoot -ChildPath $config.buildDirectory
  Ensure-Directory -Path $build

  if ($Clean) {
    Get-ChildItem -LiteralPath $build -Force -ErrorAction SilentlyContinue | Remove-Item -Recurse -Force
    Ensure-Directory -Path $build
  }

  if ($Validate) {
    & (Join-Path -Path $PSScriptRoot -ChildPath 'Test-Documentation.ps1') -ProjectRoot $ProjectRoot
    if ($LASTEXITCODE -ne 0) { 
      throw 'Documentation validation failed.' 
    }
  }

  if ($RenderDiagrams) {
    & (Join-Path -Path $PSScriptRoot -ChildPath 'Render-PlantUml.ps1') -ProjectRoot $ProjectRoot -Format svg
  }

  $null = & (Join-Path -Path $PSScriptRoot -ChildPath 'New-DocumentInventory.ps1') -ProjectRoot $ProjectRoot
  $manual = & (Join-Path -Path $PSScriptRoot -ChildPath 'Merge-Documentation.ps1') -ProjectRoot $ProjectRoot

  if ($Format -eq 'None') {
    Write-Output -InputObject ('Documentation preparation completed: {0}' -f $manual)
    return
  }

  Assert-CommandAvailable -Name 'pandoc'
  $baseArgs = @(
    $manual,
    '--from=markdown+yaml_metadata_block+pipe_tables+fenced_divs',
    '--standalone',
    '--toc',
    '--toc-depth=3',
    '--number-sections',
    '--resource-path=' + (Join-Path -Path $ProjectRoot -ChildPath 'docs'),
    '--metadata', 'lang=en-GB'
  )

  $formats = if ($Format -eq 'All') { 
    @('Html','Docx','Pdf')
  } else { 
    @($Format) 
  }
  foreach ($item in $formats) {
    switch ($item) {
      'Html' {
        $out = Join-Path -Path $build -ChildPath 'OpenData-Technical-Documentation.html'
        & "$env:LOCALAPPDATA\pandoc\pandoc.exe" @baseArgs '--embed-resources' '--output' $out
      }
      'Docx' {
        $out = Join-Path -Path $build -ChildPath 'OpenData-Technical-Documentation.docx'
        $args = @($baseArgs)
        $effectiveReference = $ReferenceDoc
        if ([string]::IsNullOrWhiteSpace($effectiveReference)) {
          $candidate = Join-Path -Path $ProjectRoot -ChildPath $config.referenceDoc
          if (Test-Path -LiteralPath $candidate) { $effectiveReference = $candidate }
        }
        if (-not [string]::IsNullOrWhiteSpace($effectiveReference)) {
          $args += '--reference-doc=' + (Resolve-Path -LiteralPath $effectiveReference).Path
        }
        & "$env:LOCALAPPDATA\pandoc\pandoc.exe" @args '--output' $out
      }
      'Pdf' {
        $out = Join-Path -Path $build -ChildPath 'OpenData-Technical-Documentation.pdf'
        & "$env:LOCALAPPDATA\pandoc\pandoc.exe" @baseArgs ('--pdf-engine=' + $config.pdfEngine) '--output' $out
      }
    }
    if ($LASTEXITCODE -ne 0) { 
      throw ('Pandoc failed while building {0} output.' -f $item) 
    }
    Write-Output -InputObject ('Created {0}' -f $out)
  }

}
New-Documentation -ProjectRoot 'C:\Users\terry\Documents\NetBeansProjects\opendata' -RenderDiagrams -Validate
