# Datenmodell: Zuwendungsbestätigung für Geldzuwendungen (Muster 034122)

Dieses Datenmodell bildet die fachlichen Geschäftsobjekte, Value Objects, Beziehungen und steuerrechtlichen Invarianten für die Ausstellung von Zuwendungsbestätigungen (Spendenquittungen) nach amtlichem BMF-Muster 034122 (Fassung 2013) auf Vereinsebene ab.

Das Modell ist streng nach den Prinzipien des **Domain-Driven Designs (DDD)** und formaler **UML-Spezifikation** atomar aufgebaut:
* **Atomare Value Objects:** Kapselung von Adress- und Betragsdaten (1. Normalform).
* **Polymorphe Spezialisierung:** Aufspaltung von `Geldzuwendung` in `Geldspende` (inkl. `Aufwandsspende`) und `Mitgliedsbeitrag`.
* **Trennung von Daten und Regeln:** Fristen (§ 63 Abs. 5 AO, § 60a AO) sind als Methoden und Invarianten ({Constraints}) formuliert, nicht als Pseudodatenfelder.
* **Hierarchische Detaillierungsstufen:**
  * **Level 1 (Atomare Gesamtübersicht):** Primäre Geschäftsobjekte, Value Objects und Transaktionen.
  * **Level 2 (Detailmodell Gemeinnützigkeitsbescheid):** Steuerrechtliche Vererbungshierarchie der Bescheide, Fristen und Gültigkeitsprüfungen.

---

## Level 1: Atomare Gesamtübersicht

Konzentriert sich auf den Gesamtprozess von der Vorankündigung im White-Label-Spenden-Wizard bis zur Bestätigung: Der Spender durchläuft den vereinsgebrandeten Wizard (Spendenabsicht mit EPC-GiroCode und Verzichtserklärung), tätigt die Geldspende, und das System prüft Satzungszwecke sowie Bescheide für die Zuwendungsbestätigung.

```mermaid
classDiagram
direction TB

namespace Stammdaten {
  class Adresse {
    +strasse: String
    +hausnummer: String
    +plz: String
    +ort: String
    +land: String = "DE"
  }

  class Zuwendungsempfaenger["Zuwendungsempfänger (Verein)"] {
    +name: String
    +steuernummer: String
    +finanzamt: String
    +rechtsform: String = "e. V."
    +iban: String
    +bic: String
    +kontoinhaber: String
    +ibanBereinigt: String
  }

  class WhiteLabelKonfiguration["White-Label-Konfiguration"] {
    +slug: String
    +wizardTitel: String
    +logoUrl: String
    +primaerFarbe: String = "#2563eb"
    +dankesNachricht: String
  }

  class Spender["Zuwendender (Spender)"] {
    +name: String
    +spenderArt: String = "natürliche Person"
    +email: String
  }

  class SteuerbeguenstigterZweck["Steuerbegünstigter Zweck (§ 52 AO)"] {
    +paragraf: String
    +bezeichnung: String
    +istAusschlussZweck: Boolean = false
  }
}

namespace SpendenWizard {
  class SpendenabsichtStatus {
    <<enumeration>>
    ANGEKUENDIGT
    EINGEGANGEN
    ABGELAUFEN
  }

  class GiroCodeGenerator {
    <<service>>
    +generierePayload(bic: String, name: String, iban: String, betrag: Decimal, zweck: String) String
  }

  class Spendenabsicht["Spendenabsicht (Pledge)"] {
    +id: String
    +referenzCode: String
    +betrag: Decimal
    +waehrung: String = "EUR"
    +erstellungsDatum: DateTime
    +status: SpendenabsichtStatus
    +giroCodePayload: String
    +markiereAlsEingegangen() Spendenabsicht
  }
}

namespace Bescheidwesen {
  class Gemeinnuetzigkeitsbescheid["Gemeinnützigkeitsbescheid 🔍"] {
    <<abstract>>
    +steuernummer: String
    +finanzamt: String
    +bescheidDatum: Date
    +istGueltigAm(datum: Date) Boolean*
  }
}

namespace TransaktionUndNachweis {
  class Geldzuwendung {
    <<abstract>>
    +betrag: Decimal
    +waehrung: String = "EUR"
    +zuwendungsDatum: Date
  }

  class Geldspende {
    +istAufwandsspende: Boolean = false
  }

  class Mitgliedsbeitrag {
    +beitragsJahr: Integer
  }

  class Zuwendungsbestaetigung["Zuwendungsbestätigung (Muster 034122)"] {
    +bestaetigungsNummer: String
    +ausstellungsDatum: Date
    +ausstellungsOrt: String
    +betragInWorten: String
    +hinweisKeinAusschlussBeitrag: Boolean
    +unterschrift: String
    +pruefeBescheidGueltigkeit() Boolean
  }

  class Gegenleistungsverzichtserklaerung["Verzichtserklärung (Aufwand/Gegenleistung)"] {
    +erklaerungsDatum: Date
    +istGegenleistungsfrei: Boolean = true
    +verzichtAufAnspruch: String
  }
}

%% Stammdaten Kompositionen
Zuwendungsempfaenger *-- "1" WhiteLabelKonfiguration : whiteLabelKonfiguration
Zuwendungsempfaenger *-- "1" Adresse : sitz
Spender *-- "1" Adresse : anschrift

%% Vereinszuordnung Satzungszweck & Bescheide
Zuwendungsempfaenger "1" *-- "1..*" Gemeinnuetzigkeitsbescheid : gemeinnuetzigkeitsbescheide
Zuwendungsempfaenger "1" --> "1..*" SteuerbeguenstigterZweck : satzungszwecke
Gemeinnuetzigkeitsbescheid "0..*" --> "1..*" SteuerbeguenstigterZweck : beguenstigt

%% Spendenabsicht (Pledge): Navigierbarkeit zum Verein, Spender, Zweck und Status
Spendenabsicht "0..*" --> "1" Zuwendungsempfaenger : empfaenger
Spendenabsicht "0..*" --> "1" Spender : spender
Spendenabsicht "0..*" --> "1" SteuerbeguenstigterZweck : gewidmeterZweck
Spendenabsicht "0..*" --> "1" SpendenabsichtStatus : status
Spendenabsicht "1" *-- "1" Gegenleistungsverzichtserklaerung : verzichtserklaerung
Spendenabsicht ..> GiroCodeGenerator : erzeugtPayloadMit

%% Transaktion & Realisierung
Geldspende "0..1" --> "0..1" Spendenabsicht : basiert_auf
Geldzuwendung "0..*" --> "1" Spender : zuwendender
Geldzuwendung "0..*" --> "1" Zuwendungsempfaenger : empfaenger

%% Spezialisierungen (Generalisierung)
Geldzuwendung <|-- Geldspende
Geldzuwendung <|-- Mitgliedsbeitrag

%% Nachweis Aufwandsspende
Geldspende "1" o-- "0..1" Gegenleistungsverzichtserklaerung : belegt_verzicht

%% Bestätigungsausstellung
Zuwendungsbestaetigung "0..1" --> "1" Geldzuwendung : bescheinigt
Zuwendungsbestaetigung "0..*" --> "1" Gemeinnuetzigkeitsbescheid : nennt
Zuwendungsbestaetigung "0..*" --> "1..*" SteuerbeguenstigterZweck : foerdert
```

---

## Level 2: Detailmodell Gemeinnützigkeitsbescheid & Fristen

Detailansicht der Spezialisierungen des Gemeinnützigkeitsbescheids nach Abgabenordnung (AO) und deren Validierungslogik:

* **Freistellungsbescheid (Regelfall):** Körperschaftsteuerbefreiung nach Veranlagung. Gültigkeitsdauer: höchstens 5 Jahre ab Bescheiddatum (§ 63 Abs. 5 AO). Pflichtangabe: *Letzter Veranlagungszeitraum*.
* **Feststellungsbescheid (§ 60a AO):** Vorläufige Feststellung der satzungsmäßigen Voraussetzungen (z. B. Neugründungen oder nach Satzungsänderungen). Gültigkeitsdauer: höchstens 3 Jahre.
* **Entweder-Oder (Regel 3):** Auf der Zuwendungsbestätigung wird genau einer der beiden Bescheide angegeben, welcher am `ausstellungsDatum` der Bestätigung noch gültig ist.

```mermaid
classDiagram
direction TB

class Zuwendungsempfaenger["Zuwendungsempfänger (Verein)"] {
  +name: String
  +steuernummer: String
  +finanzamt: String
}

class Gemeinnuetzigkeitsbescheid["Gemeinnützigkeitsbescheid"] {
  <<abstract>>
  +finanzamt: String
  +steuernummer: String
  +bescheidDatum: Date
  +istGueltigAm(datum: Date): Boolean*
}

class Freistellungsbescheid {
  +letzterVeranlagungszeitraum: Integer
  +istGueltigAm(datum: Date): Boolean
}
note for Freistellungsbescheid "Fristregel (§ 63 Abs. 5 AO):\nGültig max. 5 Jahre ab Bescheiddatum:\nausstellungsDatum <= bescheidDatum + 5 Jahre"

class Feststellungsbescheid {
  +rechtsgrundlage: String = "§ 60a AO"
  +istGueltigAm(datum: Date): Boolean
}
note for Feststellungsbescheid "Fristregel (§ 60a AO):\nGültig max. 3 Jahre ab Feststellung:\nausstellungsDatum <= bescheidDatum + 3 Jahre"

class SteuerbeguenstigterZweck["Steuerbegünstigter Zweck (§ 52 AO)"] {
  +paragraf: String
  +bezeichnung: String
}

class Zuwendungsbestaetigung["Zuwendungsbestätigung"] {
  +ausstellungsDatum: Date
  +pruefeBescheidGueltigkeit(): Boolean
}
note for Zuwendungsbestaetigung "Constraint {Entweder-Oder}:\nGenau ein gültiger Bescheid (Freistellung ODER Feststellung)\nmuss zum Ausstellungsdatum der Bestätigung gültig sein."

Zuwendungsempfaenger "1" *-- "1..*" Gemeinnuetzigkeitsbescheid : gemeinnuetzigkeitsbescheide
Gemeinnuetzigkeitsbescheid <|-- Freistellungsbescheid
Gemeinnuetzigkeitsbescheid <|-- Feststellungsbescheid
Gemeinnuetzigkeitsbescheid "0..*" --> "1..*" SteuerbeguenstigterZweck : beguenstigt
Zuwendungsempfaenger "1" --> "1..*" SteuerbeguenstigterZweck : satzungszwecke
Zuwendungsbestaetigung "0..*" --> "1" Gemeinnuetzigkeitsbescheid : nennt
Zuwendungsbestaetigung "0..*" --> "1..*" SteuerbeguenstigterZweck : foerdert
```

---

## Level 3: Hexagonale Architektur (Ports & Adapters)

Das nachfolgende Klassendiagramm spezifiziert die lose Kopplung und Abhängigkeitsrichtung der Anwendungsschichten gemäß Hexagonal Architecture (Alistair Cockburn). Abhängigkeiten zeigen ausnahmslos von den äußeren Adaptern nach innen zu den Ports und dem Domänenkern:

```mermaid
classDiagram
direction TB

namespace PrimaryAdapters_Driving {
  class SpendenWizardWebAdapter["Web-Spenden-Wizard (HTML/JS)"] {
    +zeigeWizard(slug: String)
    +uebermittlePledge(formData)
  }
  class MainCliDemo["MainKt (CLI Demo Runner)"] {
    +main()
  }
}

namespace InboundPorts_Driving {
  class RegistriereSpendenabsichtUseCase {
    <<interface>>
    +registriere(command: RegistriereSpendenabsichtCommand) Spendenabsicht
  }
  class RegistriereSpendenabsichtCommand {
    +vereinSlug: String
    +zweckParagraf: String
    +betrag: BigDecimal
    +spenderName: String
    +spenderAnschrift: Adresse
    +spenderEmail: String?
    +gegenleistungsverzichtBestaetigt: Boolean
  }
}

namespace ApplicationServices {
  class SpendenabsichtService {
    -VereinRepositoryPort vereinRepository
    -SpendenabsichtRepositoryPort spendenabsichtRepository
    +registriere(command: RegistriereSpendenabsichtCommand) Spendenabsicht
    -generiereReferenzCode() String
  }
}

namespace OutboundPorts_Driven {
  class VereinRepositoryPort {
    <<interface>>
    +findeNachSlug(slug: String) Zuwendungsempfaenger?
    +speichere(verein: Zuwendungsempfaenger)
  }
  class SpendenabsichtRepositoryPort {
    <<interface>>
    +speichere(spendenabsicht: Spendenabsicht)
    +findeNachReferenzCode(referenzCode: String) Spendenabsicht?
    +findeAlleFuerVerein(vereinSlug: String) List~Spendenabsicht~
  }
}

namespace SecondaryAdapters_Driven {
  class InMemoryVereinRepository {
    -Map vereine
    +findeNachSlug(slug: String) Zuwendungsempfaenger?
    +speichere(verein: Zuwendungsempfaenger)
  }
  class InMemorySpendenabsichtRepository {
    -List absichten
    +speichere(spendenabsicht: Spendenabsicht)
    +findeNachReferenzCode(referenzCode: String) Spendenabsicht?
    +findeAlleFuerVerein(vereinSlug: String) List~Spendenabsicht~
  }
  class VanillaQREngine["VanillaQR (Client-Side SVG Engine)"] {
    +create(payload: String)
    +toSVG(pixelSize: Int) String
  }
}

namespace DomainCore {
  class Spendenabsicht["Spendenabsicht (Entity)"]
  class Zuwendungsempfaenger["Zuwendungsempfaenger (Entity)"]
  class GiroCodeGenerator["GiroCodeGenerator (Domain Service)"]
}

%% Driving Adapter rufen Driving Ports auf
SpendenWizardWebAdapter ..> RegistriereSpendenabsichtUseCase : ruft_auf
MainCliDemo ..> RegistriereSpendenabsichtUseCase : ruft_auf
RegistriereSpendenabsichtUseCase ..> RegistriereSpendenabsichtCommand : verarbeitet

%% Application Service implementiert Inbound Port
SpendenabsichtService ..|> RegistriereSpendenabsichtUseCase : implements

%% Application Service nutzt Outbound Ports und Domain
SpendenabsichtService --> VereinRepositoryPort : vereinRepository
SpendenabsichtService --> SpendenabsichtRepositoryPort : spendenabsichtRepository
SpendenabsichtService ..> Spendenabsicht : erzeugt_und_orchestriert
SpendenabsichtService ..> Zuwendungsempfaenger : liest_verein
Spendenabsicht ..> GiroCodeGenerator : erzeugtPayloadMit

%% Secondary Adapter implementieren Outbound Ports
InMemoryVereinRepository ..|> VereinRepositoryPort : implements
InMemorySpendenabsichtRepository ..|> SpendenabsichtRepositoryPort : implements
SpendenWizardWebAdapter ..> VanillaQREngine : rendertSVG
```

---

## 4. Fachliche Invarianten & OCL-Constraints

Die nachfolgenden formalen Integritätsbedingungen sichern die steuerrechtliche Compliance:

1. **Gültigkeit des Freistellungsbescheids (§ 63 Abs. 5 AO):**
   ```ocl
   context Freistellungsbescheid::istGueltigAm(datum: Date): Boolean
   body: not datum.isBefore(self.bescheidDatum) and datum <= self.bescheidDatum.addYears(5)
   ```

2. **Gültigkeit des Feststellungsbescheids (§ 60a AO):**
   ```ocl
   context Feststellungsbescheid::istGueltigAm(datum: Date): Boolean
   body: not datum.isBefore(self.bescheidDatum) and datum <= self.bescheidDatum.addYears(3)
   ```

3. **Gültiger Bescheid bei Ausstellung:**
   ```ocl
   context Zuwendungsbestaetigung
   inv BescheidIstGueltig:
     self.gemeinnuetzigkeitsbescheid.istGueltigAm(self.ausstellungsDatum) = true
   ```

4. **Zweckkongruenz (Matching):**
   Der auf der Zuwendungsbestätigung genannte Zweck muss sowohl durch den Gemeinnützigkeitsbescheid befreit sein als auch der Satzung des Zuwendungsempfängers entsprechen:
   ```ocl
   context Zuwendungsbestaetigung
   inv ZweckUebereinstimmung:
     self.steuerbeguenstigterZweck->forAll(z |
       self.gemeinnuetzigkeitsbescheid.steuerbeguenstigterZweck->includes(z) and
       self.geldzuwendung.zuwendungsempfaenger.satzungszwecke->includes(z)
     )
   ```

5. **Aufwandsspende und Verzichtserklärung:**
   Wird eine Geldspende als Aufwandsspende deklariert (`istAufwandsspende = true`), muss zwingend eine Gegenleistungsverzichtserklärung vorliegen:
   ```ocl
   context Geldspende
   inv AufwandsspendeErfordertVerzicht:
     self.istAufwandsspende implies self.gegenleistungsverzichtserklaerung->notEmpty()
   ```

6. **Betragskonsistenz:**
   Der numerische Betrag der Geldzuwendung muss mit dem ausgeschriebenen Betrag auf der Bestätigung korrespondieren:
   ```ocl
   context Zuwendungsbestaetigung
   inv BetragStimmig:
     self.betragInWorten = formatInWords(self.geldzuwendung.betrag)
   ```

7. **Gegenleistungsfreiheit im Spenden-Wizard:**
   Jede im Wizard erfasste Spendenabsicht muss eine bestätigte Gegenleistungsverzichtserklärung enthalten:
   ```ocl
   context Spendenabsicht
   inv VerzichtErklaert:
     self.verzichtserklaerung->notEmpty() and
     self.verzichtserklaerung.istGegenleistungsfrei = true
   ```

8. **Konsistenz bei Realisierung der Spende:**
   Wird eine Geldspende auf Basis einer Spendenabsicht verbucht, müssen Betrag und Zuwendungsempfänger exakt übereinstimmen:
   ```ocl
   context Geldspende
   inv AbsichtUebereinstimmung:
     self.spendenabsicht->notEmpty() implies
       (self.betrag = self.spendenabsicht.betrag and
        self.zuwendungsempfaenger = self.spendenabsicht.empfaenger and
        self.spendenabsicht.status = SpendenabsichtStatus::EINGEGANGEN)
   ```

