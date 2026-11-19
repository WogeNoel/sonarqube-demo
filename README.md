# SonarQube Demo – Mini-Shop

Begleit-Repository zur Präsentation **„SonarQube – Automatische Code-Reviews für sauberen und sicheren Code“**.

Die Demo zeigt in drei Scans, wie das **Quality Gate** von SonarQube funktioniert und was **Clean as You Code** bedeutet:

| Scan | Code-Stand | Erwartetes Quality Gate | Botschaft |
| --- | --- | --- | --- |
| 1 – `basis` | nur Legacy-Code mit bekannten Altlasten | ✅ **Passed** | Altlasten werden sichtbar, blockieren aber nicht – bewertet wird nur neuer Code |
| 2 – `fehler` | neues Feature `VoucherService` mit Fehlern | ❌ **Failed** | neuer Code mit Bug, Code Smell und Security Hotspot wird gestoppt |
| 3 – `fix` | Feature behoben und mit Unit-Tests abgesichert | ✅ **Passed** | sauberer neuer Code darf gemergt werden |

## Inhalt

```
├── docker-compose.yml          SonarQube Community Build + PostgreSQL
├── build.gradle                Java-Projekt mit JaCoCo (Coverage) und SonarQube-Gradle-Plugin
├── src/main/java/de/demo/shop
│   ├── PriceCalculator.java    sauberer, getesteter Code
│   ├── OrderRepository.java    Legacy: SQL aus String zusammengesetzt, Passwort im Code, Ressourcen nicht geschlossen
│   ├── ReportGenerator.java    Legacy: kopierter Code, tiefe Verschachtelung, System.out, auskommentierter Code
│   └── Order.java
├── demo/
│   ├── schritt-1-feature-mit-fehlern/VoucherService.java
│   └── schritt-2-feature-gefixt/VoucherService.java + VoucherServiceTest.java
├── scripts/demo.ps1            Demo-Steuerung für Windows (demo.sh für macOS/Linux)
├── .github/workflows/          CI-Beispiel: Build + SonarQube-Analyse mit Quality Gate
└── praesentation/              Folien als .pptx und .pdf
```

### Welche Probleme stecken im neuen Feature (Scan 2)?

| Stelle in `VoucherService` | Regel | Typ |
| --- | --- | --- |
| `new BigDecimal(0.10)` – double ist ungenau (0.1000000000000000055…) | `java:S2111` | Bug (Reliability) |
| `discount.multiply(price)` – `discount` ist `null`, wenn der Code unbekannt ist | `java:S2259` | Bug (Reliability) |
| `ADMIN_PASSWORD = "..."` – Passwort im Quellcode | `java:S2068` | Security Hotspot |
| leerer `catch`-Block | `java:S108` | Code Smell (Maintainability) |

## Voraussetzungen

- Docker Desktop (mind. 4 GB RAM für Docker)
- Java 17 oder neuer (getestet mit Java 21) – Gradle wird über den Wrapper automatisch geladen
- Git (SonarQube erkennt „neuen Code“ über die Git-Historie)

## Einmalige Einrichtung (vor der Präsentation!)

```powershell
git clone https://github.com/WogeNoel/sonarqube-demo.git
cd sonarqube-demo
```

> **Wichtig:** Der Legacy-Code muss committet sein, bevor der Basis-Scan läuft (bei einem frisch geklonten Repository ist das automatisch so). SonarQube erkennt „neuen Code“ über `git blame` – nicht committete Zeilen gelten immer als neu.

1. **SonarQube starten** (dauert beim ersten Mal 1–2 Minuten):

   ```powershell
   docker compose up -d
   ```

   Dann <http://localhost:9000> öffnen, mit `admin` / `admin` anmelden und das Passwort ändern.

2. **Token erzeugen:** *My Account → Security → Generate Tokens* → Typ **Global Analysis Token** → Token kopieren.

3. **Token setzen** (in jeder neuen PowerShell-Sitzung):

   ```powershell
   $env:SONAR_TOKEN = "sqa_..."
   ```

4. **Basis-Scan ausführen** – legt das Projekt an und setzt den Ausgangspunkt für „neuen Code“:

   ```powershell
   .\scripts\demo.ps1 -Schritt basis
   ```

> Das Token gehört nie in den Code oder ins Repository – es wird nur als Umgebungsvariable übergeben.

## Ablauf der Live-Demo (≈ 5 Minuten)

| Zeit | Aktion | Was zeigen / sagen |
| --- | --- | --- |
| 0:00 | Dashboard von Scan 1 im Browser öffnen | Viele Issues im Legacy-Code (*Overall Code*), Quality Gate trotzdem **grün** → Clean as You Code |
| 0:45 | `demo\schritt-1-feature-mit-fehlern\VoucherService.java` in der IDE zeigen | „Ein neues Feature, schnell geschrieben – oder von der KI generiert.“ |
| 1:15 | `.\scripts\demo.ps1 -Schritt fehler` | Terminal endet mit **QUALITY GATE: FAILED** – so würde die Pipeline den Merge blockieren |
| 2:00 | Dashboard → Reiter *New Code* → Issue `BigDecimal(double)` oder `NullPointerException` öffnen | Jedes Issue erklärt *warum* es ein Problem ist und *wie* man es behebt; Hotspot „Passwort im Code“ zeigen |
| 3:15 | `demo\schritt-2-feature-gefixt\VoucherService.java` zeigen | String-Konstruktor, `getOrDefault`, Passwort entfernt, Unit-Tests |
| 3:45 | `.\scripts\demo.ps1 -Schritt fix` | **QUALITY GATE: PASSED** – Coverage auf neuem Code sichtbar |
| 4:30 | Fazit | „SonarQube ersetzt kein Review – es macht Reviews besser.“ |

Nach der Demo den Ausgangszustand wiederherstellen:

```powershell
.\scripts\demo.ps1 -Schritt basis -OhneScan
```

### Plan B

Bei der Generalprobe von jedem der drei Scans einen Screenshot machen (Dashboard mit Quality-Gate-Status, ein geöffnetes Issue). Streiken Docker oder das Netzwerk, werden diese Screenshots statt der Live-Demo gezeigt.

### Komplett zurücksetzen

```powershell
docker compose down -v
```

Löscht alle Analysen und Benutzer – danach wieder bei „Einmalige Einrichtung“ beginnen.

## CI/CD (GitHub Actions)

`.github/workflows/sonarqube.yml` baut und testet das Projekt bei jedem Push und Pull Request.
Sind die Repository-Secrets `SONAR_TOKEN` und `SONAR_HOST_URL` gesetzt (z. B. für SonarQube Cloud oder einen öffentlich erreichbaren Server), läuft zusätzlich die Analyse mit `sonar.qualitygate.wait=true`: Ist das Quality Gate rot, schlägt der Workflow fehl und der Pull Request kann nicht gemergt werden.

## Weiterführend

- Regelbeschreibungen: <https://rules.sonarsource.com>
- SonarQube Community Build: <https://www.sonarsource.com/open-source-editions/sonarqube-community-edition/>
- SonarQube for IDE (Plugin für VS Code / IntelliJ) – zeigt dieselben Issues direkt beim Tippen
