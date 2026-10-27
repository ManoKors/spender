package de.ad26.team07.domain.model

import java.time.LocalDate

data class Gegenleistungsverzichtserklaerung(
    val erklaerungsDatum: LocalDate = LocalDate.now(),
    val istGegenleistungsfrei: Boolean = true,
    val verzichtAufAnspruch: String? = null
) {
    init {
        require(istGegenleistungsfrei) {
            "Die Zuwendung kann nur steuerlich berücksichtigt werden, wenn Gegenleistungsfreiheit bestätigt wird."
        }
    }
}
