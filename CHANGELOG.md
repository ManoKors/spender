# Changelog

Alle nennenswerten Änderungen an diesem Projekt werden in dieser Datei dokumentiert.

Das Format basiert auf [Keep a Changelog](https://keepachangelog.com/de/1.1.0/)
und dieses Projekt hält sich an [Semantic Versioning](https://semver.org/lang/de/).

---

## [Unreleased]

### Added
- **Hexagonale Architektur (Ports & Adapters):**
  - Aufteilung des Codes in `domain` (Kern), `application` (Ports & Use Cases) und `adapter` (Infrastruktur).
  - Architektur- und Entwurfsentscheidungen ADR 04 und ADR 05.
- **White-Label Spenden-Wizard & Spendenabsichten:**
  - Value Objects `WhiteLabelKonfiguration`, `Spender`, `Adresse`, `SteuerbeguenstigterZweck`.
  - Entität `Spendenabsicht` (Pledge) mit Status-Zyklus (`ANGEKUENDIGT`, `EINGEGANGEN`, `ABGELAUFEN`).
  - Verbindliche Gegenleistungsverzichtserklärung im Wizard.
  - Domain Service `GiroCodeGenerator` nach europäischem EPC069-08 Standard.
  - Inbound-Use-Case `RegistriereSpendenabsichtUseCase` mit `SpendenabsichtService`.
  - Use-Case-Spezifikation (Teil 3) in der Prozesstabelle.

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
