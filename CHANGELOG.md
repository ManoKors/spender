# Changelog

Alle nennenswerten Änderungen an diesem Projekt werden in dieser Datei dokumentiert.

Das Format basiert auf [Keep a Changelog](https://keepachangelog.com/de/1.1.0/)
und dieses Projekt hält sich an [Semantic Versioning](https://semver.org/lang/de/).

---

## [Unreleased]

### Added
- **Hexagonale Architektur (Ports & Adapters):**
  - Aufteilung des Codes in `domain` (Kern), `application` (Ports & Use Cases) und `adapter` (Infrastruktur).
  - Outbound-Ports [`VereinRepositoryPort`](src/main/kotlin/de/ad26/team07/application/port/out/VereinRepositoryPort.kt) und [`SpendenabsichtRepositoryPort`](src/main/kotlin/de/ad26/team07/application/port/out/SpendenabsichtRepositoryPort.kt) für lose Persistenz- und Schnittstellenkopplung.
  - Architektur- und Entwurfsentscheidungen ADR 04, ADR 05 und ADR 06.
- **UML-Modellierung & 3-Level-Datenmodell (UML 2.5):**
  - Harmonisierung des Klassendatenmodells ([`fachdomäne/fachdomäneVerein/datenmodell.md`](fachdomäne/fachdomäneVerein/datenmodell.md)) mit den neuen Entitäten (`Spendenabsicht`, `WhiteLabelKonfiguration`, `GiroCodeGenerator`, `Zuwendungsempfaenger`) und exakten UML-Beziehungsrichtungen (`*--`, `o--`, `-->`, `<|--`, `..|>`, `..>`).
  - Interaktiver 3-Ebenen-UML-Viewer ([`fachdomäne/fachdomäneVerein/datenmodell_zuwendungsbestaetigung_034122.html`](fachdomäne/fachdomäneVerein/datenmodell_zuwendungsbestaetigung_034122.html)) mit Zoom, Tab-Navigation und drill-down (`Gemeinnützigkeitsbescheid 🔍` -> Level 2):
    - *Level 1: Fachdomäne Gesamtübersicht* (Stammdaten, Spenden-Wizard, Bescheidwesen, Nachweis).
    - *Level 2: Detailmodell Gemeinnützigkeitsbescheid & Fristen* (§ 63 Abs. 5 AO und § 60a AO).
    - *Level 3: Hexagonale Architektur (Ports & Adapters)* (Driving/Driven Ports, Application Service, In-Memory- & Web-Adapter).
  - Formales UML-Anwendungsfalldiagramm ([`fachdomäne/fachdomäneVerein/usecase_diagramm.md`](fachdomäne/fachdomäneVerein/usecase_diagramm.md)) basierend auf [`prozesstabelle.md`](fachdomäne/fachdomäneVerein/prozesstabelle.md) inklusive «include»- und «extend»-Beziehungen sowie lückenloser Traceability-Matrix.
  - Erweiterung des Glossars ([`fachdomäne/fachdomäneVerein/glossarVerein.md`](fachdomäne/fachdomäneVerein/glossarVerein.md)) um die Geschäftsobjekte `Spendenabsicht`, `EPC-GiroCode` und `White-Label-Konfiguration`.
- **White-Label Spenden-Wizard & Spendenabsichten:**
  - Value Objects `WhiteLabelKonfiguration`, `Spender`, `Adresse`, `SteuerbeguenstigterZweck`.
  - Entität `Spendenabsicht` (Pledge) mit Status-Zyklus (`ANGEKUENDIGT`, `EINGEGANGEN`, `ABGELAUFEN`).
  - Verbindliche Gegenleistungsverzichtserklärung im Wizard.
  - Domain Service `GiroCodeGenerator` nach europäischem EPC069-08 Standard.
  - Inbound-Use-Case `RegistriereSpendenabsichtUseCase` mit `SpendenabsichtService`.
  - Use-Case-Spezifikation (Teil 3) in der Prozesstabelle.
- **Autarke Client-Side QR-Code-Engine (VanillaQR):**
  - Zero-Dependency JavaScript-Implementierung ([`demo/vanilla_qr.js`](demo/vanilla_qr.js)) zur Generierung von EPC069-08 SEPA-GiroCodes.
  - Vektor-SVG-Rendering ohne externe CDN-Abhängigkeiten (100% offline- und `file:///`-fähig).
  - Volle UTF-8-Unterstützung für deutsche Sonderzeichen und Umlaute in Verwendungszwecken und Vereinsnamen.
  - Interaktive Komfort-Funktionen in [`demo/spenden_wizard.html`](demo/spenden_wizard.html): One-Click-Copy für SEPA-Felder, Rohdaten-Payload-Inspektion und Auto-Fill für Testdaten.
- **Automatisierte Testsuite (19 Tests):**
  - Vollständige Abdeckung aller Bescheidfristen (§ 63 Abs. 5 AO, § 60a AO), Spendenabsichts-Invarianten, GiroCode-Integrität und Inbound Use Cases.

### Changed
- **UML 2.5 Use-Case-Modell & Systemgrenzen-Harmonisierung:**
  - `UC-2` (Spende per Banking-App überweisen) aus der internen Systemgrenze in einen expliziten externen Kontext (`ExternalPayment`) überführt, da die Zahlungsanweisung bei der Hausbank des Spenders erfolgt.
  - Kopplung zwischen `UC-1.2` (EPC-GiroCode generieren) und `UC-2` per Dependency-Pfeil (`«liefert GiroCode für»`) präzisiert.
  - Das `Finanzamt` als externen Akteur formal korrekt über eine Standard-Assoziation an `UC-5` (Gemeinnützigkeitsbescheid pflegen) angebunden; die zuvor unzulässige gestrichelte Kante von `UC-4.1` zum Akteur entfernt.
- **Klassendatenmodell & OCL-Harmonisierung:**
  - Attributnotation in `InboundPorts_Driving` (`RegistriereSpendenabsichtCommand`) auf den formalen UML 2.5-Standard (`+name: Type`) vereinheitlicht.
  - OCL 1 und OCL 2 auf normgerechte OCL-Syntax (`not` statt `!`) angepasst.
  - Attributname `hinweisKeinAusschlussBeitrag` im interaktiven HTML-Viewer (`datenmodell_zuwendungsbestaetigung_034122.html`) mit dem Markdown-Modell synchronisiert.
- **Hexagonale Architektur & DIP-Konformität:**
  - Pfeilrichtungen der Outbound-Ports in [`docs/architektur.md`](docs/architektur.md) korrigiert (`Persistence --> OutPort`, `PdfOut --> OutPort`), sodass alle Pfeile von den äußeren Adaptern nach innen zum Kern zeigen (Dependency Inversion Principle).
  - Demo-Repositories in [`src/main/kotlin/de/ad26/team07/Main.kt`](src/main/kotlin/de/ad26/team07/Main.kt) von `Simple...` in `InMemoryVereinRepository` und `InMemorySpendenabsichtRepository` umbenannt zur 1:1-Übereinstimmung mit dem UML-Klassendiagramm und den Modultests.

### Fixed
- **Mermaid 11 Diagramm-Rendering im interaktiven HTML-Viewer:**
  - Behebung von `Syntax error in text`- und `DOM parser`-Fehlern in [`fachdomäne/fachdomäneVerein/datenmodell_zuwendungsbestaetigung_034122.html`](fachdomäne/fachdomäneVerein/datenmodell_zuwendungsbestaetigung_034122.html):
    - HTML-Escaping von UML-Stereotypen als `&lt;&lt;...&gt;&gt;` (`&lt;&lt;interface&gt;&gt;`, `&lt;&lt;abstract&gt;&gt;`, `&lt;&lt;service&gt;&gt;`, `&lt;&lt;enumeration&gt;&gt;`), um fehlerhafte DOM-Tag-Synthesen (`<interface>`) durch den Browser-HTML-Parser zu verhindern.
    - Korrektur der Realisierungs-Kanten auf die standardkonforme Mermaid-Syntax `..|>` (implementierende Klasse zeigt auf Schnittstelle) anstelle des nicht unterstützten `<|..`.
    - Behebung des DAGRE-Graph-Fehlers (`Cannot read properties of undefined (reading 'attr')`) durch Verknüpfung konkreter Klassenentitäten (`Spendenabsicht`, `Zuwendungsempfaenger`) anstelle des übergeordneten Namespace-Clusters `DomainCore`.
    - Bereinigung von Sonderzeichen (`§`) in Node-Labels zur Vermeidung von Lexer-Abbrüchen in Mermaid 11.
- **Git-Tracking für Outbound Ports:**
  - `.gitignore` korrigiert (`/out/` statt `out/`), wodurch das Application-Package `de.ad26.team07.application.port.out` nicht mehr fälschlicherweise als Compiler-Ausgabeverzeichnis ignoriert wird.
- **QR-Code-Generierung im Demo-Wizard:**
  - Behebung von `code length overflow`- und CDN-Blockierungsfehlern beim Generieren des EPC-GiroCodes durch Umstellung auf autarkes `VanillaQR`.

### Geplant
- Modellierung von `Geldzuwendung`, `Geldspende` und `Mitgliedsbeitrag`.
- Prüfung der Aufwandsspende-Bedingung (`Gegenleistungsverzichtserklaerung`).
- Implementierung der Zweckkongruenz-Prüfung zwischen Satzung, Bescheid und Bestätigung.
- Validierung der 300 €-Grenze für den vereinfachten Zuwendungsnachweis.

---

## [0.1.0] - 2026-10-27

### Added
- **Build & Projektsetup:**
  - Initiales Projekt-Setup mit **Kotlin 2.1** auf **Java 21 (LTS)**.
  - Gradle-Konfiguration via Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`) mit JUnit 5 und Test-Logging.
  - Gradle Wrapper (`./gradlew`) zur plattformunabhängigen Projektausführung.
  - Standard `.gitignore` für Gradle-, IDE- und OS-Dateien.
- **Domänenmodell (Bescheidwesen):**
  - `Gemeinnuetzigkeitsbescheid` als versiegelte Basishierarchie (`sealed class`).
  - `Freistellungsbescheid` inklusive 5-Jahres-Gültigkeitsprüfung gemäß § 63 Abs. 5 AO.
  - `Feststellungsbescheid` nach § 60a AO inklusive 3-Jahres-Gültigkeitsprüfung.
- **Tests & Qualitätssicherung:**
  - `GemeinnuetzigkeitsbescheidTest`: 7 Unit-Tests mit JUnit 5 zur Validierung aller Stichtags- und Grenzfälle.
- **Dokumentation:**
  - Dokumentationsverzeichnis [`docs/`](docs/) eingerichtet.
  - Technische Architektur und DDD-Konzepte in [`docs/architektur.md`](docs/architektur.md).
  - Architekturentscheidungen (ADR 01-03) in [`docs/entscheidungen.md`](docs/entscheidungen.md).
  - Aktualisierung der [`README.md`](README.md) mit Thema, Dokumentationsverlinkungen und Setup-Anweisungen.
- **Fachdomänen-Exploration:**
  - Fachglossar in [`fachdomäne/glossar.md`](fachdomäne/glossar.md).
  - Atomares DDD-Datenmodell und OCL-Constraints für BMF Muster 034122 in [`fachdomäne/fachdomäneVerein/datenmodell.md`](fachdomäne/fachdomäneVerein/datenmodell.md).
  - Interaktives HTML-Diagramm in [`fachdomäne/fachdomäneVerein/datenmodell_zuwendungsbestaetigung_034122.html`](fachdomäne/fachdomäneVerein/datenmodell_zuwendungsbestaetigung_034122.html).
  - Use-Case-Szenarien und Prozessablauf in [`fachdomäne/fachdomäneVerein/prozesstabelle.md`](fachdomäne/fachdomäneVerein/prozesstabelle.md).
