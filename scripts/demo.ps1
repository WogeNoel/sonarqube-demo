<#
.SYNOPSIS
    Steuert die Live-Demo: setzt den Code auf einen Demo-Schritt und startet die SonarQube-Analyse.

.EXAMPLE
    $env:SONAR_TOKEN = "<dein Token>"
    .\scripts\demo.ps1 -Schritt basis     # Scan 1: nur Legacy-Code        -> Quality Gate PASSED
    .\scripts\demo.ps1 -Schritt fehler    # Scan 2: neues Feature mit Bugs  -> Quality Gate FAILED
    .\scripts\demo.ps1 -Schritt fix       # Scan 3: Feature behoben+Tests   -> Quality Gate PASSED
    .\scripts\demo.ps1 -Schritt basis -OhneScan   # nur Dateien zurücksetzen
#>
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('basis', 'fehler', 'fix')]
    [string]$Schritt,

    [switch]$OhneScan
)

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$mainFile = Join-Path $root 'src/main/java/de/demo/shop/VoucherService.java'
$testFile = Join-Path $root 'src/test/java/de/demo/shop/VoucherServiceTest.java'

# 1) Code auf den gewünschten Stand bringen
Remove-Item $mainFile, $testFile -ErrorAction SilentlyContinue
switch ($Schritt) {
    'basis'  { Write-Host 'Schritt BASIS: nur Legacy-Code, kein neues Feature.' -ForegroundColor Cyan }
    'fehler' {
        Copy-Item (Join-Path $root 'demo/schritt-1-feature-mit-fehlern/VoucherService.java') $mainFile
        Write-Host 'Schritt FEHLER: neues Feature VoucherService (mit Fehlern) hinzugefügt.' -ForegroundColor Yellow
    }
    'fix'    {
        Copy-Item (Join-Path $root 'demo/schritt-2-feature-gefixt/VoucherService.java') $mainFile
        Copy-Item (Join-Path $root 'demo/schritt-2-feature-gefixt/VoucherServiceTest.java') $testFile
        Write-Host 'Schritt FIX: VoucherService behoben und mit Tests abgesichert.' -ForegroundColor Green
    }
}

if ($OhneScan) { return }

# 2) Analyse starten (Token kommt aus der Umgebungsvariable, nie aus dem Code)
if (-not $env:SONAR_TOKEN) {
    Write-Host 'SONAR_TOKEN ist nicht gesetzt. Beispiel: $env:SONAR_TOKEN = "sqa_..."' -ForegroundColor Red
    exit 1
}

Push-Location $root
try {
    # sonar.qualitygate.wait=true: der Build wartet auf das Quality Gate und schlägt fehl, wenn es rot ist
    & .\gradlew.bat clean test sonar '-Dsonar.qualitygate.wait=true' --console=plain
    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n  QUALITY GATE: PASSED  " -ForegroundColor Black -BackgroundColor Green
    } else {
        Write-Host "`n  QUALITY GATE: FAILED  " -ForegroundColor White -BackgroundColor Red
    }
    $url = if ($env:SONAR_HOST_URL) { $env:SONAR_HOST_URL } else { 'http://localhost:9000' }
    Write-Host "Ergebnis: $url/dashboard?id=sonarqube-demo-shop"
} finally {
    Pop-Location
}
