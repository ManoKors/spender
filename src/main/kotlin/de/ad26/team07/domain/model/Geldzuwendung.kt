package de.ad26.team07.domain.model

import java.math.BigDecimal
import java.time.LocalDate

/**
 * Abstrakte Basisklasse für alle Geldzuwendungen nach amtlichem BMF-Muster 034122.
 */
sealed class Geldzuwendung {
    abstract val betrag: BigDecimal
    abstract val waehrung: String
    abstract val zuwendungsDatum: LocalDate
    abstract val zuwendender: Spender
    abstract val empfaenger: Zuwendungsempfaenger
}

/**
 * Eine Geldspende (reguläre Spende oder Aufwandsspende mit Erstattungsverzicht).
 */
data class Geldspende(
    override val betrag: BigDecimal,
    override val waehrung: String = "EUR",
    override val zuwendungsDatum: LocalDate,
    override val zuwendender: Spender,
    override val empfaenger: Zuwendungsempfaenger,
    val istAufwandsspende: Boolean = false,
    val spendenabsicht: Spendenabsicht? = null,
    val verzichtserklaerung: Gegenleistungsverzichtserklaerung? = null
) : Geldzuwendung() {
    init {
        require(betrag > BigDecimal.ZERO) { "Der Zuwendungsbetrag muss größer als 0 sein." }

        // OCL 5: Aufwandsspende erfordert zwingend eine Verzichtserklärung
        if (istAufwandsspende) {
            require(verzichtserklaerung != null) {
                "Für eine Aufwandsspende (Verzicht auf Erstattung von Aufwendungen) muss eine Gegenleistungsverzichtserklärung vorliegen (OCL 5)."
            }
        }
        // OCL 8: Wenn die Spende auf einer Spendenabsicht basiert, müssen Betrag, Empfänger und Status übereinstimmen
        if (spendenabsicht != null) {
            require(betrag.compareTo(spendenabsicht.betrag) == 0) {
                "Spendenbetrag ($betrag) muss mit dem Betrag der Vorankündigung (${spendenabsicht.betrag}) übereinstimmen."
            }
            require(empfaenger.whiteLabelKonfiguration.slug == spendenabsicht.empfaenger.whiteLabelKonfiguration.slug) {
                "Zuwendungsempfänger muss mit dem Empfänger der Vorankündigung übereinstimmen."
            }
            require(spendenabsicht.status == SpendenabsichtStatus.EINGEGANGEN) {
                "Die zugrundeliegende Spendenabsicht muss den Status EINGEGANGEN haben (aktuell: ${spendenabsicht.status})."
            }
        }
    }
}

/**
 * Ein Mitgliedsbeitrag an den Verein.
 */
data class Mitgliedsbeitrag(
    override val betrag: BigDecimal,
    override val waehrung: String = "EUR",
    override val zuwendungsDatum: LocalDate,
    override val zuwendender: Spender,
    override val empfaenger: Zuwendungsempfaenger,
    val beitragsJahr: Int
) : Geldzuwendung() {
    init {
        require(betrag > BigDecimal.ZERO) { "Der Zuwendungsbetrag muss größer als 0 sein." }
        require(beitragsJahr in 1900..2100) { "Ungültiges Beitragsjahr: $beitragsJahr" }
    }
}
