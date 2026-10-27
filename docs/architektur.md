# Technische Architektur und Domänenmodellierung

Dieses Dokument beschreibt die technische Architektur, die eingesetzten Technologien sowie die Umsetzung der fachlichen Vorgaben und Invarianten im Code.

---

## 1. Technologie-Stack

| Komponente | Technologie | Version | Rationale |
|---|---|---|---|
| **Programmiersprache** | Kotlin | 2.1.x | Ausgezeichnete Unterstützung für DDD (Data Classes, Immutability, Sealed Classes, Type-Safety). |
| **Laufzeitumgebung** | Java Virtual Machine (JVM) | 21 (LTS) | Moderner Standard für JVM-basierte Enterprise- und Fachanwendungen. |
| **Build-Tool** | Gradle (Kotlin DSL) | 9.2.x | Schnelle, typsichere Build-Konfiguration (`build.gradle.kts`) mit Wrapper (`./gradlew`). |
| **Test-Framework** | JUnit Jupiter (JUnit 5) | 5.11.x | Standard für Modultests; unterstützt `@Nested`-Strukturen und sprechende Testnamen. |
| **Assertion-Bibliothek** | Kotlin Test / AssertJ | 3.27.x | Klare, lesbare Fehlerbeschreibungen bei fehlschlagenden Invariantenprüfungen. |

---

## 2. Struktur des Domänenmodells (Domain-Driven Design)

Das Softwaredesign orientiert sich streng an den fachlichen Geschäftsobjekten und OCL-Constraints aus [`fachdomäne/fachdomäneVerein/datenmodell.md`](file:///Users/mano/Desktop/Projekte/AD26-Team07/fachdomäne/fachdomäneVerein/datenmodell.md).

### Paketstruktur
```text
src/
├── main/
│   └── kotlin/
│       └── de/ad26/team07/verein/
│           ├── Gemeinnuetzigkeitsbescheid.kt   // Bescheidwesen & Fristprüfungen
│           ├── ...                             // Weitere Fachobjekte (Spender, Zuwendung, etc.)
└── test/
    └── kotlin/
        └── de/ad26/team07/verein/
            ├── GemeinnuetzigkeitsbescheidTest.kt // Unit-Tests für Bescheidfristen
            └── ...
```

### Abbildung von Fachregeln im Code

#### Bescheidwesen (`Gemeinnuetzigkeitsbescheid`)
Das Bescheidwesen ist als `sealed class` modelliert, um den steuerrechtlichen Rahmen vollständig und typsicher abzubilden:

1. **`Freistellungsbescheid` (§ 63 Abs. 5 AO):**
   * *Fachliche Regel:* Körperschaftsteuerbefreiung nach Veranlagung; Gültigkeit höchstens 5 Jahre ab Bescheiddatum.
   * *Umsetzung:* Methode `istGueltigAm(datum: LocalDate): Boolean` prüft, ob das Prüfdatum zwischen Bescheiddatum und Bescheiddatum + 5 Jahre liegt.
2. **`Feststellungsbescheid` (§ 60a AO):**
   * *Fachliche Regel:* Vorläufiger Nachweis der satzungsmäßigen Voraussetzungen (z. B. bei Neugründungen); Gültigkeit höchstens 3 Jahre.
   * *Umsetzung:* Methode `istGueltigAm(datum: LocalDate): Boolean` prüft, ob das Prüfdatum zwischen Feststellungsdatum und Feststellungsdatum + 3 Jahre liegt.

---

## 3. Testkonzept und Test-Driven Development (TDD)

Die steuerrechtlichen Compliance-Regeln erfordern höchste Präzision, da fehlerhafte Zuwendungsbestätigungen zur Falschausstellerhaftung (§ 54 Abs. 2 EStG) führen können.

* **Isolierte Modultests:** Jede fachliche Invariante wird durch Unit-Tests ohne externe Abhängigkeiten oder Mocks geprüft.
* **Grenzprüfungen (Boundary Value Analysis):**
  * Gültigkeit am Tag des Bescheid-Erlasses (untere Grenze).
  * Gültigkeit exakt am Stichtag des Fristendes (obere Grenze).
  * Ungültigkeit am Tag nach Ablauf der Frist.
  * Ungültigkeit für Datumsangaben vor der Bescheidausstellung.
* **Sprechende Testspezifikation:** Durch Kotlin-Backtick-Namen (`fun \`ist exakt 5 Jahre nach Ausstellung noch gueltig\`()`) dokumentieren die Tests direkt die fachlichen Anforderungen.
