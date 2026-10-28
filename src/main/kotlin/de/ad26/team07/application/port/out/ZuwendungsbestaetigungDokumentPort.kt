package de.ad26.team07.application.port.out

import de.ad26.team07.domain.model.Zuwendungsbestaetigung

/**
 * Driven Port zur Erzeugung eines rechtssicheren Zuwendungsbestätigungs-Dokuments nach BMF-Muster 034122.
 */
interface ZuwendungsbestaetigungDokumentPort {
    fun generiereDokument(bestaetigung: Zuwendungsbestaetigung): String
}
