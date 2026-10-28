# UML-Anwendungsfalldiagramm (Use Case Model)

Dieses Dokument spezifiziert das **UML 2.5 Use-Case-Modell** für das Spenden- und Compliance-System zur Ausstellung von Zuwendungsbestätigungen nach amtlichem Muster 034122 (Geldzuwendungen). Es basiert auf den in [`prozesstabelle.md`](prozesstabelle.md) detaillierten Ablaufszenarien und harmonisiert die Anforderungen mit dem [Klassendatenmodell](datenmodell.md) sowie der [Hexagonalen Architektur](../../docs/architektur.md).

---

## 1. Übersicht: UML-Anwendungsfalldiagramm

Das Diagramm gliedert die Interaktionen nach Akteuren und Systemgrenze. Es beachtet streng die UML-Regeln für Beziehungsrichtungen:
* **Assoziation (durchgezogene Linie):** Verbindung zwischen Akteur und Use Case (Akteur initiiert oder empfängt das Ergebnis).
* **«include» (gestrichelter Pfeil vom Basis-Use-Case zum eingeschlossenen Use Case):** Erzwingt die Ausführung des Teilprozesses als zwingenden Bestandteil (z. B. Gegenleistungsverzicht bei Spendenabsicht oder Voraussetzungen prüfen bei Ausstellung).
* **«extend» (gestrichelter Pfeil vom Erweiterungs-Use-Case zum erweiterten Basis-Use-Case):** Optionale Erweiterung unter einer expliziten Bedingung (Extension Point: Betrag ≤ 300 €).

```mermaid
flowchart LR
    classDef actorStyle fill:#f8fafc,stroke:#2563eb,stroke-width:2px,font-weight:bold
    classDef ucStyle fill:#ffffff,stroke:#1e40af,stroke-width:1.5px
    classDef subUcStyle fill:#f0fdf4,stroke:#16a34a,stroke-width:1.5px
    classDef extUcStyle fill:#fefce8,stroke:#ca8a04,stroke-width:1.5px
    classDef boundaryStyle fill:#fafafa,stroke:#94a3b8,stroke-width:2px,stroke-dasharray: 4 4

    subgraph SpenderBoundary ["Initiator (Spenderseite)"]
        Spender["👤 Spender / Zuwendender<br/><i>(Spendenwillige Person)</i>"]:::actorStyle
    end

    subgraph System ["Spenden- und Compliance-System (Muster 034122)"]
        UC1(["UC-1: Spendenabsicht über White-Label-Wizard erfassen"]):::ucStyle
        UC1_1(["UC-1.1: Gegenleistungsverzicht erklären"]):::subUcStyle
        UC1_2(["UC-1.2: EPC-GiroCode generieren"]):::subUcStyle

        UC3(["UC-3: Geldeingang verbuchen & Referenz abgleichen"]):::ucStyle

        UC4(["UC-4: Zuwendungsbestätigung ausstellen"]):::ucStyle
        UC4_1(["UC-4.1: Steuerliche Voraussetzungen prüfen"]):::subUcStyle
        UC4_2(["UC-4.2: Vereinfachten Nachweis vorschlagen"]):::subUcStyle

        UC5(["UC-5: Gemeinnützigkeitsbescheid pflegen"]):::ucStyle
        UC6(["UC-6: White-Label-Mandantenprofil konfigurieren"]):::ucStyle
    end

    subgraph ExternalPayment ["Externer Kontext (Banking-App / Hausbank)"]
        UC2(["UC-2: Spende per Banking-App überweisen<br/><i>(Zahlungsanweisung via EPC-GiroCode)</i>"]):::extUcStyle
    end

    subgraph VereinBoundary ["Verwaltung & Externe"]
        Kassenwart["👤 Kassenwart / Vereinsvorstand<br/><i>(Zuwendungsempfänger)</i>"]:::actorStyle
        Finanzamt["🏛️ Finanzamt<br/><i>(Erteilt Freistellungs- / Feststellungsbescheid)</i>"]:::actorStyle
    end

    %% Spender-Interaktionen
    Spender --- UC1
    Spender --- UC2
    UC1_2 -.->|«liefert GiroCode für»| UC2

    %% Includes für UC-1 (Richtung: Basis-UC -> Included-UC)
    UC1 -.->|«include»| UC1_1
    UC1 -.->|«include»| UC1_2

    %% Kassenwart-Interaktionen
    UC3 --- Kassenwart
    UC4 --- Kassenwart
    UC5 --- Kassenwart
    UC6 --- Kassenwart

    %% Finanzamt als ausstellende Behörde für Bescheide (Assoziation zu UC-5)
    UC5 --- Finanzamt

    %% Includes & Extends für UC-4
    UC4 -.->|«include»| UC4_1
    UC4_2 -.->|«extend»<br/>[Bedingung: Betrag <= 300 €]| UC4
```

---

## 2. Akteure und Rollen

| Akteur | Typ | Beschreibung & Verantwortlichkeit |
|---|---|---|
| **Spender / Zuwendender** | Primärer Akteur (Mensch) | Natürliche oder juristische Person, die dem Verein finanzielle Mittel zuwendet. Nutzt den vereinsindividuellen White-Label-Wizard, erklärt verbindlich den Verzicht auf Gegenleistungen und führt die Zahlung per EPC-GiroCode oder klassischer Überweisung durch. |
| **Kassenwart / Vereinsvorstand** | Primärer Akteur (Mensch) | Verantwortliche Person des Zuwendungsempfängers. Konfiguriert das Vereins- und Spendenprofil, pflegt Freistellungs- bzw. Feststellungsbescheide ein, gleicht eingehende Zahlungen mit Spendenabsichten ab und veranlasst die Ausstellung rechtskonformer Zuwendungsbestätigungen. |
| **Finanzamt** | Sekundärer Akteur (Behörde / Institution) | Zuständiges Finanzamt, welches Freistellungsbescheide (§ 63 Abs. 5 AO) oder Feststellungsbescheide (§ 60a AO) erlässt und beim Spender den Sonderausgabenabzug nach § 10b EStG prüft. |

---

## 3. Anwendungsfälle im Detail

### UC-1: Spendenabsicht über White-Label-Wizard erfassen
* **Akteur:** Spender
* **Beschreibung:** Der Spender ruft die vereinsindividuelle Web-URL auf (z. B. `/spenden/katzenschutz-ms`), wählt Betrag und Satzungszweck, erfasst Name und Postanschrift für die spätere Quittung und schließt die Spendenabsicht ab.
* **Included Use Cases:**
  * **UC-1.1: Gegenleistungsverzicht erklären (`«include»`):** Der Spender bestätigt aktiv, dass der Zuwendung weder Gegenleistungen noch private Vorteile gegenüberstehen (OCL 7).
  * **UC-1.2: EPC-GiroCode generieren (`«include»`):** Das System erzeugt einen standardisierten SEPA-Credit-Transfer-Payload (EPC069-08) mit dynamischem Referenzcode (`SP-YYYY-NNNN`), um fehlerfreie Banküberweisungen zu ermöglichen.
* **Nachbedingung:** Spendenabsicht im Status `ANGEKUENDIGT` gespeichert; Spender sieht IBAN und scanbaren QR-Code.

### UC-2: Spende per Banking-App überweisen (Externer Vorgang)
* **Akteur:** Spender (Interaktion mit eigener Banking-App / Hausbank)
* **Beschreibung:** Der Spender scannt den in UC-1.2 generierten GiroCode mit seiner Banking-App (oder kopiert IBAN, Betrag und Referenzcode) und autorisiert die SEPA-Überweisung bei seiner Hausbank.
* **Systemgrenzen-Einordnung:** Dieser Anwendungsfall läuft außerhalb des Kernsystems im Drittsystem der Bank des Spenders ab und ist über den normierten EPC069-08 QR-Code nahtlos mit dem Spendenprozess gekoppelt.
* **Nachbedingung:** Zahlungsauftrag bei der Bank des Spenders ausgelöst.

### UC-3: Geldeingang verbuchen & Referenz abgleichen
* **Akteur:** Kassenwart
* **Beschreibung:** Nach Eingang der Gutschrift auf dem Vereinskonto gleicht der Kassenwart den Verwendungszweck mit den gespeicherten Spendenabsichten ab.
* **Nachbedingung:** Status der Spendenabsicht wechselt von `ANGEKUENDIGT` zu `EINGEGANGEN`. Eine `Geldspende` wird persistent erfasst.

### UC-4: Zuwendungsbestätigung ausstellen
* **Akteur:** Kassenwart
* **Beschreibung:** Der Kassenwart stößt für eine eingegangene Geldzuwendung die Erstellung des amtlichen PDF-Formulars (Muster 034122) an.
* **Included & Extended Use Cases:**
  * **UC-4.1: Steuerliche Voraussetzungen prüfen (`«include»`):** Zwingende automatische Vorab-Prüfung aller steuerlichen Bedingungen (Bescheiddatum ≤ 5 bzw. 3 Jahre, Satzungszweck-Deckung, Gegenleistungsverzicht).
  * **UC-4.2: Vereinfachten Nachweis vorschlagen (`«extend»`):** Falls der Betrag ≤ 300 € ist (§ 50 Abs. 4 EStDV), weist das System darauf hin, dass ein Kontoauszug genügt, ermöglicht aber auf Wunsch die Ausstellung.
* **Nachbedingung:** Rechtsgültiges PDF (Muster 034122) mit eindeutiger Bestätigungsnummer generiert und gesichert.

### UC-5: Gemeinnützigkeitsbescheid pflegen
* **Akteure:** Kassenwart (primär erfassend), Finanzamt (sekundär, ausstellende Behörde)
* **Beschreibung:** Erfassen und Aktualisieren von Freistellungsbescheiden (Gültigkeit: max. 5 Jahre ab Erlass) oder Feststellungsbescheiden (§ 60a AO, max. 3 Jahre ab Erlass) des zuständigen Finanzamts sowie Pflege des letzten Veranlagungszeitraums.
* **Nachbedingung:** Neuer Bescheid mit Bescheiddatum, Steuernummer und geförderten Zwecken hinterlegt.

### UC-6: White-Label-Mandantenprofil konfigurieren
* **Akteur:** Kassenwart
* **Beschreibung:** Konfiguration des öffentlichen Spendenauftritts: URL-Slug, Vereinsname, Logo-URL, Farbpalette (Corporate Design), Dankestext und Bankverbindungsdaten (IBAN, BIC).
* **Nachbedingung:** `WhiteLabelKonfiguration` aktualisiert; Wizard sofort im individuellen Look & Feel verfügbar.

---

## 4. Traceability-Matrix: Use Cases zu Domänenmodell und Codebase

Die nachfolgende Matrix dokumentiert die lückenlose Durchgängigkeit von den Anwendungsfällen über das [Klassendatenmodell](datenmodell.md) bis in die Quellcode-Implementierung:

| Use Case | Beteiligte Domänen-Entitäten & VOs | Driving Ports / Commands | Service / Engine | Status / Testabdeckung |
|---|---|---|---|---|
| **UC-1** | `Spendenabsicht`, `Zuwendungsempfaenger`, `Spender`, `SteuerbeguenstigterZweck` | `RegistriereSpendenabsichtUseCase`, `RegistriereSpendenabsichtCommand` | `SpendenabsichtService` | [`SpendenabsichtServiceTest`](../../src/test/kotlin/de/ad26/team07/application/service/SpendenabsichtServiceTest.kt) |
| **UC-1.1** | `Gegenleistungsverzichtserklaerung` (OCL 7) | `RegistriereSpendenabsichtCommand` (`gegenleistungsverzichtBestaetigt`) | `SpendenabsichtService` | [`SpendenabsichtServiceTest`](../../src/test/kotlin/de/ad26/team07/application/service/SpendenabsichtServiceTest.kt) |
| **UC-1.2** | `Spendenabsicht` (`giroCodePayload`) | Inbound Adapter (`MainKt`, `spenden_wizard.html`) | `GiroCodeGenerator` (EPC069-08), `VanillaQR` | [`GiroCodeGeneratorTest`](../../src/test/kotlin/de/ad26/team07/domain/service/GiroCodeGeneratorTest.kt) |
| **UC-2** | - (Extern über Spender-Banking-App) | SEPA EPC069-08 QR-Code | Banking-App des Spenders | Manuell verifiziert via Banking-Scan |
| **UC-3** | `Spendenabsicht` (`markiereAlsEingegangen()`), `Geldspende` | `SpendenabsichtRepositoryPort` | Application Service | [`SpendenabsichtTest`](../../src/test/kotlin/de/ad26/team07/domain/model/SpendenabsichtTest.kt) |
| **UC-4** | `Zuwendungsbestaetigung`, `Geldzuwendung` | Outbound Port (PDF Generator) | PDF Adapter (Muster 034122) | Domänen-Invarianten spezifiziert |
| **UC-4.1** | `Gemeinnuetzigkeitsbescheid`, `Freistellungsbescheid`, `Feststellungsbescheid` | `Gemeinnuetzigkeitsbescheid.istGueltigAm()` | Domain-Validierung | [`GemeinnuetzigkeitsbescheidTest`](../../src/test/kotlin/de/ad26/team07/domain/model/GemeinnuetzigkeitsbescheidTest.kt) |
| **UC-5** | `Gemeinnuetzigkeitsbescheid`, `Zuwendungsempfaenger` | `VereinRepositoryPort` | Application Service | [`GemeinnuetzigkeitsbescheidTest`](../../src/test/kotlin/de/ad26/team07/domain/model/GemeinnuetzigkeitsbescheidTest.kt) |
| **UC-6** | `WhiteLabelKonfiguration`, `Zuwendungsempfaenger` | `VereinRepositoryPort` | Application Service | [`spenden_wizard.html`](../../demo/spenden_wizard.html) |