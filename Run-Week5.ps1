$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

New-Item -ItemType Directory -Force out | Out-Null

$files = Get-ChildItem src -Filter *.java |
    Where-Object { $_.Name -ne "UIProcess.java" } |
    ForEach-Object { $_.FullName }

Write-Host "Compiling Week 5 Simulator..."

javac -encoding UTF-8 -d out $files

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed." -ForegroundColor Red
    Read-Host "Press Enter to close"
    exit 1
}

Write-Host "Starting Week 5 GUI..."
java -cp out SimulatorFrame
