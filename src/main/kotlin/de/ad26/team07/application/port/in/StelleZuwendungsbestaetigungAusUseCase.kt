package de.ad26.team07.application.port.`in`

import de.ad26.team07.domain.model.Zuwendungsbestaetigung

/**
 * Inbound (Driving) Port für die Ausstellung einer amtlichen Zuwendungsbestätigung (UC-4).
 */
interface StelleZuwendungsbestaetigungAusUseCase {
    fun ausstellen(command: StelleZuwendungsbestaetigungAusCommand): Zuwendungsbestaetigung
}
