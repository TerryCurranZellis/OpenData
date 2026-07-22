[CmdletBinding()]
param(
    [ValidateSet('All','Html','Docx','Pdf','None')][string]$Format = 'All',
    [string]$ProjectRoot,
    [string]$ReferenceDoc,
    [switch]$RenderDiagrams,
    [switch]$Validate,
    [switch]$Clean
)

$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'Common.ps1')
if ([string]::IsNullOrWhiteSpace($ProjectRoot)) { $ProjectRoot = Resolve-ProjectRoot }
$config = Read-DocumentationConfig -ProjectRoot $ProjectRoot
$build = Join-Path $ProjectRoot $config.buildDirectory
Ensure-Directory -Path $build

if ($Clean) {
    Get-ChildItem -LiteralPath $build -Force -ErrorAction SilentlyContinue | Remove-Item -Recurse -Force
    Ensure-Directory -Path $build
}

if ($Validate) {
    & (Join-Path $PSScriptRoot 'Test-Documentation.ps1') -ProjectRoot $ProjectRoot
    if ($LASTEXITCODE -ne 0) { throw 'Documentation validation failed.' }
}

if ($RenderDiagrams) {
    & (Join-Path $PSScriptRoot 'Render-PlantUml.ps1') -ProjectRoot $ProjectRoot -Format svg
}

& (Join-Path $PSScriptRoot 'New-DocumentInventory.ps1') -ProjectRoot $ProjectRoot | Out-Null
$manual = & (Join-Path $PSScriptRoot 'Merge-Documentation.ps1') -ProjectRoot $ProjectRoot

if ($Format -eq 'None') {
    Write-Host "Documentation preparation completed: $manual"
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
    '--resource-path=' + (Join-Path $ProjectRoot 'docs'),
    '--metadata', 'lang=en-GB'
)

$formats = if ($Format -eq 'All') { @('Html','Docx','Pdf') } else { @($Format) }
foreach ($item in $formats) {
    switch ($item) {
        'Html' {
            $out = Join-Path $build 'OpenData-Technical-Documentation.html'
            & pandoc @baseArgs '--embed-resources' '--output' $out
        }
        'Docx' {
            $out = Join-Path $build 'OpenData-Technical-Documentation.docx'
            $args = @($baseArgs)
            $effectiveReference = $ReferenceDoc
            if ([string]::IsNullOrWhiteSpace($effectiveReference)) {
                $candidate = Join-Path $ProjectRoot $config.referenceDoc
                if (Test-Path -LiteralPath $candidate) { $effectiveReference = $candidate }
            }
            if (-not [string]::IsNullOrWhiteSpace($effectiveReference)) {
                $args += '--reference-doc=' + (Resolve-Path -LiteralPath $effectiveReference).Path
            }
            & pandoc @args '--output' $out
        }
        'Pdf' {
            $out = Join-Path $build 'OpenData-Technical-Documentation.pdf'
            & pandoc @baseArgs ('--pdf-engine=' + $config.pdfEngine) '--output' $out
        }
    }
    if ($LASTEXITCODE -ne 0) { throw "Pandoc failed while building $item output." }
    Write-Host "Created $out"
}
