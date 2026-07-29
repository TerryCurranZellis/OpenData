<#
Copyright © 2026 Terry Curran
SPDX-License-Identifier: Apache-2.0
#>

# Recursively unblock all files in a folder and subfolders
$folderPath = 'C:\Users\terry\Documents\NetBeansProjects\opendata' # Change this to your target folder

Get-ChildItem -Path $folderPath -Recurse | Unblock-File
