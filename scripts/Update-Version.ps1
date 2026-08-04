
$rootPath = "C:\Users\terry\Documents\NetBeansProjects\opendata"
$fileCount = 0
$updateCount = 0

# Get all .java files recursively
Get-ChildItem -Path $rootPath -Filter "*.java" -Recurse | ForEach-Object {
    $fileCount++
    $filePath = $_.FullName
    
    try {
        # Read the file content
        $content = Get-Content -Path $filePath -Raw
        
        # Check if the file contains @version
        if ($content -match '@version') {
            # Replace @version followed by any text until end of line with @version 1.0.0
            $updatedContent = $content -replace '@version\s+.*?(?=\r?\n|$)', '@version 1.0.0'
            
            # Write the updated content back to the file
            Set-Content -Path $filePath -Value $updatedContent -NoNewline
            
            $updateCount++
            Write-Host "Updated: $filePath"
        }
    }
    catch {
        Write-Host "Error processing file: $filePath - $_"
    }
}

Write-Host "`nSummary:"
Write-Host "Total .java files found: $fileCount"
Write-Host "Files updated: $updateCount"
