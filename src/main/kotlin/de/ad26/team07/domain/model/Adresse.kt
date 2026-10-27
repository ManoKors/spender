package de.ad26.team07.domain.model

data class Adresse(
    val strasse: String,
    val hausnummer: String,
    val plz: String,
    val ort: String,
    val land: String = "DE"
) {
    init {
        require(strasse.isNotBlank()) { "Straße darf nicht leer sein" }
        require(hausnummer.isNotBlank()) { "Hausnummer darf nicht leer sein" }
        require(plz.isNotBlank()) { "PLZ darf nicht leer sein" }
        require(ort.isNotBlank()) { "Ort darf nicht leer sein" }
    }
}
