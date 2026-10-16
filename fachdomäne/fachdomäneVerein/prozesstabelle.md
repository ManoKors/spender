# Szenario-Vorlage und Beispiel

Ein Szenario besteht aus einem **Header** und bis zu **drei Szenario-Tabellen**. Teil 1 ist die leere Vorlage zum Kopieren, Teil 2 ist ein ausgefülltes Beispiel für das Tool zu Zuwendungsbestätigungen (Verein, Muster 034122, nur Geldzuwendungen).

---

## Teil 1: Vorlage

### Header

| Feld | Bedeutung | Eintrag |
|---|---|---|
| Name | Name des Use Case, exakt wie im Use-Case-Diagramm | |
| Auslösender Akteur | Wer stößt den Ablauf an? | |
| Weitere Akteure | Wer ist sonst beteiligt (inkl. externer Systeme)? | |
| Auslöser | Welches Ereignis startet den Ablauf? | |
| Vorbedingung | Was muss gelten, damit der Ablauf überhaupt starten kann? | |
| Nachbedingung | Was gilt nach erfolgreichem Abschluss? | |

### Hauptszenario (Happy Path)

Alles läuft wie vorgesehen, der Use Case gelingt. Die Schritte werden einfach durchnummeriert (1., 2., 3., …).

| Nr. | Schritt |
|---|---|
| 1. | |
| 2. | |
| 3. | |

### Alternativszenario (optional)

Abweichungen, bei denen der Use Case trotzdem gelingt, nur auf einem anderen Weg. Am Ende steht, wo der Ablauf im Hauptszenario weitergeht.

| Nr. | Schritt |
|---|---|
| 2a | *Bedingung der Abweichung („Wenn …")* |
| 2a1 | |
| 2a2 | Weiter bei Schritt 3. |

### Ausnahmeszenario (optional)

Abweichungen, bei denen der Use Case scheitert. Mit **NB** (nota bene) wird festgehalten, was stattdessen nach dem Ablauf gilt, denn die Nachbedingung des Hauptszenarios trifft dann nicht zu.

| Nr. | Schritt |
|---|---|
| 3a | *Bedingung der Abweichung („Wenn …")* |
| 3a1 | |
| 3a2 | NB: *Was gilt nach dem Abbruch?* |

### Verankerung: Hierarchische Dezimalpunktnotation

Alternativ- und Ausnahmeszenarien hängen immer an einer Stelle des Hauptszenarios.

- Die **Zahl** nennt den Schritt des Hauptszenarios, an dem die Abweichung auftritt (z. B. 4).
- Der **Buchstabe** unterscheidet mehrere Abweichungen am selben Schritt (4a, 4b, …). Er läuft über Alternativ- und Ausnahmeszenario hinweg, damit jede Nummer nur einmal vorkommt.
- Eine **weitere Zahl** nummeriert die Teilschritte innerhalb der Abweichung (4a1, 4a2, …).

### Tipps zum Ausfüllen

- Die Vorbedingung nennt nur das, was das System beim Start annehmen darf. Was erst im Ablauf kontrolliert wird (z. B. „liegt eine Erklärung vor?"), gehört als Prüfschritt in das Hauptszenario, nicht in die Vorbedingung.
- Jeder Schritt hat genau einen Handelnden (Akteur oder System) und eine Handlung.
- Jede Abweichung braucht ein Ende: entweder „Weiter bei Schritt n" (Alternative) oder einen NB-Satz (Ausnahme).

---

## Teil 2: Beispiel „Voraussetzungen prüfen"

Dieser Use Case ist im Use-Case-Diagramm per «include» an „Zuwendungsbestätigung ausstellen" angebunden. Er enthält die steuerlichen Voraussetzungen: Was muss vorliegen, damit eine Zuwendungsbestätigung überhaupt ausgestellt werden darf?

### Header

| Feld | Eintrag |
|---|---|
| Name | Voraussetzungen prüfen |
| Auslösender Akteur | Kassenwart (über den Use Case „Zuwendungsbestätigung ausstellen") |
| Weitere Akteure | keine. Das Tool prüft selbständig. Finanzamt und Zuwendender wirken nicht mit. |
| Auslöser | Der Kassenwart startet für eine erfasste Geldzuwendung die Ausstellung einer Zuwendungsbestätigung. |
| Vorbedingung | (1) Der Verein ist als Zuwendungsempfänger im Tool angelegt. (2) Die Geldzuwendung ist erfasst (Zuwendender, Betrag, Tag der Zuwendung, Zuwendungsart). (3) Der Kassenwart hat den zu bestätigenden steuerbegünstigten Zweck gewählt. |
| Nachbedingung | Das Prüfergebnis „bestanden" ist der Geldzuwendung zugeordnet. Die Ausstellung der Zuwendungsbestätigung kann fortgesetzt werden. |

### Hauptszenario (Happy Path)

| Nr. | Schritt |
|---|---|
| 1. | Das Tool ermittelt den Freistellungsbescheid (bzw. die Anlage zum Körperschaftsteuerbescheid) des Vereins. |
| 2. | Das Tool prüft, dass der Bescheid nicht zu alt ist: Der Freistellungsbescheid darf höchstens 5 Jahre alt sein (gerechnet bis zum Tag der Ausstellung). |
| 3. | Das Tool prüft, dass der gewählte Zweck durch den Bescheid und die Satzung des Vereins gedeckt ist. |
| 4. | Das Tool prüft, dass es sich um eine Geldzuwendung handelt und dass für sie noch keine Zuwendungsbestätigung existiert. |
| 5. | Das Tool prüft, dass eine Gegenleistungsverzichtserklärung zu dieser Geldzuwendung vorliegt. |
| 6. | Das Tool prüft, dass der Betrag 300 € übersteigt, sodass der vereinfachte Zuwendungsnachweis nicht genügt und eine förmliche Zuwendungsbestätigung nötig ist. |
| 7. | Das Tool prüft, dass der Abzug für die Zuwendungsart nicht ausgeschlossen ist (Spende: zulässig; Mitgliedsbeitrag: nur, wenn der Zweck des Vereins den Abzug nicht ausschließt). |
| 8. | Das Tool speichert das Prüfergebnis „bestanden" bei der Geldzuwendung und meldet es an „Zuwendungsbestätigung ausstellen". |

### Alternativszenario

| Nr. | Schritt |
|---|---|
| 1a | *Der Verein hat keinen Freistellungsbescheid, aber einen Feststellungsbescheid nach § 60a AO.* |
| 1a1 | Das Tool verwendet den Feststellungsbescheid. |
| 1a2 | Weiter bei Schritt 2. Hier gilt eine Höchstdauer von 3 Jahren statt 5. |
| 6a | *Der Betrag beträgt höchstens 300 €.* |
| 6a1 | Das Tool weist darauf hin, dass für diesen Betrag der vereinfachte Zuwendungsnachweis (z. B. Kontoauszug) genügt. |
| 6a2 | Der Kassenwart bestätigt, dass trotzdem eine Zuwendungsbestätigung ausgestellt werden soll. |
| 6a3 | Weiter bei Schritt 7. |
| 7a | *Die Zuwendungsart ist Spende und der Verein fördert Zwecke, bei denen Mitgliedsbeiträge nicht abziehbar sind.* |
| 7a1 | Das Tool merkt die Aussage „kein ausgeschlossener Mitgliedsbeitrag" für die Bestätigung vor. |
| 7a2 | Weiter bei Schritt 8. |

### Ausnahmeszenario

| Nr. | Schritt |
|---|---|
| 1b | *Es ist weder ein Freistellungs- noch ein Feststellungsbescheid erfasst.* |
| 1b1 | Das Tool meldet, dass kein Bescheid vorliegt, und bricht die Prüfung ab. |
| 1b2 | NB: Das Prüfergebnis „nicht bestanden – Bescheid fehlt" ist gespeichert. Es wird keine Zuwendungsbestätigung ausgestellt. |
| 2a | *Der Bescheid ist älter als 5 Jahre (Freistellungsbescheid) bzw. 3 Jahre (Feststellungsbescheid).* |
| 2a1 | Das Tool meldet die Überschreitung und bricht die Prüfung ab. |
| 2a2 | NB: Das Prüfergebnis „nicht bestanden – Bescheid zu alt" ist gespeichert. Es wird keine Zuwendungsbestätigung ausgestellt. Erst ein neuer Bescheid (Gemeinnützigkeitsbescheid erfassen) ermöglicht einen neuen Versuch. |
| 3a | *Der gewählte Zweck ist weder im Bescheid noch in der Satzung des Vereins enthalten.* |
| 3a1 | Das Tool meldet den nicht gedeckten Zweck und bricht die Prüfung ab. |
| 3a2 | NB: Das Prüfergebnis „nicht bestanden – Zweck nicht gedeckt" ist gespeichert. Es wird keine Zuwendungsbestätigung ausgestellt. |
| 4a | *Die Zuwendung ist keine Geldzuwendung (z. B. Sachspende).* |
| 4a1 | Das Tool meldet, dass dieses Formular nur für Geldzuwendungen gilt, und bricht ab. |
| 4a2 | NB: Es wird keine Zuwendungsbestätigung ausgestellt. Sachspenden sind nicht Teil des MVP. |
| 4b | *Für die Geldzuwendung existiert bereits eine Zuwendungsbestätigung.* |
| 4b1 | Das Tool meldet die vorhandene Bestätigung und bricht ab. |
| 4b2 | NB: Der Bestand bleibt unverändert. Es entsteht keine zweite Bestätigung für dieselbe Zuwendung. |
| 5a | *Es liegt keine Gegenleistungsverzichtserklärung vor.* |
| 5a1 | Das Tool meldet die fehlende Erklärung und bricht ab. |
| 5a2 | NB: Das Prüfergebnis „nicht bestanden – Gegenleistungsverzicht fehlt" ist gespeichert. Es wird keine Zuwendungsbestätigung ausgestellt. Nach dem Erfassen der Erklärung ist ein neuer Versuch möglich. |
| 6b | *Der Betrag beträgt höchstens 300 € und der Kassenwart verzichtet auf die Ausstellung.* |
| 6b1 | Das Tool weist auf den vereinfachten Zuwendungsnachweis hin (wie 6a1). |
| 6b2 | Der Kassenwart wählt „keine Zuwendungsbestätigung ausstellen". Das Tool beendet die Prüfung. |
| 6b3 | NB: Das Prüfergebnis „nicht erforderlich" ist gespeichert. Es wird keine Zuwendungsbestätigung ausgestellt. |
| 7b | *Die Zuwendungsart ist Mitgliedsbeitrag und der Abzug ist wegen des Vereinszwecks ausgeschlossen.* |
| 7b1 | Das Tool meldet den Abzugsausschluss und bricht ab. |
| 7b2 | NB: Das Prüfergebnis „nicht bestanden – Mitgliedsbeitrag nicht abziehbar" ist gespeichert. Es wird keine Zuwendungsbestätigung ausgestellt. |

### Hinweise zum Beispiel

1. **Reihenfolge der Prüfschritte:** Zuerst kommen Prüfungen am Verein (Bescheid, Frist, Zweck), dann Prüfungen an der Zuwendung (Geldzuwendung, Erklärung, Betrag, Abzug). Das erleichtert das Lesen: Wer den Ablauf liest, sieht der Reihe nach, was vorliegen muss.
2. **Grenze von 300 €:** Sie gilt seit dem 01.01.2021 (§ 50 Abs. 4 EStDV). Manche Quellen sprechen von „pro Jahr", andere von „je Einzelzuwendung". Vor der Umsetzung gegen den Gesetzestext prüfen.
3. **Frist des Bescheids:** Das vorliegende Formular spricht von der Zeit „seit Ausstellung des Bescheides". Andere Fassungen beziehen sich auf die Ausstellung der Bestätigung. Siehe offene Punkte im Glossar (Abschnitt 5, Punkt 1).
4. **Prüfergebnis:** Der Begriff ist hier informell verwendet. Wenn ihr ihn dauerhaft speichern wollt, gehört er als eigenes Objekt (z. B. Prüfprotokoll) ins Glossar und ins Datenmodell.
5. **Nicht ausgearbeitet:** Verzicht auf Erstattung von Aufwendungen (Aufwandsspende), Sachspenden und Sammelbestätigungen.