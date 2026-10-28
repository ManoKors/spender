package de.ad26.team07.application.port.out

import de.ad26.team07.domain.model.Spendenabsicht

/**
 * Driven Port für die Persistenz von Spendenabsichten (Pledges).
 */
interface SpendenabsichtRepositoryPort {
    fun speichere(spendenabsicht: Spendenabsicht)
    fun findeNachReferenzCode(referenzCode: String): Spendenabsicht?
    fun findeAlleFuerVerein(vereinSlug: String): List<Spendenabsicht>
}
