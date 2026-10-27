package de.ad26.team07.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Spendenabsicht(
    val id: String,
    val referenzCode: String,
    val betrag: BigDecimal,
    val waehrung: String = "EUR",
    val empfaenger: Zuwendungsempfaenger,
    val spender: Spender,
    val gewidmeterZweck: SteuerbeguenstigterZweck,
    val verzichtserklaerung: Gegenleistungsverzichtserklaerung,
    val giroCodePayload: String,
    val erstellungsDatum: LocalDateTime = LocalDateTime.now(),
    val status: SpendenabsichtStatus = SpendenabsichtStatus.ANGEKUENDIGT
) {
    init {
        require(betrag > BigDecimal.ZERO) { "Der Spendenbetrag muss größer als 0 sein" }
        require(referenzCode.isNotBlank()) { "Der Referenzcode darf nicht leer sein" }
        require(verzichtserklaerung.istGegenleistungsfrei) {
            "Gegenleistungsfreiheit muss zwingend bestätigt sein (OCL Invariante 7)"
        }
    }

    fun markiereAlsEingegangen(): Spendenabsicht {
        check(status == SpendenabsichtStatus.ANGEKUENDIGT) {
            "Spendenabsicht kann nur von ANGEKUENDIGT auf EINGEGANGEN gesetzt werden (aktuell: $status)"
        }
        return copy(status = SpendenabsichtStatus.EINGEGANGEN)
    }
}
