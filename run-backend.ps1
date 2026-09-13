# Download Maven 3.9.6 and set PATH for this session
$MavenVersion = "3.9.6"
$MavenHome = "$env:USERPROFILE\.m2\maven-$MavenVersion"

if (-not (Test-Path $MavenHome)) {
    Write-Host "Downloading Maven $MavenVersion..."
    $MavenUrl = "https://archive.apache.org/dist/maven/maven-3/$MavenVersion/binaries/apache-maven-$MavenVersion-bin.zip"
    $MavenZip = "$env:TEMP\maven-$MavenVersion.zip"
    
    Invoke-WebRequest -Uri $MavenUrl -OutFile $MavenZip -ErrorAction Stop
    
    Write-Host "Extracting Maven..."
    $M2Folder = "$env:USERPROFILE\.m2"
    if (-not (Test-Path $M2Folder)) { New-Item -ItemType Directory -Path $M2Folder -Force | Out-Null }
    
    Expand-Archive -Path $MavenZip -DestinationPath $M2Folder -Force
    Rename-Item "$M2Folder\apache-maven-$MavenVersion" $MavenHome -Force -ErrorAction SilentlyContinue
    
    Remove-Item $MavenZip -Force
    Write-Host "Maven installed at: $MavenHome"
} else {
    Write-Host "Maven already at: $MavenHome"
}

# Add Maven to PATH for this session
$env:Path = "$MavenHome\bin;$env:Path"

# Verify Maven works
Write-Host "`nVerifying Maven..."
mvn --version

Write-Host "`nMaven is ready! Running backend...`n"
Push-Location 'e:\my portfolio\techloom-software-engineer-intern-assessment\task-01\backend'
mvn spring-boot:run
