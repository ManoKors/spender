package de.ad26.team07.domain.model

data class SteuerbeguenstigterZweck(
    val paragraf: String,
    val bezeichnung: String,
    val istAusschlussZweck: Boolean = false
) {
    init {
        require(bezeichnung.isNotBlank()) { "Zweckbezeichnung darf nicht leer sein" }
    }
}
