
# Configuration
$javaSourcePath = "C:\Users\terry\Documents\NetBeansProjects\opendata\src\main\java" # Modify this path
$outputFile = "C:\Users\terry\Documents\NetBeansProjects\opendata\duplicate_classes_report.md"

function Remove-JavaComments {
    param([string]$content)
    
    # Remove multi-line comments /* ... */
    $content = $content -replace '(?s)/\*.*?\*/', ''
    
    # Remove single-line comments //
    $content = $content -replace '//.*?$', ''
    
    return $content
}

function Normalize-JavaCode {
    param([string]$content)
    
    # Remove comments
    $content = Remove-JavaComments $content
    
    # Remove leading/trailing whitespace
    $content = $content -replace '^\s+', '' -replace '\s+$', ''
    
    # Normalize whitespace (multiple spaces/newlines to single space)
    $content = $content -replace '\s+', ' '
    
    # Remove spaces around braces, parentheses, etc.
    $content = $content -replace '\s+([{}()=;,])', '$1'
    $content = $content -replace '([{}()=;,])\s+', '$1'
    
    return $content
}

function Extract-ClassName {
    param([string]$filePath)
    
    $content = Get-Content $filePath -Raw
    
    # Extract class name
    if ($content -match 'class\s+(\w+)') {
        return $matches[1]
    }
    
    return $null
}

function Extract-Package {
    param([string]$filePath)
    
    $content = Get-Content $filePath -Raw
    
    # Extract package name
    if ($content -match 'package\s+([\w.]+)') {
        return $matches[1]
    }
    
    return "default"
}

# Step 1: Collect all Java files with their metadata
Write-Host "Scanning Java files..." -ForegroundColor Cyan
$javaFiles = @()

Get-ChildItem -Path $javaSourcePath -Filter "*.java" -Recurse | ForEach-Object {
    $className = Extract-ClassName $_.FullName
    $package = Extract-Package $_.FullName
    
    if ($className) {
        $javaFiles += [PSCustomObject]@{
            FullPath = $_.FullName
            ClassName = $className
            Package = $package
            Content = (Get-Content $_.FullName -Raw)
        }
    }
}

Write-Host "Found $($javaFiles.Count) Java files" -ForegroundColor Green

# Step 2: Find duplicate class names
$duplicateNames = $javaFiles | Group-Object -Property ClassName | Where-Object { $_.Count -gt 1 }

if ($duplicateNames.Count -eq 0) {
    Write-Host "No duplicate class names found." -ForegroundColor Yellow
    exit
}

Write-Host "Found $($duplicateNames.Count) duplicate class names" -ForegroundColor Yellow

# Step 3: Compare content of duplicate classes
$results = @()

foreach ($group in $duplicateNames) {
    $className = $group.Name
    $duplicates = $group.Group
    
    Write-Host "`nAnalyzing class: $className" -ForegroundColor Cyan
    
    # Compare each pair
    for ($i = 0; $i -lt $duplicates.Count; $i++) {
        for ($j = $i + 1; $j -lt $duplicates.Count; $j++) {
            $file1 = $duplicates[$i]
            $file2 = $duplicates[$j]
            
            $normalized1 = Normalize-JavaCode $file1.Content
            $normalized2 = Normalize-JavaCode $file2.Content
            
            $isIdentical = $normalized1 -eq $normalized2
            
            $results += [PSCustomObject]@{
                ClassName = $className
                File1 = $file1.FullPath
                Package1 = $file1.Package
                File2 = $file2.FullPath
                Package2 = $file2.Package
                Identical = $isIdentical
                Size1 = $file1.Content.Length
                Size2 = $file2.Content.Length
            }
            
            if ($isIdentical) {
                Write-Host "  ✓ $($file1.Package).$className == $($file2.Package).$className" -ForegroundColor Green
            }
            else {
                Write-Host "  ✗ $($file1.Package).$className != $($file2.Package).$className" -ForegroundColor Red
            }
        }
    }
}

# Step 4: Find references to duplicate classes
Write-Host "`nSearching for references..." -ForegroundColor Cyan

$identicalDuplicates = $results | Where-Object { $_.Identical -eq $true }

$references = @()

foreach ($duplicate in $identicalDuplicates) {
    $className = $duplicate.ClassName
    
    # Search for import statements and usage
    Get-ChildItem -Path $javaSourcePath -Filter "*.java" -Recurse | ForEach-Object {
        $content = Get-Content $_.FullName -Raw
        
        # Look for imports of this class
        if ($content -match "import\s+[\w.]*$className") {
            $references += [PSCustomObject]@{
                DuplicateClass = $className
                ReferencingFile = $_.FullName
                Type = "Import"
            }
        }
        
        # Look for direct usage (simple pattern)
        if ($content -match "\b$className\s*[\(\{]") {
            $references += [PSCustomObject]@{
                DuplicateClass = $className
                ReferencingFile = $_.FullName
                Type = "Usage"
            }
        }
    }
}

# Step 5: Generate report
$report = @"
========================================
JAVA DUPLICATE CLASS ANALYSIS REPORT
Generated: $(Get-Date)
========================================

IDENTICAL DUPLICATES FOUND:
$(if ($identicalDuplicates) { $identicalDuplicates | Format-Table -AutoSize | Out-String } else { "None" })

IDENTICAL DUPLICATE REFERENCES:
$(if ($references) { $references | Format-Table -AutoSize | Out-String } else { "None" })

ALL COMPARISONS:
$($results | Format-Table -AutoSize | Out-String)
"@

$report | Out-File -FilePath $outputFile -Encoding UTF8
Write-Host "`nReport saved to: $outputFile" -ForegroundColor Green
Write-Host $report
$results | Out-GridView