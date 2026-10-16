# Datenmodell: Zuwendungsbestätigung für Geldzuwendungen (Muster 034122)

Dieses Datenmodell bildet die fachlichen Geschäftsobjekte und Beziehungen für die Ausstellung von Zuwendungsbestätigungen (Spendenquittungen) nach amtlichem BMF-Muster 034122 (Fassung 2013) auf Vereinsebene ab.

Das Modell ist hierarchisch in zwei Detaillierungsstufen unterteilt:
* **Level 1 (Gesamtübersicht):** Primäre Geschäftsobjekte der Spendenabwicklung.
* **Level 2 (Detailmodell Gemeinnützigkeitsbescheid):** Drill-Down in die steuerrechtliche Vererbungshierarchie der Bescheide und deren Gültigkeitsfristen.

---

## Level 1: Gesamtübersicht

Konzentriert sich auf den Kernprozess: Der Spender leistet eine Geldzuwendung, der Verein als Zuwendungsempfänger empfängt sie, prüft den Gemeinnützigkeitsbescheid und stellt die Zuwendungsbestätigung aus.

```mermaid
classDiagram
direction TB

namespace Vereinskontext {
  class Zuwendungsempfaenger["Zuwendungsempfänger"] {
    Bezeichnung
    Anschrift
  }
  class Zweck["Steuerbegünstigter Zweck"] {
    Wortlaut des Zwecks
  }
  class Bescheid["Gemeinnützigkeitsbescheid"] {
    Finanzamt
    Steuernummer
    Datum des Bescheids
    Bescheidart: Freistellung oder Feststellung
  }
}

namespace Zuwendungskontext {
  class Spender["Spender"] {
    Name
    Anschrift
  }
  class Geldzuwendung {
    Zuwendungsart
    Betrag
    Tag der Zuwendung
    Verzicht auf Erstattung
  }
  class Gegenleistungsverzichtserklaerung["Gegenleistungsverzichtserklärung"] {
    Erklärungsdatum
  }
  class Zuwendungsbestaetigung["Zuwendungsbestätigung"] {
    Betrag in Buchstaben
    Bestätigung kein ausgeschlossener Mitgliedsbeitrag
    Ausstellungsort
    Ausstellungsdatum
    Unterschrift
    Muster
  }
}

Spender "1" -- "0..*" Geldzuwendung : leistet
Zuwendungsempfaenger "1" -- "0..*" Geldzuwendung : empfängt
Geldzuwendung "1" *-- "0..1" Gegenleistungsverzichtserklaerung : hat
Geldzuwendung "1" -- "0..1" Zuwendungsbestaetigung : wird bestätigt durch

Zuwendungsbestaetigung "0..*" -- "1" Bescheid : nennt
Zuwendungsempfaenger "1" -- "0..*" Bescheid : erhält
Zuwendungsempfaenger "0..*" o-- "1..*" Zweck : Satzungszweck
Zuwendungsbestaetigung "0..*" -- "1..*" Zweck : bestätigter Zweck
Bescheid "0..*" -- "1..*" Zweck : bescheinigter Zweck
```

---

## Level 2: Detailmodell Gemeinnützigkeitsbescheid & Fristen

Detailansicht der Spezialisierungen des Gemeinnützigkeitsbescheids nach Abgabenordnung (AO):

* **Freistellungsbescheid (Regelfall):** Körperschaftsteuerbefreiung nach Veranlagung. Gültigkeitsdauer: höchstens 5 Jahre ab Bescheiddatum (§ 63 Abs. 5 AO). Pflichtangabe: *Letzter Veranlagungszeitraum*.
* **Feststellungsbescheid (§ 60a AO):** Vorläufige Feststellung der satzungsmäßigen Voraussetzungen (z. B. Neugründungen oder nach Satzungsänderungen). Gültigkeitsdauer: höchstens 3 Jahre.
* **Entweder-Oder (Regel 3):** Auf der Zuwendungsbestätigung wird genau einer der beiden Bescheide angegeben.

```mermaid
classDiagram
direction TB

class Zuwendungsempfaenger["Zuwendungsempfänger"]
class Bescheid["Gemeinnützigkeitsbescheid"]
class Zweck["Steuerbegünstigter Zweck"]
class Zuwendungsbestaetigung["Zuwendungsbestätigung"]
class Freistellungsbescheid
class Feststellungsbescheid

Zuwendungsempfaenger : Bezeichnung
Zuwendungsempfaenger : Anschrift

Bescheid : Finanzamt
Bescheid : Steuernummer
Bescheid : Datum des Bescheids

Freistellungsbescheid : Letzter Veranlagungszeitraum
Freistellungsbescheid : Gueltigkeit max 5 Jahre nach Paragraph 63 Abs 5 AO

Feststellungsbescheid : Rechtsgrundlage Paragraph 60a AO
Feststellungsbescheid : Gueltigkeit max 3 Jahre nach Feststellung

Zweck : Wortlaut des Zwecks
Zuwendungsbestaetigung : Ausstellungsdatum

Zuwendungsempfaenger "1" -- "0..*" Bescheid : erhält
Bescheid <|-- Freistellungsbescheid
Bescheid <|-- Feststellungsbescheid
Bescheid "0..*" -- "1..*" Zweck : bescheinigter Zweck
Zuwendungsempfaenger "0..*" o-- "1..*" Zweck : Satzungszweck
Zuwendungsbestaetigung "0..*" -- "1" Bescheid : nennt
```
