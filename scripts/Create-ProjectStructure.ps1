<#
.SYNOPSIS
    Creates the OpenData project folder structure.

.DESCRIPTION
    Creates the complete repository folder hierarchy for
    source code, documentation, SQL, examples, scripts,
    tools and GitHub configuration.

.NOTES
    Project : OpenData
    Author  : Towermarsh
    Version : 1.0.0
#>

[CmdletBinding()]
param(
    [string]$Root = '.'
)

$folders = @(
    '.github',
    '.github\ISSUE_TEMPLATE',
    '.github\workflows',

    'docs',
    'docs\architecture',
    'docs\decisions',
    'docs\development',
    'docs\diagrams',
    'docs\diagrams\activity',
    'docs\diagrams\class',
    'docs\diagrams\component',
    'docs\diagrams\deployment',
    'docs\diagrams\entity-relationship',
    'docs\diagrams\package',
    'docs\diagrams\sequence',
    'docs\diagrams\state',
    'docs\guides',
    'docs\reference',
    'docs\roadmap',
    'docs\standards',

    'examples',

    'scripts',
    'scripts\build',
    'scripts\database',
    'scripts\documentation',
    'scripts\release',

    'sql',
    'sql\functions',
    'sql\migration',
    'sql\procedures',
    'sql\schema',
    'sql\seed-data',
    'sql\test-data',
    'sql\views',

    'src',
    'src\main',
    'src\main\java',
    'src\main\resources',

    'src\test',
    'src\test\java',
    'src\test\resources',

    'tools',

    'build',

    'logs',

    'data',
    'data\archive',
    'data\download',
    'data\processed',
    'data\rejected',
    'data\working'
)

foreach ($folder in $folders)
{
    $path = Join-Path -Path $Root -ChildPath $folder

    if (-not (Test-Path -Path $path))
    {
        New-Item `
            -ItemType Directory `
            -Path $path `
            -Force | Out-Null

        Write-Output -InputObject ('[Created] {0}' -f $folder)
    }
    else
    {
        Write-Output -InputObject ('[Exists ] {0}' -f $folder)
    }
}

Write-Output -InputObject ''
Write-Output -InputObject 'OpenData project structure complete.'