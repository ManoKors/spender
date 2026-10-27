# AD26-Team07

## Team

<!-- Namen aller Teammitglieder, eine Zeile pro Person -->

-

## Thema

Automatisierung der steuerrechtlichen Compliance und Belegprüfung für Geld- und Sachzuwendungen im Vereins- und Universitätskontext. Das System unterstützt die Prüfung der steuerlichen Voraussetzungen (Bescheide, Fristen, Satzungszwecke) und die rechtssichere Ausstellung von Zuwendungsbestätigungen nach amtlichem BMF-Muster (034122).

## Dokumentation

### Fachkonzept & Domain Exploration
* [Fachdomänenbeschreibung](fachdomäne/beschreibung.md) – Kontext und Zielsetzung des Systems.
* [Glossar](fachdomäne/glossar.md) – Geschäftsobjekte und Attribute im Singular.
* [Datenmodell & OCL-Constraints](fachdomäne/fachdomäneVerein/datenmodell.md) – Atomares UML-Modell und formale Invarianten nach BMF-Muster 034122.
* [Interaktives Datenmodell](fachdomäne/fachdomäneVerein/datenmodell_zuwendungsbestaetigung_034122.html) – Visuelle Darstellung der Modellstufen (Level 1 & Level 2).
* [Prozesstabelle & Use Cases](fachdomäne/fachdomäneVerein/prozesstabelle.md) – Detaillierte Ablaufbeschreibung für „Voraussetzungen prüfen".

### Technische Architektur & Entscheidungen
* [Architektur & Modellierung](docs/architektur.md) – Tech-Stack (Kotlin/Gradle/JUnit 5), DDD-Struktur und Testkonzept.
* [Architekturentscheidungen (ADR)](docs/entscheidungen.md) – Entscheidungsdokumente zu Technologien, Fristkapselung und TDD.
* [Changelog](CHANGELOG.md) – Meilensteine und Release-Historie nach Keep a Changelog.

### Demos & Prototypen
* [Interaktiver White-Label Spenden-Wizard](demo/spenden_wizard.html) – Voll funktionsfähiger Web-Wizard mit Mandanten-Umschaltung, Gegenleistungsverzicht und echtem EPC-GiroCode (QR-Code zum Scannen mit Banking-Apps).
* [Interaktives Datenmodell](fachdomäne/fachdomäneVerein/datenmodell_zuwendungsbestaetigung_034122.html) – Visuelle Darstellung der Modellstufen (Level 1 & Level 2).

## Setup und Start

Das Projekt verwendet **Kotlin** mit **Gradle** (Kotlin DSL) und **Java 21**.

### Interaktive Demo ausführen
```bash
./gradlew run
```

### Tests ausführen
```bash
./gradlew test
```

### Projekt bauen
```bash
./gradlew build
```

## Reflexion

<!-- Persönlicher Reflexionsteil, je Teammitglied ein Abschnitt oder eine Datei. -->