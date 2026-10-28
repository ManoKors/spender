package de.ad26.team07.domain.model

import de.ad26.team07.domain.service.ZahlenInWortenKonverter
import java.time.LocalDate

/**
 * Amtliche Zuwendungsbestätigung für Geldzuwendungen/Mitgliedsbeitrag nach BMF-Muster 034122 (Fassung 2013).
 */
data class Zuwendungsbestaetigung(
    val bestaetigungsNummer: String,
    val ausstellungsDatum: LocalDate,
    val ausstellungsOrt: String,
    val geldzuwendung: Geldzuwendung,
    val gemeinnuetzigkeitsbescheid: Gemeinnuetzigkeitsbescheid,
    val gefoerderterZweck: SteuerbeguenstigterZweck,
    val hinweisKeinAusschlussBeitrag: Boolean = false,
    val unterschrift: String = "Vorstand / Kassenwart",
    val betragInWorten: String = ZahlenInWortenKonverter.inWorten(geldzuwendung.betrag)
) {
    init {
        require(bestaetigungsNummer.isNotBlank()) { "Bestätigungsnummer darf nicht leer sein." }
        require(ausstellungsOrt.isNotBlank()) { "Ausstellungsort darf nicht leer sein." }
        require(unterschrift.isNotBlank()) { "Unterschrift darf nicht leer sein." }

        // OCL 3: Bescheid muss zum Ausstellungsdatum gültig sein
        require(pruefeBescheidGueltigkeit()) {
            "Der Gemeinnützigkeitsbescheid (${gemeinnuetzigkeitsbescheid.bescheidDatum}) ist am Ausstellungsdatum ($ausstellungsDatum) nicht mehr gültig (OCL 3)."
        }

        // OCL 4: Zweckkongruenz – Der geförderte Zweck muss in der Vereinssatzung enthalten sein
        require(geldzuwendung.empfaenger.satzungszwecke.any { it.paragraf == gefoerderterZweck.paragraf }) {
            "Der geförderte Zweck '${gefoerderterZweck.bezeichnung}' ist nicht in der Satzung des Empfängers enthalten (OCL 4)."
        }

        // OCL 6: Betrag in Worten muss konsistent sein
        require(betragInWorten.isNotBlank()) {
            "Betrag in Worten darf nicht leer sein (OCL 6)."
        }
    }

    /**
     * Prüft, ob der angegebene Bescheid am Ausstellungsdatum gültig ist (Fristen nach § 63 Abs. 5 AO bzw. § 60a AO).
     */
    fun pruefeBescheidGueltigkeit(): Boolean {
        return gemeinnuetzigkeitsbescheid.istGueltigAm(ausstellungsDatum)
    }

    /**
     * Gibt an, ob für diesen Betrag ein vereinfachter Zuwendungsnachweis (§ 50 Abs. 4 EStDV <= 300 €) genügt hätte.
     */
    val vereinfachterNachweisMoeglich: Boolean
        get() = geldzuwendung.betrag <= java.math.BigDecimal("300.00")
}
