Set-StrictMode -Version 2.0

function Resolve-ProjectRoot {
    [CmdletBinding()]
    param([string]$StartPath = $PSScriptRoot)

    $current = Resolve-Path -LiteralPath $StartPath
    while ($null -ne $current) {
        if (Test-Path -LiteralPath (Join-Path $current 'config\documentation.json')) {
            return $current.Path
        }
        $parent = Split-Path -Parent $current
        if ([string]::IsNullOrWhiteSpace($parent) -or $parent -eq $current.Path) { break }
        $current = Get-Item -LiteralPath $parent
    }
    throw 'Unable to locate project root containing config\documentation.json.'
}

function Read-DocumentationConfig {
    [CmdletBinding()]
    param([Parameter(Mandatory=$true)][string]$ProjectRoot)

    $path = Join-Path $ProjectRoot 'config\documentation.json'
    if (-not (Test-Path -LiteralPath $path)) { throw "Configuration file not found: $path" }
    return Get-Content -LiteralPath $path -Raw -Encoding UTF8 | ConvertFrom-Json
}

function Assert-CommandAvailable {
    [CmdletBinding()]
    param([Parameter(Mandatory=$true)][string]$Name)
    if (-not (Get-Command -Name $Name -ErrorAction SilentlyContinue)) {
        throw "Required command '$Name' was not found on PATH."
    }
}

function Ensure-Directory {
    [CmdletBinding()]
    param([Parameter(Mandatory=$true)][string]$Path)
    if (-not (Test-Path -LiteralPath $Path)) {
        New-Item -ItemType Directory -Path $Path -Force | Out-Null
    }
}
