package de.ad26.team07.application.port.`in`

import java.time.LocalDate

data class StelleZuwendungsbestaetigungAusCommand(
    val referenzCode: String,
    val ausstellungsOrt: String,
    val ausstellungsDatum: LocalDate = LocalDate.now(),
    val unterschrift: String = "Vorstand / Kassenwart"
)
