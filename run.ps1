$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

$jdkFolder = Get-ChildItem "C:\Program Files\Eclipse Adoptium" -Directory -ErrorAction SilentlyContinue |
    Sort-Object Name -Descending |
    Select-Object -First 1

if (-not $jdkFolder) {
    Write-Host "JDK not found. Install Temurin 17 first." -ForegroundColor Red
    exit 1
}

$javac = Join-Path $jdkFolder.FullName "bin\javac.exe"
$java = Join-Path $jdkFolder.FullName "bin\java.exe"

if (-not (Test-Path $javac) -or -not (Test-Path $java)) {
    Write-Host "java.exe or javac.exe not found inside JDK folder." -ForegroundColor Red
    exit 1
}

& $javac -d out src/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Starting SecureVault (interactive mode)..." -ForegroundColor Green
& $java -cp out VaultApp
