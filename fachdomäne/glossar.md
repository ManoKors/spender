# Glossar der Fachdomäne: Steuerliche Spenden-Compliance

Das fachliche Glossar definiert die zentralen Geschäftsobjekte (GO) und ihre Attribute für die steuerliche Geltendmachung von Spenden, steuerrechtliche Compliance und Belegprüfung im Vereins- und Universitätskontext.

Gemäß den Vorgaben des Workshops folgt das Glossar diesen Prinzipien:
- **Erweiterte Form:** 3 Spalten (*Geschäftsobjekt*, *Attribut*, *Definition*). In der ersten Zeile steht das Geschäftsobjekt mit seiner Definition, darunter versetzt die zugehörigen Attribute.
- **Singular-Konvention:** Alle Geschäftsobjekte sind konsequent im Singular benannt.
- **Rein fachlich:** Keine Datentypen, keine Sichtbarkeiten, keine Datenbankbegriffe.
- **Trennung von Vorgängen & Regeln:** Tätigkeiten (z. B. *Belegprüfung*) gehören primär in die Prozessmodelle; rechtliche Invarianten (z. B. *Gegenleistungsverbot*) sind fachliche Regeln und keine Klassen.

---

## 1. Geschäftsobjekte und Attribute

| Geschäftsobjekt | Attribut | Definition |
|---|---|---|
| **Spender** | | Natürliche oder juristische Person, die einer steuerbegünstigten Organisation uneigennützig eine Zuwendung erbringt (Synonym: *Zuwendender*). |
| | Name | Vollständiger Personen- oder Firmenname des Spenders. |
| | Anschrift | Wohnsitz bzw. Unternehmenssitz (erforderlich für die postalische Zustellung der Bestätigung). |
| | Spenderart | Unterscheidung zwischen natürlicher Person (Sonderausgabenabzug) und juristischer Person (Betriebsausgabenabzug). |
| **Zuwendungsempfänger** | | Steuerbegünstigte Körperschaft (z. B. gemeinnütziger Verein, Stiftung) oder Körperschaft des öffentlichen Rechts (z. B. Universität), die die Zuwendung empfängt und zur Ausstellung der Bestätigung berechtigt ist. |
| | Name | Offizielle Bezeichnung der empfangenden Institution. |
| | Rechtsform | Juristische Organisationsform (z. B. e. V., Stiftung, Körperschaft des öffentlichen Rechts). |
| | Steuernummer | Vom zuständigen Finanzamt vergebene Steuernummer der Körperschaft. |
| | Satzungszweck | Die laut Satzung oder Gesetz geförderten gemeinnützigen, mildtätigen oder kirchlichen Zwecke (§§ 51 ff. AO). |
| **Freistellungsbescheid** | | Vom Finanzamt erteilter Verwaltungsakt, der die Körperschaftsteuerbefreiung wegen Gemeinnützigkeit bescheinigt. Dient als formeller Nachweis der Spendenempfangsberechtigung. |
| | Ausstellungsdatum | Datum, an dem der Bescheid durch das Finanzamt erlassen wurde (darf zum Spendenzeitpunkt i. d. R. maximal 5 Jahre alt sein). |
| | Veranlagungszeitraum | Die Steuerjahre, für die die Steuerbefreiung verbindlich festgestellt wurde. |
| | Ausstellendes Finanzamt | Name und Sitz des für die Körperschaft zuständigen Finanzamts. |
| | Begünstigter Zweck | Konkrete Bestätigung der steuerbegünstigten Zwecke laut Bescheid. |
| **Spende** | | Die freiwillige, uneigennützige Zuwendung eines Vermögenswerts an den Zuwendungsempfänger ohne Gegenleistung. |
| | Zuwendungsdatum | Der Tag, an dem die Zuwendung dem Empfänger tatsächlich zugeflossen ist. |
| | Betrag | Der finanzielle Gesamtwert der Zuwendung in Euro. |
| | Verwendungszweck | Zweckbestimmung der Spende (z. B. zweckgebunden für ein konkretes Forschungsprojekt oder ungebunden). |
| | Spendenrhythmus | Unterscheidung, ob es sich um eine punktuelle *Einmalspende* oder eine wiederkehrende *Dauerspende* handelt. |
| **Geldspende** | | Spezialisierung der Spende in Form von flüssigen Zahlungsmitteln (Überweisung, SEPA-Lastschrift, Barzahlung). |
| | Zahlungsart | Der genutzte Zahlungsweg (z. B. Banküberweisung, Lastschrift, Barzahlung). |
| | Buchungstag | Datum des Geldeingangs auf dem Bankkonto des Empfängers. |
| **Sachspende** | | Spezialisierung der Spende durch unentgeltliche Übereignung materieller Wirtschaftsgüter (z. B. Laborgeräte, IT-Hardware, Bücher, Kunstwerke). |
| | Gegenstandsbeschreibung | Genaue Bezeichnung, Modell, Typ und Stückzahl des gespendeten Gegenstands. |
| | Zustand | Beschreibung des Erhaltungszustands (neu, gebraucht, Alter). |
| | Herkunft | Angabe, ob der Gegenstand aus dem Privatvermögen oder Betriebsvermögen des Spenders stammt. |
| | Gemeiner Wert | Der im gewöhnlichen Geschäftsverkehr zu erzielende Verkehrswert/Marktwert zum Zeitpunkt der Zuwendung. |
| | Wertermittlungsmethode | Grundlage des Wertansatzes (z. B. Original-Kaufbeleg, Gutachten, Listenpreis abzüglich Abschreibung). |
| **Aufwandsspende** | | Sonderform der Zuwendung, bei der auf einen bestehenden, vertraglich vereinbarten Aufwendungsersatz- oder Vergütungsanspruch (z. B. Ehrenamtspauschale, Reisekosten) verzichtet wird. |
| | Anspruchsart | Art des zugrundeliegenden Anspruchs (z. B. Reisekostenersatz, Übungsleiterfreibetrag). |
| | Anspruchsdatum | Datum des Vertrags oder Beschlusses, aus dem der ursprüngliche Anspruch hervorgeht. |
| | Verzichtserklärung | Schriftliche Bestätigung des Spenders über den nachträglichen und zeitnahen Verzicht auf Auszahlung. |
| **Zuwendungsbestätigung** | | Das gesetzlich vorgeschriebene Formular (umgangssprachlich *Spendenbescheinigung*) nach amtlichem BMF-Muster, das dem Spender die Zuwendung bescheinigt. |
| | Bestätigungsnummer | Eindeutige, fortlaufende Registrierungsnummer zur internen Nachverfolgung. |
| | Ausstellungsdatum | Datum, an dem die Bescheinigung formell ausgestellt und unterzeichnet wird. |
| | BetragInWorten | Der bestätigte Zuwendungsbetrag in ausgeschriebenen Worten (Schutz vor Manipulation). |
| | BestätigterZweck | Bestätigung, dass die Mittel ausschließlich für steuerbegünstigte Satzungszwecke verwendet werden. |
| | Gegenleistungsverzichtserklärung | Gesetzlich vorgegebene Klausel, dass dem Spender keine Gegenleistung gewährt wurde. |
| **Vereinfachter Zuwendungsnachweis** | | Gesetzliche Nachweiserleichterung (§ 50 Abs. 4 EStDV) für Zuwendungen bis 300 Euro, die ohne förmliche Zuwendungsbestätigung auskommt. |
| | Buchungsbestätigung | Nachweis über die Kontobelastung (z. B. Kontoauszug, Lastschriftermächtigung, Bareinzahlungsbeleg). |
| | Höchstgrenze | Gesetzlicher Grenzbetrag (aktuell 300 Euro je Einzelzuwendung). |
| **Sachwertgutachten** | | Schriftliche Wertermittlung durch eine fachkundige Person oder einen Gutachter, erforderlich bei schwer bewertbaren Sachspenden ohne Kaufbeleg. |
| | Gutachter | Name und Qualifikation der bewertenden Person oder Institution. |
| | Gutachtendatum | Datum der Begutachtung und Wertermittlung. |
| | Gutachtenwert | Der vom Gutachter verbindlich festgestellte Schätzwert in Euro. |
| **Beleg** | | Physisches oder digitales Dokument, das als Nachweis für die Zuwendung, deren Wert oder Berechtigung dient. |
| | Belegnummer | Interne oder externe Kennzeichnung des Belegs. |
| | Belegdatum | Ausstellungs- bzw. Buchungsdatum des Nachweisdokuments. |
| | Belegart | Klassifizierung (z. B. Kaufrechnung, Fahrtenbuch, Kontoauszug, Schenkungsvertrag). |
| **Prüfprotokoll** | | Das aus der Compliance- und Belegprüfung hervorgehende Dokument, das die steuerrechtliche Zulässigkeit der Spendenbescheinigung dokumentiert. |
| | Prüfdatum | Zeitpunkt der durchgeführten Prüfung. |
| | Prüfstatus | Ergebnis der Prüfung (z. B. *Freigegeben*, *Nachforderung erforderlich*, *Abgelehnt*). |
| | Prüfer | Name bzw. Kennung der zuständigen Person im Verein oder der Universität. |
| | Beanstandung | Dokumentation eventueller Mängel (z. B. fehlender Kaufbeleg, veralteter Freistellungsbescheid). |

---

## 2. Fachliche Regeln & Prinzipien (Business Rules)

Begriffe, die keine eigenständigen Geschäftsobjekte mit Attributen darstellen, sondern grundlegende steuerrechtliche Regeln und Invarianten bilden:

* **Gegenleistungsverbot:** Eine Zuwendung ist nur dann steuerlich begünstigt, wenn sie uneigennützig und ohne jedwede materielle oder immaterielle Gegenleistung erbracht wird. Grenzt Spenden von steuerpflichtigem Sponsoring oder Werbeleistungen ab.
* **Steuerliche Absetzbarkeit:** Rechtliche Wirkung beim Spender. Mindert das zu versteuernde Einkommen als Sonderausgabe (§ 10b EStG) oder den Gewinn als Betriebsausgabe (§ 9 Abs. 1 Nr. 2 KStG).
* **Amtliches Muster:** Verbindliche Formularvorgabe des Bundesfinanzministeriums (BMF). Bereits geringfügige Abweichungen vom vorgeschriebenen Wortlaut können zur Nichtanerkennung oder Falschausstellerhaftung führen.

---

## 3. Prozesse & Vorgänge

Begriffe, die zeitliche Abläufe und Handlungen beschreiben und in den nachfolgenden Prozessmodellen (Szenarien und Flowcharts) detailliert werden:

* **Belegprüfung:** Formelle und materielle Prüfung eingereichter Nachweise auf Echtheit, Vollständigkeit und steuerrechtliche Plausibilität vor Ausstellung der Zuwendungsbestätigung.
* **Spendenbescheinigung ausstellen:** Der geregelte Ablauf von der Erfassung der Spende über die Prüfung bis zur rechtsgültigen Unterzeichnung und Ausgabe der Zuwendungsbestätigung.
