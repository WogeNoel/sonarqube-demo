#!/usr/bin/env bash
# Live-Demo für macOS/Linux. Nutzung: ./scripts/demo.sh basis|fehler|fix [--ohne-scan]
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MAIN="$ROOT/src/main/java/de/demo/shop/VoucherService.java"
TEST="$ROOT/src/test/java/de/demo/shop/VoucherServiceTest.java"

rm -f "$MAIN" "$TEST"
case "${1:-}" in
  basis)  echo "Schritt BASIS: nur Legacy-Code." ;;
  fehler) cp "$ROOT/demo/schritt-1-feature-mit-fehlern/VoucherService.java" "$MAIN"
          echo "Schritt FEHLER: VoucherService (mit Fehlern) hinzugefügt." ;;
  fix)    cp "$ROOT/demo/schritt-2-feature-gefixt/VoucherService.java" "$MAIN"
          cp "$ROOT/demo/schritt-2-feature-gefixt/VoucherServiceTest.java" "$TEST"
          echo "Schritt FIX: VoucherService behoben und getestet." ;;
  *)      echo "Nutzung: $0 basis|fehler|fix [--ohne-scan]"; exit 1 ;;
esac

[ "${2:-}" = "--ohne-scan" ] && exit 0
: "${SONAR_TOKEN:?SONAR_TOKEN ist nicht gesetzt}"

cd "$ROOT"
if ./gradlew clean test sonar -Dsonar.qualitygate.wait=true --console=plain; then
  echo "QUALITY GATE: PASSED"
else
  echo "QUALITY GATE: FAILED"
fi
echo "Ergebnis: ${SONAR_HOST_URL:-http://localhost:9000}/dashboard?id=sonarqube-demo-shop"
