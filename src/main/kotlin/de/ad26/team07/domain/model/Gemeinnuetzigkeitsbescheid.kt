package de.ad26.team07.domain.model

import java.time.LocalDate

/**
 * Abstrakte Basisklasse für Gemeinnützigkeitsbescheide nach Abgabenordnung (AO).
 */
sealed class Gemeinnuetzigkeitsbescheid(
    open val steuernummer: String,
    open val finanzamt: String,
    open val bescheidDatum: LocalDate,
) {
    /**
     * Prüft, ob der Bescheid an einem Stichtag (z. B. Tag der Ausstellung der Zuwendungsbestätigung) noch gültig ist.
     */
    abstract fun istGueltigAm(datum: LocalDate): Boolean
}

/**
 * Körperschaftsteuer-Freistellungsbescheid nach Veranlagung.
 * Fristregel (§ 63 Abs. 5 AO): Gültig höchstens 5 Jahre ab Bescheiddatum.
 */
data class Freistellungsbescheid(
    override val steuernummer: String,
    override val finanzamt: String,
    override val bescheidDatum: LocalDate,
    val letzterVeranlagungszeitraum: Int,
) : Gemeinnuetzigkeitsbescheid(steuernummer, finanzamt, bescheidDatum) {

    override fun istGueltigAm(datum: LocalDate): Boolean {
        val ablaufDatum = bescheidDatum.plusYears(5)
        return !datum.isBefore(bescheidDatum) && !datum.isAfter(ablaufDatum)
    }
}

/**
 * Vorläufiger Feststellungsbescheid nach § 60a AO (z. B. für Neugründungen).
 * Fristregel (§ 60a AO): Gültig höchstens 3 Jahre ab Feststellungsdatum.
 */
data class Feststellungsbescheid(
    override val steuernummer: String,
    override val finanzamt: String,
    override val bescheidDatum: LocalDate,
    val rechtsgrundlage: String = "§ 60a AO",
) : Gemeinnuetzigkeitsbescheid(steuernummer, finanzamt, bescheidDatum) {

    override fun istGueltigAm(datum: LocalDate): Boolean {
        val ablaufDatum = bescheidDatum.plusYears(3)
        return !datum.isBefore(bescheidDatum) && !datum.isAfter(ablaufDatum)
    }
}
