# Architekturentscheidungen (Architecture Decision Records)

Dieses Dokument hält wichtige technische und architektonische Entscheidungen, deren Kontext und die getroffene Wahl fest.

---

## ADR 01: Technologiestack Kotlin mit Gradle (Kotlin DSL) und Java 21

* **Status:** Angenommen
* **Datum:** 2026-10-27

### Kontext
Für die Realisierung der Zuwendungsbestätigungs- und Compliance-Software im Rahmen von AD26 wird ein robuster, typsicherer Stack benötigt, der sich ideal für Domain-Driven Design (DDD) eignet und schnelles, automatisiertes Testen (TDD) ermöglicht.

### Entscheidung
Wir verwenden **Kotlin 2.1** auf der **JVM 21 (LTS)** mit **Gradle (Kotlin DSL)** und **JUnit 5**.

### Konsequenzen
* **Vorteile:**
  * Kompakte Syntax für Value Objects (`data class`, `value class`) und geschlossene Vererbungshierarchien (`sealed class`).
  * Hohe Typsicherheit und Null-Safety zur Vermeidung von Laufzeitfehlern bei rechtlich bindenden Dokumenten.
  * Gradle Wrapper sorgt für konsistente Builds auf allen Entwicklungsrechnern ohne Vorinstallation von Gradle.
* **Nachteile:**
  * Teammitglieder müssen grundlegende Kotlin-Kenntnisse besitzen (durch Java-Vorkenntnisse jedoch sehr schneller Einstieg).

---

## ADR 02: Kapselung steuerlicher Fristen im Bescheidwesen als Domänenmethoden

* **Status:** Angenommen
* **Datum:** 2026-10-27

### Kontext
Nach § 63 Abs. 5 AO und § 60a AO haben Freistellungs- und Feststellungsbescheide unterschiedliche Höchstgültigkeitsdauern (5 Jahre bzw. 3 Jahre). Es stellte sich die Frage, ob Ablaufdaten als statische Datenfelder persistiert oder dynamisch durch Methoden berechnet werden sollen.

### Entscheidung
Die Fristen werden als fachliche Invarianten und Methoden (`istGueltigAm(datum: LocalDate): Boolean`) direkt in den spezialisierten Bescheidklassen (`Freistellungsbescheid`, `Feststellungsbescheid`) implementiert und nicht als persistierte Hilfsdatenfelder im Datenmodell abgelegt.

### Konsequenzen
* **Vorteile:**
  * Verhindert Dateninkonsistenzen (keine redundanten "Gültig-Bis"-Felder, die vom Bescheiddatum abweichen könnten).
  * Entspricht exakt der OCL-Spezifikation aus [`fachdomäne/fachdomäneVerein/datenmodell.md`](file:///Users/mano/Desktop/Projekte/AD26-Team07/fachdomäne/fachdomäneVerein/datenmodell.md).
  * Ermöglicht präzise Stichtagsprüfungen für beliebig gewählte Zuwendungs- oder Ausstellungsdaten.
* **Nachteile:**
  * Berechnung erfolgt bei jedem Prüfaufruf (rechnerischer Overhead ist bei Datumsoperationen vernachlässigbar).

---

## ADR 03: Testgetriebene Entwicklung (TDD) für Compliance-Prüfungen

* **Status:** Angenommen
* **Datum:** 2026-10-27

### Kontext
Fehlerhafte Zuwendungsbestätigungen lösen steuerstrafrechtliche Risiken und Falschausstellerhaftung aus. Alle Geschäftsregeln müssen vorab verifiziert werden.

### Entscheidung
Alle Prüfschritte aus der Prozesstabelle ([`fachdomäne/fachdomäneVerein/prozesstabelle.md`](file:///Users/mano/Desktop/Projekte/AD26-Team07/fachdomäne/fachdomäneVerein/prozesstabelle.md)) werden testgetrieben (TDD) implementiert: Zuerst wird der fehlschlagende Unit-Test für Gut- und Ausnahmefälle geschrieben, danach die minimale Domänenlogik implementiert.

### Konsequenzen
* **Vorteile:**
  * Sehr hohe Testabdeckung der steuerlichen Randbedingungen.
  * Nachvollziehbarer Nachweis der Compliance-Funktionen.
