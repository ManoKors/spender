package de.ad26.team07.application.port.out

import de.ad26.team07.domain.model.Zuwendungsempfaenger

/**
 * Driven Port für den Zugriff auf Zuwendungsempfänger (Vereine / Mandanten).
 */
interface VereinRepositoryPort {
    fun findeNachSlug(slug: String): Zuwendungsempfaenger?
    fun speichere(verein: Zuwendungsempfaenger)
}
